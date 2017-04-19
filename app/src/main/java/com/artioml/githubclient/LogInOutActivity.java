package com.artioml.githubclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.artioml.githubclient.api.ApiConstants;
import com.artioml.githubclient.api.Credentials;
import com.artioml.githubclient.api.GitHubClient;
import com.artioml.githubclient.entities.AccessToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogInOutActivity extends AppCompatActivity {

    private static final String GIT_HUB_URL = "https://github.com/";
    private static final String OAUTH_URL = "login/oauth/authorize";
    private static final String LOGOUT_URL = "logout";

    private Credentials mCredentials;
    private WebView mVebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mCredentials = new Credentials(this);

        mVebView = (WebView)findViewById(R.id.web_view);
        mVebView.getSettings().setJavaScriptEnabled(true);
        mVebView.getSettings().setSaveFormData(true);
        mVebView.setWebViewClient(new CustomWebViewClient());
        mVebView.clearCache(true);
        mVebView.clearFormData();
        mVebView.clearHistory();
        mVebView.clearMatches();
        mVebView.clearSslPreferences();
        mVebView.getSettings().setUseWideViewPort(true);

        if (mCredentials.getToken() == null) startLogin();
        else startLogout();

    }

    public void startLogin() {
        mVebView.loadUrl(Uri.parse(
                GIT_HUB_URL + OAUTH_URL
                        + "?&client_id=" + ApiConstants.CLIENT_ID
                        + "&redirect_uri=" + ApiConstants.CLIENT_CALLBACK + "/auth"
                        + "&scope=gist,user,repo"
        ).toString());
    }

    public void startLogout() {
        mVebView.loadUrl(Uri.parse(GIT_HUB_URL + LOGOUT_URL).toString());
    }

    class CustomWebViewClient extends WebViewClient {

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.compareTo(GIT_HUB_URL) == 0) {
                LogInOutActivity.this.mCredentials.clear();
                LogInOutActivity.this.startLogin();
            }
        }

        public void onPageFinished(WebView paramWebView, String paramString) {

            if (!TextUtils.isEmpty(paramString)
                    && paramString.startsWith(ApiConstants.CLIENT_CALLBACK)) {

                LogInOutActivity.this.mVebView.setVisibility(View.INVISIBLE);
                findViewById(R.id.load_progress).setVisibility(View.VISIBLE);

                Uri localUri = Uri.parse(paramString);
                String code = localUri.getQueryParameter("code");

                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("https://github.com/")
                        .addConverterFactory(GsonConverterFactory.create());
                Retrofit retrofit = builder.build();
                retrofit.create(GitHubClient.class).getAccessToken(
                        ApiConstants.CLIENT_ID,
                        ApiConstants.CLIENT_SECRET,
                        code,
                        ApiConstants.CLIENT_CALLBACK + "/auth"
                ).enqueue(getTokenCallback);
            }
        }

        private Callback<AccessToken> getTokenCallback = new Callback<AccessToken>() {

            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    AccessToken accessToken = response.body();
                    new Credentials(LogInOutActivity.this).putToken(accessToken.getAccessToken());
                    LogInOutActivity.this.startActivity(
                            new Intent(LogInOutActivity.this, MainActivity.class));
                    LogInOutActivity.this.finish();
                } else {
                    Toast.makeText(LogInOutActivity.this,
                            getString(R.string.msg_failed_responce), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Toast.makeText(LogInOutActivity.this,
                        getString(R.string.msg_no_connection), Toast.LENGTH_SHORT).show();
            }
        };

        /*public void onReceivedError(
                WebView paramWebView, WebResourceRequest paramWebResourceRequest,
                WebResourceError paramWebResourceError)  {
            super.onReceivedError(paramWebView, paramWebResourceRequest, paramWebResourceError);
            //dialog.dismiss();
        }

        public boolean shouldOverrideUrlLoading(WebView paramWebView,  WebResourceRequest request) {
            if ((!TextUtils.isEmpty(request.toString())) && (!request.toString().startsWith(ApiConstants.CLIENT_CALLBACK)))
                LogInOutActivity.this.webView.setVisibility(View.GONE);
            paramWebView.loadUrl(request.toString());
            return true;
        }*/
    }

}

