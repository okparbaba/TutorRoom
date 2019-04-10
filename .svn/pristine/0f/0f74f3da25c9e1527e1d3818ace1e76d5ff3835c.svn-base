package inc.osbay.android.tutorroom.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.utils.WebViewClientImpl;

public class WebviewFragment extends BackHandledFragment {

    public static String WEBVIEW_EXTRA = "WebviewActivity_EXTRA";
    public static String TITLE_EXTRA = "WebviewActivity_TITLE";
    static WebView webView;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.webview)
    WebView mWebView;
    String url;

    /*public static void onMyKeyDown(int key, KeyEvent event) {
        if ((key == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Title")
                    .setMessage("Do you really want to Exit?")
                    //.setIcon(R.drawable.logo)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> getActivity().finish())
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(WEBVIEW_EXTRA);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_webview, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);
                return true;
            }
        });
        WebSettings websetting = mWebView.getSettings();
        websetting.setJavaScriptEnabled(true);
        websetting.setPluginState(WebSettings.PluginState.ON);
        mWebView.loadUrl(url);
        webView = mWebView;
        return view;
    }

    @Override
    public boolean onBackPressed() {
        getActivity().finish();
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        setTitle(getArguments().getString(TITLE_EXTRA));
        setDisplayHomeAsUpEnable(true);
    }
}