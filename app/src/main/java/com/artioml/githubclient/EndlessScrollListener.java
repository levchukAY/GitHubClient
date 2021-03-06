package com.artioml.githubclient;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private final int VISIBLE_THRESHOLD = 5;
    // Sets the starting page index
    private final int STARTING_PAGE_LISTENER = 1;
    // The current offset index of data you have loaded
    private int mCurrentPage = 1;
    // The total number of items in the dataset after the last load
    private int mPreviousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean mLoading = true;

    private RecyclerView.LayoutManager mLayoutManager;

    EndlessScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        int totalItemCount = mLayoutManager.getItemCount();

        lastVisibleItemPosition =
                    ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();

        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (mLoading && (totalItemCount > mPreviousTotalItemCount)) {
            mLoading = false;
            mPreviousTotalItemCount = totalItemCount;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!mLoading && (lastVisibleItemPosition + VISIBLE_THRESHOLD) > totalItemCount) {
            mCurrentPage++;
            onLoadMore(mCurrentPage, totalItemCount, view);
            mLoading = true;
        }
    }

    // Call whenever performing new searches
    void resetState() {
        this.mCurrentPage = this.STARTING_PAGE_LISTENER;
        this.mPreviousTotalItemCount = 0;
        this.mLoading = true;
    }

    void retry() {
        Log.d(UserListFragment.class.getSimpleName(), "on repeat");
        this.mCurrentPage--;
        this.mLoading = false;
    }

    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);

}
