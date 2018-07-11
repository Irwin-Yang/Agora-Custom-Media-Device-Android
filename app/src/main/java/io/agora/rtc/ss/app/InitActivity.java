package io.agora.rtc.ss.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.agora.rtc.ss.app.videoSource.ui.GLFilterActivity;
import io.agora.rtc.ss.app.videoSource.ui.MainActivity;
import io.agora.rtc.ss.app.sharescreen.ui.ShareScreenActivity;

public class InitActivity extends Activity {

    private static final String LOG_TAG = "InitActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_activity);

    }


    public void onOldInterfaceClicked(View view) {
        Intent intent1 = new Intent(InitActivity.this,
                MainActivity.class);
        startActivity(intent1);
    }


    public void onNewInterfaceClicked(View view) {
        Intent intent1 = new Intent(InitActivity.this,
                ShareScreenActivity.class);
        startActivity(intent1);
    }

    public void onTestFilterClick(View view) {
        Intent intent = new Intent(this, GLFilterActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
