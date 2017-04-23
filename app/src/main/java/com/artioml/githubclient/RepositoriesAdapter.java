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

import java.util.ArrayList;
import java.util.List;

class RepositoriesAdapter extends RecyclerView.Adapter<RepositoriesAdapter.ViewHolder> {

    private List<Repository> mRepositories;
    private Activity mActivity;
    private int mType;

    RepositoriesAdapter(Activity activity, int type) {
        this.mRepositories = new ArrayList<>();
        this.mActivity = activity;
        this.mType = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = mType == 0 ? R.layout.item_repo : R.layout.item_repo_demo;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        final ViewHolder holder = new ViewHolder(view, viewType);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( Intent.ACTION_VIEW,
                        Uri.parse(mRepositories.get(holder.getAdapterPosition()).getHtmlUrl()));
                mActivity.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Repository repository = mRepositories.get(position);
        holder.title.setText(repository.getName());
        if (repository.getLanguage() == null && mType == 0) {
            holder.language.setVisibility(View.GONE);
        } else if (mType == 0) {
            holder.language.setVisibility(View.VISIBLE);
            holder.language.setText(repository.getLanguage());
        }
        if (repository.getDescription() == null) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(repository.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return mRepositories.size();
    }

    void addAll(List<Repository> data) {
        int startIndex = mRepositories.size();
        mRepositories.addAll(data);
        notifyItemRangeChanged(startIndex, data.size());
    }

    void clear() {
        mRepositories.clear();
        notifyDataSetChanged();
        //notifyItemRemoved(0);
    }

    int size() {
        return mRepositories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        TextView language;

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text_title);
            description = (TextView) itemView.findViewById(R.id.text_description);
            if (viewType == 0)
                language = (TextView) itemView.findViewById(R.id.text_language);
        }
    }
}
