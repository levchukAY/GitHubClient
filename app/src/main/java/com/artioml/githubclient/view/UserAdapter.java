package com.artioml.githubclient.view;

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

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UserItem> mUers;
    private View.OnClickListener mOnUserClickListener;
    private Activity mActivity;
    private int mIconHeight;

    public UserAdapter(Activity activity, List<UserItem> repos,
                       View.OnClickListener onUserClickListener) {
        this.mUers = repos;
        this.mOnUserClickListener = onUserClickListener;
        this.mActivity = activity;

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
        UserItem user = mUers.get(position);
        holder.name.setText(user.getLogin());
        Glide.with(mActivity)
                .load(user.getAvatarUrl())
                .override(mIconHeight, mIconHeight)
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return mUers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView avatar;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.text_item_name);
            avatar = (ImageView) itemView.findViewById(R.id.image_item_avatar);
        }
    }

}
