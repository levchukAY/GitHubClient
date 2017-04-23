package com.artioml.githubclient;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.artioml.githubclient.api.Credentials;
import com.artioml.githubclient.api.GitHubClient;
import com.artioml.githubclient.api.ServiceGenerator;
import com.artioml.githubclient.entities.AuthorizedUser;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {

    private TextInputLayout mNameEdit;
    private TextInputLayout mCompanyEdit;
    private TextInputLayout mEmailEdit;

    private GitHubClient mClient;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNameEdit = (TextInputLayout) findViewById(R.id.text_input_name);
        mCompanyEdit = (TextInputLayout) findViewById(R.id.text_input_company);
        mEmailEdit = (TextInputLayout) findViewById(R.id.text_input_email);

        mNameEdit.getEditText().setText(getIntent().getStringExtra("EXTRA_NAME"));
        mCompanyEdit.getEditText().setText(getIntent().getStringExtra("EXTRA_COMPANY"));
        mEmailEdit.getEditText().setText(getIntent().getStringExtra("EXTRA_EMAIL"));

        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InputMethodManager) EditUserActivity.this.getSystemService(
                        Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                        view.getWindowToken(), 0);
                updateUser();
            }
        });

        mClient = ServiceGenerator.createService(GitHubClient.class);
        mToken = new Credentials(this).getToken();
    }

    private void updateUser() {

        /*JSONObject infoRequestBody = new JSONObject();
        try {
            infoRequestBody.put("name", mNameEdit.getEditText().getText().toString());
            infoRequestBody.put("email", mEmailEdit.getEditText().getText().toString());
            infoRequestBody.put("blog", getIntent().getStringExtra("EXTRA_BLOG"));
            infoRequestBody.put("company", mCompanyEdit.getEditText().getText().toString());
            infoRequestBody.put("location", getIntent().getStringExtra("EXTRA_LOCATION"));
            infoRequestBody.put("hireable", getIntent().getBooleanExtra("EXTRA_HIREABLE", false));
            infoRequestBody.put("bio", getIntent().getStringExtra("EXTRA_BIO"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UpdateData updateData = new UpdateData(
                mNameEdit.getEditText().getText().toString(),
                mEmailEdit.getEditText().getText().toString(),
                getIntent().getStringExtra("EXTRA_BLOG"),
                mCompanyEdit.getEditText().getText().toString(),
                getIntent().getStringExtra("EXTRA_LOCATION"),
                getIntent().getBooleanExtra("EXTRA_HIREABLE", false),
                getIntent().getStringExtra("EXTRA_BIO"));*/

        Map<String, String> userPart = new HashMap<>();
        userPart.put("name", mNameEdit.getEditText().getText().toString());
        userPart.put("email", mEmailEdit.getEditText().getText().toString());
        userPart.put("blog", getIntent().getStringExtra("EXTRA_BLOG"));
        userPart.put("company", mCompanyEdit.getEditText().getText().toString());
        userPart.put("location", getIntent().getStringExtra("EXTRA_LOCATION"));
        userPart.put("hireable", getIntent().getBooleanExtra("EXTRA_HIREABLE", true) + "");
        userPart.put("bio", getIntent().getStringExtra("EXTRA_BIO"));

        mClient.updateUser(mToken, userPart).enqueue(new Callback<AuthorizedUser>() {

            @Override
            public void onResponse(Call<AuthorizedUser> call, Response<AuthorizedUser> response) {
                if (response.isSuccessful()) {
                    AuthorizedUser user = response.body();
                    mNameEdit.getEditText().setText(user.getName());
                    mCompanyEdit.getEditText().setText(user.getCompany());
                    mEmailEdit.getEditText().setText(user.getEmail());
                    Toast.makeText(EditUserActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    /*Log.d("EDIT", response.toString());
                    String jsonObject = response.toString();
                    Log.d("EDIT", jsonObject);*/
                } else {
                    Toast.makeText(EditUserActivity.this,
                            getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthorizedUser> call, Throwable t) {
                Toast.makeText(EditUserActivity.this,
                getString(R.string.msg_no_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
