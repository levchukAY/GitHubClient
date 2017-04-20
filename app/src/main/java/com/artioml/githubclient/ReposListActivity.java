package com.artioml.githubclient;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.artioml.githubclient.api.Credentials;
import com.artioml.githubclient.api.GitHubClient;
import com.artioml.githubclient.api.ServiceGenerator;
import com.artioml.githubclient.entities.Repository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReposListActivity extends AppCompatActivity {

    private EndlessScrollListener mScrollListener;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;

    private GitHubClient mClient;
    private List<Repository> mRepositories;
    private String mUserLogin, mToken;
    private int mReposCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repos_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRepositories = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.view_repos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        ReposAdapter adapter = new ReposAdapter(mRepositories, this);
        mRecyclerView.setAdapter(adapter);

        mScrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (mRepositories.size() < mReposCount)
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
        mReposCount = getIntent().getIntExtra("EXTRA_REPOS_COUNT", 0);
        mToken = new Credentials(this).getToken();
        mClient = ServiceGenerator.createService(GitHubClient.class);
        loadReposPage(mUserLogin, 1);
    }

    private void loadReposPage(final String userLogin, final int page) {
        mClient.reposForUser(userLogin, page, mToken).enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> resp) {
                if (resp.isSuccessful()) {
                    if (page == 1) mRepositories.clear();
                    mRepositories.addAll(resp.body());
                    mRecyclerView.getAdapter().notifyDataSetChanged();
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
        });
    }

}
