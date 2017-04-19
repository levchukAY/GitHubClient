package com.artioml.githubclient.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
    private TextInputLayout mTextInputLayout;
    private SwipeRefreshLayout mSwipeRefresh;
    private EndlessRecyclerViewScrollListener mScrollListener;

    private GitHubClient nClient;
    private String mQuery = "Google";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_users_list, container, false);

        mTextInputLayout = (TextInputLayout) view.findViewById(R.id.text_search_user);
        nClient = ServiceGenerator.createService(GitHubClient.class);
        mUsers = new ArrayList<>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.view_users);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new UserAdapter(getActivity(), mUsers, onUserClickListener));
        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
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
                loadUsersPage(mQuery, 1);
            }
        });

        loadUsersPage(mQuery, 1);

        view.findViewById(R.id.button_search_user).setOnClickListener(onSearchUserClickListener);

        return view;
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

    private View.OnClickListener onSearchUserClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    getView().getWindowToken(), 0);

            mScrollListener.resetState();
            String currentQuery = mTextInputLayout.getEditText().getText().toString().trim();
            if (!currentQuery.equals("")) {
                mUsers.clear();
                mQuery = currentQuery;
                loadUsersPage(mQuery, 1);
            }
        }
    };

    private void loadUsersPage(String login, final int page) {
        nClient.searchUsers(login, page).enqueue(new Callback<Users>() {

            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    if (page == 1) mUsers.clear();
                    mUsers.addAll(response.body().getItems());
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                } else {
                    Log.d(UserListFragment.class.getSimpleName(),
                            getString(R.string.msg_failed_responce));
                }
                mSwipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(getActivity(),
                        getString(R.string.msg_no_connection), Toast.LENGTH_SHORT).show();
                mSwipeRefresh.setRefreshing(false);
            }
        });
    }


}
