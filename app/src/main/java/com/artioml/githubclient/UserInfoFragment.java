﻿package com.artioml.githubclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.artioml.githubclient.api.Credentials;
import com.artioml.githubclient.api.GitHubClient;
import com.artioml.githubclient.api.ServiceGenerator;
import com.artioml.githubclient.entities.AuthorizedUser;
import com.artioml.githubclient.entities.User;
import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoFragment extends Fragment {

    private ImageView mAvatarImageView;
    private TextView mLoginTextView;
    private TextView mNameTextView;
    private TextView mCompanyTextView;
    private TextView mEmailTextView;
    private TextView mReposTextView;
    private TextView mGistsTextView;
    private TextView mPrivateGistsTextView;
    private TextView mTotalReposTextView;
    private TextView mOwnedReposTextView;
    private ProgressBar mProgressBar;
    private ScrollView mScrollView;
    private View mPrivatePanel;

    private GitHubClient mClient;
    private User mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        mAvatarImageView = (ImageView) view.findViewById(R.id.image_user_avatar);
        mLoginTextView = (TextView) view.findViewById(R.id.text_user_login);
        mNameTextView = (TextView) view.findViewById(R.id.text_user_name);
        mCompanyTextView = (TextView) view.findViewById(R.id.text_user_company);
        mEmailTextView = (TextView) view.findViewById(R.id.text_user_email);
        mReposTextView = (TextView) view.findViewById(R.id.text_repos);
        mGistsTextView = (TextView) view.findViewById(R.id.text_gists);
        mPrivateGistsTextView = (TextView) view.findViewById(R.id.text_privte_gists);
        mTotalReposTextView = (TextView) view.findViewById(R.id.text_total_repos);
        mOwnedReposTextView = (TextView) view.findViewById(R.id.text_owned_repos);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_user);
        mScrollView = (ScrollView) view.findViewById((R.id.view_scroll));
        mPrivatePanel = view.findViewById(R.id.panel_private);

        view.findViewById(R.id.button_repos).setOnClickListener(onReposClickListener);
        view.findViewById(R.id.button_repeat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                showUser();
            }
        });

        mScrollView.setVisibility(View.GONE);

        mClient = ServiceGenerator.createService(GitHubClient.class);
        showUser();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getArguments() == null) {
            inflater.inflate(R.menu.menu_edit_fragment, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                if (mUser != null) {
                    Intent editUserIntent = new Intent(getActivity(), EditUserActivity.class);
                    editUserIntent.putExtra("EXTRA_NAME", mUser.getName());
                    editUserIntent.putExtra("EXTRA_COMPANY", mUser.getCompany());
                    editUserIntent.putExtra("EXTRA_BLOG", mUser.getBlog());
                    editUserIntent.putExtra("EXTRA_LOCATION", mUser.getLocation());
                    editUserIntent.putExtra("EXTRA_BIO", mUser.getBio());
                    editUserIntent.putExtra("EXTRA_EMAIL", mUser.getEmail());
                    editUserIntent.putExtra("EXTRA_HIREABLE", mUser.getHireable());
                    startActivity(editUserIntent);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showUser() {
        if (getArguments() == null) {
            showAuthorizedUser();
        } else {
            showUserByLogin(getArguments().getString("ARGUMENT_LOGIN"));
        }
    }

    private void showAuthorizedUser() {
        String token = new Credentials(getActivity()).getToken();
        mClient.getAuthorizedUser(token).enqueue(new Callback<AuthorizedUser>() {

            @Override
            public void onResponse(Call<AuthorizedUser> call, Response<AuthorizedUser> response) {
                if (response.isSuccessful()) {
                    AuthorizedUser user = response.body();
                    showUserInfo(user);
                    mUser = user;

                    mPrivateGistsTextView.setText(user.getPrivateGists() + "");
                    mTotalReposTextView.setText(user.getTotalPrivateRepos() + "");
                    mOwnedReposTextView.setText(user.getOwnedPrivateRepos() + "");
                } else {
                    new Credentials(UserInfoFragment.this.getActivity()).putToken(null);
                    startActivity(new Intent(getActivity(), LogInOutActivity.class));
                }
            }

            @Override
            public void onFailure(Call<AuthorizedUser> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showUserByLogin(final String login) {
        mClient.getUser(login).enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    mUser = response.body();
                    showUserInfo(mUser);

                    mPrivatePanel.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getActivity(),
                            getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showUserInfo(User user) {

        mProgressBar.setVisibility(View.GONE);
        mScrollView.setVisibility(View.VISIBLE);

        mLoginTextView.setText(user.getLogin());
        mNameTextView.setText(user.getName());
        mCompanyTextView.setText(user.getCompany());
        mEmailTextView.setText(user.getEmail());
        mReposTextView.setText(user.getPublicRepos() + "");
        mGistsTextView.setText(user.getPublicGists() + "");

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int iconHeight = 100 * metrics.densityDpi / 160;

        Glide.with(this)
                .load(user.getAvatarUrl())
                .override(iconHeight, iconHeight)
                .into(mAvatarImageView);

    }

    private View.OnClickListener onReposClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mUser != null) {
                Intent reposListIntent = new Intent(getActivity(), ReposListActivity.class);
                reposListIntent.putExtra("EXTRA_LOGIN", mUser.getLogin());
                reposListIntent.putExtra("EXTRA_REPOS_COUNT", mUser.getPublicRepos());
                startActivity(reposListIntent);
            }
        }
    };

}

/*----------
помеченные операции
update profile
MVP
*/
