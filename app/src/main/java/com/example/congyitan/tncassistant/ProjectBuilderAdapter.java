package com.example.congyitan.tncassistant;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * RecyclerView for building a list of ProjectBuilderListItem in ProjectBuilder
 * Created by Congyi Tan on 12/11/2015.
 */
public class ProjectBuilderAdapter extends RecyclerView.Adapter<ProjectBuilderAdapter.ViewHolder> {

    private List<ProjectBuilderListItem> myList = Collections.emptyList();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.itemlist_project_builder_textview);
            icon = (ImageView) itemView.findViewById(R.id.itemlist_project_builder_imageview);
        }
    }

    public ProjectBuilderAdapter(List<ProjectBuilderListItem> inputList) {

        myList = inputList;
    }

    @Override
    public ProjectBuilderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemlist_project_builder, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ProjectBuilderListItem current = myList.get(position);

        holder.title.setText(current.title);
        holder.icon.setImageResource(current.iconId);
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }
}
