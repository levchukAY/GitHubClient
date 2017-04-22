package com.artioml.githubclient;

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
    private int mType;

    public ReposAdapter(Activity activity, List<Repository> repos, int type) {
        this.mRepos = repos;
        this.mActivity = activity;
        this.mType = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = mType == 0 ? R.layout.item_repo : R.layout.item_repo_demo;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
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
        if (repository.getLanguage() == null && mType == 0) {
            holder.language.setVisibility(View.GONE);
        } else if (mType == 0){
            holder.language.setText(repository.getLanguage());
        }
        if (repository.getDescription() == null) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setText(repository.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return mRepos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        TextView language;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text_title);
            description = (TextView) itemView.findViewById(R.id.text_description);
            if (mType == 0)
                language = (TextView) itemView.findViewById(R.id.text_language);
        }
    }
}
