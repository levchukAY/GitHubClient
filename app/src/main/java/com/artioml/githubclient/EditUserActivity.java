package com.artioml.githubclient;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.artioml.githubclient.api.Credentials;
import com.artioml.githubclient.api.GitHubClient;
import com.artioml.githubclient.api.ServiceGenerator;
import com.artioml.githubclient.entities.AuthorizedUser;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
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

        Map<String, String> userPart = new HashMap<>();
        userPart.put("name", mNameEdit.getEditText().getText().toString());
        userPart.put("company", mCompanyEdit.getEditText().getText().toString());
        //userPart.put("bio", "newName");
        userPart.put("email", mEmailEdit.getEditText().getText().toString());
        //userPart.put("hireable", "false");
        //userPart.put("location", "false");
        //userPart.put("blog", "newCompany");
        //UserPart part = new UserPart("rtfy", "artiom77@tut.by", "vygbh", "ytg", "cfgvh", "true", "dctfvgybh");
        mClient.updateUser(mToken, userPart ).enqueue(new Callback<AuthorizedUser>() {

            @Override
            public void onResponse(Call<AuthorizedUser> call, Response<AuthorizedUser> response) {
                if (response.isSuccessful()) {
                    AuthorizedUser user = response.body();
                    mNameEdit.getEditText().setText(user.getName());
                    mCompanyEdit.getEditText().setText(user.getCompany());
                    mEmailEdit.getEditText().setText(user.getEmail());
                    Toast.makeText(EditUserActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(EditUserActivity.this,
                        "Cannot update", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<AuthorizedUser> call, Throwable t) {
                Toast.makeText(EditUserActivity.this,
                getString(R.string.msg_no_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
