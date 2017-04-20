package com.artioml.githubclient;

import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReposListActivity extends AppCompatActivity {

    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;

    private GitHubClient mClient;
    private List<Repository> mRepositories;
    private String mUserLogin, mToken;

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

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadReposPage(mUserLogin, page);
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_repos_refresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scrollListener.resetState();
                loadReposPage(mUserLogin, 1);
            }
        });

        mUserLogin = getIntent().getStringExtra("EXTRA_LOGIN");
        mToken = new Credentials(this).getToken();
        mClient = ServiceGenerator.createService(GitHubClient.class);
        loadReposPage(mUserLogin, 1);
    }

    private void loadReposPage(final String userLogin, final int page) {
        mClient.reposForUser(userLogin, page, mToken).enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> resp) {
                if (resp.isSuccessful()) {
                    scrollListener.resetState();
                    if (page == 1) mRepositories.clear();
                    mRepositories.addAll(resp.body());
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    mSwipeRefresh.setRefreshing(false);
                } else {
                    Toast.makeText(ReposListActivity.this,
                            getString(R.string.msg_no_connection), Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadReposPage(userLogin, page);
                        }
                    }, 1000);
                    //loadReposPage(userLogin, page);
                }
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
