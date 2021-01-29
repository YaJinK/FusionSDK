package com.magata.core.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WebViewActivity extends Activity {

    protected WebView webView = null;
    protected Button backButton = null;
    protected Button closeButton = null;
    protected Toast backTips = null;
    protected TextView text = null;

    protected RelativeLayout relativeLayout = null;//管理WebView布局
    protected LinearLayout linearLayout = null;//管理底部Button布局

    private android.webkit.ValueCallback<Uri> uploadMessage;
    private android.webkit.ValueCallback<Uri[]> uploadMessageAboveL;

    private final int FILE_CHOOSER_RESULT_CODE = 10000;
    private Activity currentActivity = null;
    private boolean isDestroyed = false;

    //弹出图片选择面板
    public void showImageChooser() {
        Log.d("Unity", "onClick: ");

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "选择图片"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("NewApi")
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                Log.d("Unity", dataString);
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                        Log.d("Unity", item.getUri().toString());
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
        final Bundle bundle = getIntent().getBundleExtra("data");
        Log.d("Unity", bundle.getString("postData"));
        Log.d("Unity", bundle.getString("url"));

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initView(bundle.getString("postData"), bundle.getString("url"));
            }
        });

    }

    private WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            text.setVisibility(View.INVISIBLE);
            if (null != webView)
                webView.setVisibility(View.VISIBLE);
            Log.d("Unity", "aaa");
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载\
            text.setVisibility(View.VISIBLE);
            if (null != webView)
                webView.setVisibility(View.INVISIBLE);
        }
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("Unity", "shouldOverrideUrlLoading: ");
            super.shouldOverrideUrlLoading(view, url);

            if (url == null) {
                return  false;
            }

            try {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);
                }
                return true;
            } catch (Exception e){
                return false;
            }
        }
    };

    private WebChromeClient webChromeClient = new WebChromeClient() {
        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            uploadMessage = valueCallback;
            showImageChooser();
        }
        // For Android  >= 3.0
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            uploadMessage = valueCallback;
            showImageChooser();
        }
        //For Android  >= 4.1
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            uploadMessage = valueCallback;
            showImageChooser();
        }
        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            showImageChooser();
            return true;
        }
    };

    private void initView(final String postData, final String url) {
        relativeLayout = new RelativeLayout(this);
        linearLayout = new LinearLayout(this);
        //linearLayout.setVisibility(View.INVISIBLE);
        backButton = new Button(this);
        closeButton = new Button(this);

        relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        //设置linerLayout的相对布局参数
        RelativeLayout.LayoutParams reParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        reParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        linearLayout.setLayoutParams(reParams);

        //设置返回按钮的相对布局参数
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParams.weight = 1;
        backButton.setText("返 回");
        backButton.setBackgroundColor(Color.parseColor("#3E8DFF"));
        backButton.setTextColor(Color.parseColor("#FFFFFF"));
        backButton.setLayoutParams(lParams);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //relativeLayout.removeAllViews();
                if (webView != null) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
//                        if (backTips == null)
//                        {
//                            backTips = Toast.makeText(currentActivity, null, Toast.LENGTH_SHORT);
//                            backTips.setText("已是最后一页！");
//                        }
//                        backTips.show();
                        Destroy();
                    }
                }
            }
        });

        //设置关闭按钮的相对布局参数
//        closeButton.setText("关 闭");
//        closeButton.setBackgroundColor(Color.parseColor("#3E8DFF"));
//        closeButton.setTextColor(Color.parseColor("#FFFFFF"));
//        closeButton.setLayoutParams(lParams);
//        //button.setId(new Integer(2));
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Destroy();
//            }
//        });

        linearLayout.addView(backButton);
//        linearLayout.addView(closeButton);
        relativeLayout.addView(linearLayout);

        relativeLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // TODO Auto-generated method stub
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webView = new WebView(currentActivity);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.getSettings().setDomStorageEnabled(true);          // 这个要加上
                        webView.setWebViewClient(webViewClient);
                        webView.setWebChromeClient(webChromeClient);
                        RelativeLayout.LayoutParams reParams = new RelativeLayout.LayoutParams
                                (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        reParams.bottomMargin = linearLayout.getHeight();
                        webView.setLayoutParams(reParams);
                        relativeLayout.addView(webView);
                        //linearLayout.setVisibility(View.VISIBLE);

                        if ("".equals(postData))
                            webView.loadUrl(url);
                        else
                            webView.postUrl(url, postData.getBytes());
                    }
                }, 500);
                relativeLayout.getViewTreeObserver().removeOnPreDrawListener(this);

                return true;
            }
        });

        text = new TextView(this);
        text.setText("页面加载中,请稍后...");
        reParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        reParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        text.setLayoutParams(reParams);
        relativeLayout.addView(text);
        FrameLayout.LayoutParams mainParams = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        mainParams.gravity=Gravity.TOP|Gravity.LEFT;
        this.addContentView(relativeLayout, mainParams);

    }

    private void Destroy() {
        if (isDestroyed) {
            return;
        }
        isDestroyed = true;
        if (backTips != null) {
            backTips.cancel();
            backTips = null;
        }
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        setResult(1001);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (webView != null) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                Destroy();
            }
        }

    }
}

