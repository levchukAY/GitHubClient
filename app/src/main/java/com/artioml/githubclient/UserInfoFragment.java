package com.artioml.githubclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.artioml.githubclient.entities.Repository;
import com.artioml.githubclient.entities.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoFragment extends Fragment {

    private ImageView mAvatarImageView;
    private TextView mLoginTextView;
    private TextView mLoctionTextView;
    private TextView mNameTextView;
    private TextView mCompanyTextView;
    private TextView mEmailTextView;
    private TextView mFollowersTextView;
    private TextView mFollowingTextView;
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
    private String mToken;
    private RecyclerView mRecyclerView;

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
        mFollowersTextView = (TextView) view.findViewById(R.id.text_user_followers);
        mFollowingTextView = (TextView) view.findViewById(R.id.text_user_following);
        mReposTextView = (TextView) view.findViewById(R.id.text_repos);
        mGistsTextView = (TextView) view.findViewById(R.id.text_gists);
        mPrivateGistsTextView = (TextView) view.findViewById(R.id.text_privte_gists);
        mTotalReposTextView = (TextView) view.findViewById(R.id.text_total_repos);
        mOwnedReposTextView = (TextView) view.findViewById(R.id.text_owned_repos);
        mLoctionTextView = (TextView) view.findViewById(R.id.text_user_location);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_user);
        mScrollView = (ScrollView) view.findViewById((R.id.view_scroll));
        mPrivatePanel = view.findViewById(R.id.panel_private);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.view_repos);

        view.findViewById(R.id.text_next).setOnClickListener(onReposClickListener);
        view.findViewById(R.id.button_repeat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                showUser();
            }
        });

        mScrollView.setVisibility(View.GONE);

        mClient = ServiceGenerator.createService(GitHubClient.class);
        mToken = new Credentials(getActivity()).getToken();
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
        mClient.getAuthorizedUser(mToken).enqueue(new Callback<AuthorizedUser>() {

            @Override
            public void onResponse(Call<AuthorizedUser> call, Response<AuthorizedUser> response) {
                if (response.isSuccessful()) {
                    AuthorizedUser user = response.body();
                    showUserInfo(user);
                    mUser = user;

                    mPrivateGistsTextView.setText(user.getPrivateGists() + "");
                    mTotalReposTextView.setText(user.getTotalPrivateRepos() + "");
                    mOwnedReposTextView.setText(user.getOwnedPrivateRepos() + "");

                    loadUserRepos(user.getLogin());
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
                    loadUserRepos(mUser.getLogin());
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

    private void loadUserRepos(final String userLogin) {
        mClient.reposForUser(userLogin, 1, mToken).enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> resp) {
                if (resp.isSuccessful()) {
                    if (resp.body().size() == 0) {
                        getActivity().findViewById(R.id.text_next).setVisibility(View.GONE);
                    } else {
                        getActivity().findViewById(R.id.text_no_repos).setVisibility(View.GONE);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mRecyclerView.setAdapter(new ReposAdapter(getActivity(),
                                resp.body().subList(0, Math.min(resp.body().size(), 5)), 1));
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(),
                            getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
                    getActivity().findViewById(R.id.text_next).setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                Toast.makeText(getActivity(),
                        getString(R.string.msg_no_connection), Toast.LENGTH_SHORT).show();
                getActivity().findViewById(R.id.text_next).setVisibility(View.GONE);
            }
        });
    }

    private void showUserInfo(User user) {

        mProgressBar.setVisibility(View.GONE);
        mScrollView.setVisibility(View.VISIBLE);

        mLoginTextView.setText(user.getLogin());
        if (user.getName() != null) {
            mNameTextView.setText(user.getName());
            mNameTextView.setVisibility(View.VISIBLE);
        }
        if (user.getCompany() != null) {
            mCompanyTextView.setText(user.getCompany());
            mCompanyTextView.setVisibility(View.VISIBLE);
        }
        if (user.getEmail() != null) {
            mEmailTextView.setText(user.getEmail());
            mEmailTextView.setVisibility(View.VISIBLE);
        }
        if (user.getLocation() != null) {
            mLoctionTextView.setText(user.getLocation());
            mLoctionTextView.setVisibility(View.VISIBLE);
        }

        mReposTextView.setText(user.getPublicRepos() + "");
        mGistsTextView.setText(user.getPublicGists() + "");
        mFollowersTextView.setText(user.getFollowers() + "");
        mFollowingTextView.setText(user.getFollowing() + "");

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int iconHeight = 100 * metrics.densityDpi / 160;
        final int cornerRadius = 16 * metrics.densityDpi / 160;

        /*Glide.with(this)
                .load(user.getAvatarUrl())
                .override(iconHeight, iconHeight)
                .into(mAvatarImageView);*/

        Glide.with(this)
                .load(user.getAvatarUrl())
                .asBitmap()
                .override(iconHeight, iconHeight)
                .into(new BitmapImageViewTarget(mAvatarImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(cornerRadius);
                        mAvatarImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });

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
?????????? ????????
update profile
MVP
*/
