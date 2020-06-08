package com.test.androiddemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import java.nio.ByteBuffer;

@TargetApi(21)
public class ScreenCaptureManager {
    public final String TAG = this.getClass().getSimpleName();
    public static final int RECORD_REQUEST_CODE = 1000;
    private static ScreenCaptureManager instance;
    private Activity activity;
    private ScreenCaptureManager.ScreenCaptureCallback screenCaptureCallback;
    public ScreenCaptureManager.State state;
    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    private int resultCode;
    private Intent intent;
    private boolean needRestart;
    private long oldTime;
    private int fps;

    private ScreenCaptureManager() {
        this.state = ScreenCaptureManager.State.IDLE;
        this.resultCode = 0;
        this.needRestart = false;
        this.oldTime = 0L;
        this.fps = 15;
    }

    public static ScreenCaptureManager getInstance() {
        if (instance == null) {
            instance = new ScreenCaptureManager();
        }

        return instance;
    }

    public void init(Activity activity) {
        this.activity = activity;
        this.projectionManager = (MediaProjectionManager)activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (this.projectionManager != null && this.state == ScreenCaptureManager.State.IDLE) {
            activity.startActivityForResult(this.projectionManager.createScreenCaptureIntent(), 1000);
            this.state = ScreenCaptureManager.State.RUNNING;
        }

    }

    public void init() {
        if (this.mediaProjection != null) {
            this.mediaProjection.stop();
        }

        if (this.virtualDisplay != null) {
            this.virtualDisplay.release();
        }

        if (this.imageReader != null) {
            this.imageReader.setOnImageAvailableListener((ImageReader.OnImageAvailableListener)null, (Handler)null);
        }

        this.state = ScreenCaptureManager.State.IDLE;
        this.activity = null;
    }

    public void stop() {
        if (this.mediaProjection != null) {
            this.mediaProjection.stop();
        }

    }

    public void start(int resultCode, Intent intent) {
        Log.i(this.TAG, "start: " + resultCode + " - " + intent.toString());
        if (this.projectionManager != null) {
            this.mediaProjection = this.projectionManager.getMediaProjection(resultCode, intent);
            if (this.mediaProjection != null) {
                this.resultCode = resultCode;
                this.intent = intent;
                Log.i(this.TAG, "start: resultCode: " + resultCode + " - intent: " + intent);
                this.initVirtualDisplay(this.activity);
                this.mediaProjection.registerCallback(new ScreenCaptureManager.MediaProjectionStopCallback(), (Handler)null);
            }

            Log.d(this.TAG, "Capture screen start success!");
        } else {
            Log.d(this.TAG, "Capture screen start failed! ProjectionManager is null");
        }

    }

    public void start(MediaProjection mediaProjection) {
        this.mediaProjection = mediaProjection;
        if (this.projectionManager != null) {
            this.initVirtualDisplay(this.activity);
            this.mediaProjection.registerCallback(new ScreenCaptureManager.MediaProjectionStopCallback(), (Handler)null);
        }
    }

    public void restart() {
        if (this.resultCode != 0 && this.intent != null) {
            this.stop();
            Log.i(this.TAG, "restart: ");
            this.needRestart = true;
        } else {
            Log.i(this.TAG, "restart: No screen capture started before.");
        }
    }

    public void initVirtualDisplay(Activity activity) {
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int density = metrics.densityDpi;
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getRealSize(size);
        int width = size.x;
        int height = size.y;
        this.imageReader = ImageReader.newInstance(width, height, 1, 1);
        this.imageReader.setOnImageAvailableListener(new ScreenCaptureManager.ImageAvailableListener(), (Handler)null);
        String virtualDisplayName = "Screenshot";
        int flags = 9;
        this.virtualDisplay = this.mediaProjection.createVirtualDisplay(virtualDisplayName, width, height, density, flags, this.imageReader.getSurface(), (VirtualDisplay.Callback)null, (Handler)null);
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public void setScreenCaptureCallback(ScreenCaptureManager.ScreenCaptureCallback callback) {
        this.screenCaptureCallback = callback;
    }

    public static enum State {
        IDLE,
        RUNNING;

        private State() {
        }
    }

    public interface ScreenCaptureCallback {
        void onBitmap(Bitmap var1);
    }

    private class MediaProjectionStopCallback extends android.media.projection.MediaProjection.Callback {
        private MediaProjectionStopCallback() {
        }

        public void onStop() {
            Log.i(ScreenCaptureManager.this.TAG, "onStop: ");
            if (ScreenCaptureManager.this.virtualDisplay != null) {
                ScreenCaptureManager.this.virtualDisplay.release();
            }

            if (ScreenCaptureManager.this.imageReader != null) {
                ScreenCaptureManager.this.imageReader.setOnImageAvailableListener((ImageReader.OnImageAvailableListener)null, (Handler)null);
            }

            ScreenCaptureManager.this.mediaProjection.unregisterCallback(this);
            if (ScreenCaptureManager.this.needRestart) {
                ScreenCaptureManager.this.needRestart = false;
                ScreenCaptureManager.this.start(ScreenCaptureManager.this.resultCode, ScreenCaptureManager.this.intent);
            } else {
                ScreenCaptureManager.this.state = ScreenCaptureManager.State.IDLE;
                ScreenCaptureManager.this.activity = null;
            }

        }
    }

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        private ImageAvailableListener() {
        }

        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireNextImage();
            long currTime = System.currentTimeMillis();
            if (currTime - ScreenCaptureManager.this.oldTime > (long)(1000 / ScreenCaptureManager.this.fps)) {
                Log.d(ScreenCaptureManager.this.TAG, "capture next");
                Bitmap bitmap = null;
                if (image != null) {
                    ScreenCaptureManager.this.oldTime = currTime;
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int width = image.getWidth();
                    int height = image.getHeight();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int padding = (rowStride - pixelStride * width) / pixelStride;
                    bitmap = Bitmap.createBitmap(width + padding, height, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    if (ScreenCaptureManager.this.screenCaptureCallback != null) {
                        ScreenCaptureManager.this.screenCaptureCallback.onBitmap(bitmap);
                    }
                }
            }

            if (image != null) {
                image.close();
            }

        }
    }
}