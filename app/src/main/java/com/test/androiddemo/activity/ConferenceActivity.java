package com.test.androiddemo.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ServiceCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.test.androiddemo.CaptureScreenService;
import com.test.androiddemo.R;
import com.test.androiddemo.ScreenCaptureManager;

public class ConferenceActivity extends AppCompatActivity {

    private Intent service;

    public static void start(Context context) {
        Intent starter = new Intent(context, ConferenceActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference);

    }

    public void start_service(View view) {
        ScreenCaptureManager.getInstance().init(this);
        ScreenCaptureManager.getInstance().setScreenCaptureCallback(new ScreenCaptureManager.ScreenCaptureCallback() {
            @Override
            public void onBitmap(Bitmap bitmap) {
                Log.e("TAG", "bitmap = "+bitmap);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1000) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                service = new Intent(this, CaptureScreenService.class);
                service.putExtra("code", resultCode);
                service.putExtra("data", data);
                startForegroundService(service);
            }else {
                ScreenCaptureManager.getInstance().start(resultCode, data);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(service != null) {
            ScreenCaptureManager.getInstance().stop();
            stopService(service);
            Log.e("TAG", "stopService");
        }
    }
}
