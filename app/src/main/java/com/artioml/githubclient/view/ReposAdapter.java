package com.artioml.githubclient.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.artioml.githubclient.entities.Repository;

import java.util.List;

public class ReposAdapter extends RecyclerView.Adapter<ReposAdapter.ViewHolder> {

    private List<Repository> mRepos;
    private Activity mActivity;

    public ReposAdapter(List<Repository> repos, Activity activity) {
        this.mRepos = repos;
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_repos, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( Intent.ACTION_VIEW,
                        Uri.parse(mRepos.get(holder.getAdapterPosition()).getHtmlUrl()));
                mActivity.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Repository repository = mRepos.get(position);
        holder.title.setText(repository.getName());
        if (repository.getDescription() == null)
            holder.description.setVisibility(View.GONE);
        else holder.description.setText(repository.getDescription().toString());
    }

    @Override
    public int getItemCount() {
        return mRepos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text_title);
            description = (TextView) itemView.findViewById(R.id.text_description);
        }
    }
}
