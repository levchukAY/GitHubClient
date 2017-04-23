package com.artioml.githubclient;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.artioml.githubclient.entities.UserItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UserItem> mUsers;
    private View.OnClickListener mOnUserClickListener;
    private Activity mActivity;
    private int mIconHeight;

    UserAdapter(Activity activity, View.OnClickListener onUserClickListener) {
        this.mOnUserClickListener = onUserClickListener;
        this.mActivity = activity;

        this.mUsers = new ArrayList<>();

        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mIconHeight = 50 * metrics.densityDpi / 160;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_user, parent, false);
        view.setOnClickListener(mOnUserClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserItem user = mUsers.get(position);
        holder.name.setText(user.getLogin());
        Glide.with(mActivity)
                .load(user.getAvatarUrl())
                .override(mIconHeight, mIconHeight)
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    void addAll(List<UserItem> data) {
        int startIndex = mUsers.size();
        mUsers.addAll(data);
        notifyItemRangeInserted(startIndex, data.size());
    }

    void clear() {
        mUsers.clear();
        notifyDataSetChanged();
        //notifyItemRemoved(0);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView avatar;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.text_item_name);
            avatar = (ImageView) itemView.findViewById(R.id.image_item_avatar);
        }
    }

}
