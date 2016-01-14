package com.example.congyitan.tncassistant.utilities;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.congyitan.tncassistant.R;

import java.util.Collections;
import java.util.List;

/**
 * RecyclerView for building a list of ProjectBuilderListItem in ProjectBuilder
 * Created by Congyi Tan on 12/11/2015.
 */
public class ProjectBuilderAdapter extends RecyclerView.Adapter<ProjectBuilderAdapter.ViewHolder> {

    //for Log.d ; debugging
    private static final String TAG = "ProjectBuilderAdapter";
    private Context myContext;
    private List<ProjectBuilderListItem> myList = Collections.emptyList();

    private ProjectBuilderClickListener myClickListener;

    //Constructor for ProjectBuilderAdapter
    public ProjectBuilderAdapter(List<ProjectBuilderListItem> inputList, Context thisContext) {
        this.myContext = thisContext;
        this.myList = inputList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.itemlist_project_builder_textview);
            icon = (ImageView) itemView.findViewById(R.id.itemlist_project_builder_imageview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Log.d(TAG, "I'm here in Adapter's onClick and getAdapterPosition is " + getAdapterPosition());

            if(myClickListener == null)
                Log.d(TAG, "myClickListender was NULL");
            else
                myClickListener.onListItemClicked(v, getAdapterPosition());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemlist_project_builder, parent, false);

        ViewHolder VH = new ViewHolder(view);

        return VH;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ProjectBuilderListItem currentListItem = myList.get(position);

        holder.title.setText(currentListItem.title);
        holder.icon.setImageResource(currentListItem.iconId);
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public interface ProjectBuilderClickListener{
        void onListItemClicked(View view, int position);
    }

    public void setProjectBuilderClickListener(final ProjectBuilderClickListener inputClickListener){
        this.myClickListener = inputClickListener;
    }

}
