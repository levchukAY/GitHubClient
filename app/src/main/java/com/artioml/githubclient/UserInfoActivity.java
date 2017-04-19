package com.artioml.githubclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class UserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserInfoFragment mUserInfoFragment = new UserInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ARGUMENT_LOGIN", getIntent().getStringExtra("EXTRA_LOGIN"));
        mUserInfoFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_user_container, mUserInfoFragment)
                .commit();
    }
}
