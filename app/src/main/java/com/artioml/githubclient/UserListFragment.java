package com.artioml.githubclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.artioml.githubclient.api.GitHubClient;
import com.artioml.githubclient.api.ServiceGenerator;
import com.artioml.githubclient.entities.UserItem;
import com.artioml.githubclient.entities.Users;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListFragment extends Fragment {

    private ArrayList<UserItem> mUsers;
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private SwipeRefreshLayout mSwipeRefresh;
    private EndlessScrollListener mScrollListener;

    private GitHubClient mClient;
    private String mQuery = "Google";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_users_list, container, false);

        mSearchView = (SearchView) view.findViewById(R.id.text_search_user);
        mClient = ServiceGenerator.createService(GitHubClient.class);
        mUsers = new ArrayList<>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.view_users);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new UserAdapter(getActivity(), mUsers, onUserClickListener));
        mScrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadUsersPage(mQuery, page);
            }
        };
        mRecyclerView.addOnScrollListener(mScrollListener);

        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_users_refresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mScrollListener.resetState();
                loadUsersPage(mQuery, 1);
            }
        });

        mSearchView.setOnQueryTextListener(onQueryTextListener);

        loadUsersPage(mQuery, 1);

        return view;
    }

    private void loadUsersPage(final String login, final int page) {

       mClient.searchUsers(login, page).enqueue(new Callback<Users>() {

            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Log.d(UserListFragment.class.getSimpleName(), "code: " + response.code());
                if (response.isSuccessful()) {
                    if (page == 1) {
                        mUsers.clear();
                        if (response.body().getTotalCount() == 0) {
                            mSwipeRefresh.setVisibility(View.GONE);
                        } else mSwipeRefresh.setVisibility(View.VISIBLE);
                    }
                    mUsers.addAll(response.body().getItems());
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    Log.d(UserListFragment.class.getSimpleName(), mUsers.size()
                            + " (" + page + ") " + response.body().getTotalCount());
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScrollListener.retry();
                        }
                    }, 25_000);
                    Log.d(UserListFragment.class.getSimpleName(),
                            getString(R.string.msg_failed_response));
                    Toast.makeText(getActivity(),
                            getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
                    mSwipeRefresh.setVisibility(View.GONE);
                }
                mSwipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                if (UserListFragment.this.isAdded())
                    Toast.makeText(getActivity(),
                            getString(R.string.msg_no_connection), Toast.LENGTH_SHORT).show();
                mSwipeRefresh.setRefreshing(false);
                mSwipeRefresh.setVisibility(View.GONE);
            }
        });
    }

    private View.OnClickListener onUserClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent userInfoIntent = new Intent(getActivity(), UserInfoActivity.class);
            String userLogin =
                    ((TextView) view.findViewById(R.id.text_item_name)).getText().toString();
            userInfoIntent.putExtra("EXTRA_LOGIN", userLogin);
            startActivity(userInfoIntent);
        }
    };

    SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            ((InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    getView().getWindowToken(), 0);

            String currentQuery = mSearchView.getQuery().toString().trim();
            mUsers.clear();
            mScrollListener.resetState();
            if (!currentQuery.equals("")) {
                mQuery = currentQuery;
                loadUsersPage(mQuery, 1);
            }
            mRecyclerView.getAdapter().notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return true;
        }
    };


}
