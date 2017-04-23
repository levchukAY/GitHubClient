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
import com.artioml.githubclient.api.ServiceGenerator;
import com.artioml.githubclient.entities.AccessToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInOutActivity extends AppCompatActivity {

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

        if (mCredentials.getToken() == null)
            startLogin();
        else startLogout();
    }

    public void startLogin() {

        /*ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null)
            new NoConnectionDialogFragment().show(LogInOutActivity.this.getSupportFragmentManager(), "gvh");*/

        mVebView.loadUrl(Uri.parse(
                ApiConstants.GIT_HUB_URL + ApiConstants.OAUTH_URL
                        + "?&client_id=" + ApiConstants.CLIENT_ID
                        + "&redirect_uri=" + ApiConstants.CLIENT_CALLBACK + ApiConstants.AUTH_PART
                        + "&scope=gist,user,repo"
        ).toString());
    }

    public void startLogout() {
        mVebView.loadUrl(Uri.parse(ApiConstants.GIT_HUB_URL + ApiConstants.LOGOUT_PART).toString());
    }

    class CustomWebViewClient extends WebViewClient {

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.compareTo(ApiConstants.GIT_HUB_URL) == 0) {
                LogInOutActivity.this.mCredentials.clear();
                LogInOutActivity.this.startLogin();
            }
        }

        public void onPageFinished(WebView view, String url) {

            if (!TextUtils.isEmpty(url) && url.startsWith(ApiConstants.CLIENT_CALLBACK)) {
                LogInOutActivity.this.mVebView.setVisibility(View.INVISIBLE);
                findViewById(R.id.load_progress).setVisibility(View.VISIBLE);

                Uri localUri = Uri.parse(url);
                String code = localUri.getQueryParameter("code");

                ServiceGenerator.createLoginService(GitHubClient.class).getAccessToken(
                        ApiConstants.CLIENT_ID,
                        ApiConstants.CLIENT_SECRET,
                        code,
                        ApiConstants.CLIENT_CALLBACK + ApiConstants.AUTH_PART
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
                            getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
                    startLogin();
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Toast.makeText(LogInOutActivity.this,
                        getString(R.string.msg_no_connection), Toast.LENGTH_SHORT).show();
            }
        };

    }

}

