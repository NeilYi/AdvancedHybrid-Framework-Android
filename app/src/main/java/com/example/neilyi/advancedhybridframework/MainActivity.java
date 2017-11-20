package com.example.neilyi.advancedhybridframework;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.neilyi.advancedhybridframework.activity.CommonWebActivity;
import com.example.neilyi.advancedhybridframework.base.WebBaseActivity;

public class MainActivity extends WebBaseActivity {
    private static String DEMO_URL = "http://wap.cmread.com";
    // "file:///android_asset/notice01.html"
    private static String JS_DEMO_URL = "http://wap.cmread.com/rbc/t/cps_jsb.jsp?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Button btnOne = findViewById(R.id.btn_one);
        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBrowserActivity(DEMO_URL,false);
            }
        });

        Button btnTwo = findViewById(R.id.btn_two);
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBrowserActivity(DEMO_URL,true);
            }
        });

        Button btnThree = findViewById(R.id.btn_three);
        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBrowserActivity(JS_DEMO_URL,false);
            }
        });
    }

    private void startBrowserActivity(String url, boolean isDisablePtr) {
        Intent intent = new Intent(this, CommonWebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("disableRefresh", isDisablePtr);
        startActivity(intent);
    }
}
