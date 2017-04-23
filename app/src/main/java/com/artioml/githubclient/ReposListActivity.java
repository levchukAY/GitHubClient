package com.artioml.githubclient;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.artioml.githubclient.api.Credentials;
import com.artioml.githubclient.api.GitHubClient;
import com.artioml.githubclient.api.ServiceGenerator;
import com.artioml.githubclient.entities.Repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReposListActivity extends AppCompatActivity {

    private EndlessScrollListener mScrollListener;
    private SwipeRefreshLayout mSwipeRefresh;
    private RepositoriesAdapter mAdapter;

    private GitHubClient mClient;
    private String mUserLogin, mToken;
    private int mRepositoriesCount;
    private boolean isAuthorized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repos_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.view_repos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RepositoriesAdapter(this, 0);
        mRecyclerView.setAdapter(mAdapter);

        mScrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (mAdapter.size() < mRepositoriesCount)
                    loadReposPage(mUserLogin, page);
            }
        };
        mRecyclerView.addOnScrollListener(mScrollListener);

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_repos_refresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mScrollListener.resetState();
                loadReposPage(mUserLogin, 1);
            }
        });

        mUserLogin = getIntent().getStringExtra("EXTRA_LOGIN");
        mRepositoriesCount = getIntent().getIntExtra("EXTRA_REPOS_COUNT", 0);
        isAuthorized = getIntent().getBooleanExtra("EXTRA_IS_AUTHORIZED", false);
        mToken = new Credentials(this).getToken();
        mClient = ServiceGenerator.createService(GitHubClient.class);
        loadReposPage(mUserLogin, 1);
    }

    private void loadReposPage(final String userLogin, final int page) {
        if (isAuthorized)
            mClient.getRepositoriesByLogin(userLogin, page).enqueue(getCallback(page));
        else
            mClient.getOwnedRepositories(userLogin, page, mToken).enqueue(getCallback(page));
    }

    @NonNull
    private Callback<List<Repository>> getCallback(final int page) {
        return new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> resp) {
                if (resp.isSuccessful()) {
                    if (page == 1) mAdapter.clear();
                    mAdapter.addAll(resp.body());
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScrollListener.retry();
                        }
                    }, 25_000);
                    Toast.makeText(ReposListActivity.this,
                            getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
                }
                mSwipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                Toast.makeText(ReposListActivity.this,
                        getString(R.string.msg_no_connection), Toast.LENGTH_SHORT).show();
                mSwipeRefresh.setRefreshing(false);
            }
        };
    }

}
