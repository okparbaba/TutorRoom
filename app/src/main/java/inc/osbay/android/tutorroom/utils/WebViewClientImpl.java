package inc.osbay.android.tutorroom.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;

public class WebViewClientImpl extends WebViewClient {

    private Context context;

    public WebViewClientImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        if (url.contains(CommonConstant.PAYPAL_URL))
            return false;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
        return true;
    }

}