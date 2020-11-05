package com.magata.tencentsdk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.magata.core.util.Util;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int loginLayoutId = Util.getRContent(getPackageName(), "layout", "com_magata_tencentsdk_login");

        this.setContentView(loginLayoutId);

        int qqLoginId = Util.getRContent(getPackageName(), "id", "qq");
        ImageButton qqLoginBtn = findViewById(qqLoginId);
        qqLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 响应事件
                TencentGameSDK.login(1);
                finish();
            }
        });

        int wechatLoginId = Util.getRContent(getPackageName(), "id", "wechat");
        ImageButton wechatLoginBtn = findViewById(wechatLoginId);
        wechatLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 响应事件
                TencentGameSDK.login(2);
                finish();
            }
        });
    }
}
