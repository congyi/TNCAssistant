package com.example.congyitan.tncassistant;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class BrowseProjectsAdapter extends RecyclerView.Adapter<BrowseProjectsAdapter.ViewHolder> {

    //for Log.d ; debugging
    private static final String TAG = "BrowseProjectsAdapter";

    private Context mContext;
    private List<BrowseProjectsListItem> mList = Collections.emptyList();

    private BrowseProjectsClickListener mClickListener;

    //Constructor for BrowseProjectsAdapter
    public BrowseProjectsAdapter(List<BrowseProjectsListItem> inputList, Context thisContext) {
        this.mContext = thisContext;
        this.mList = inputList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.itemlist_browse_projects_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            //Log.d(TAG, "I'm here in Adapter's onClick and getAdapterPosition is " + getAdapterPosition());

            if(mClickListener == null)
                Log.d(TAG, "myClickListender was NULL");
            else
                mClickListener.onListItemClicked(v, getAdapterPosition());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemlist_browse_projects, parent, false);

        ViewHolder VH = new ViewHolder(view);

        return VH;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BrowseProjectsListItem currentListItem = mList.get(position);
        holder.title.setText(currentListItem.title);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface BrowseProjetsClickListener{
        void onListItemClicked(View view, int position);
    }

    public void setBrowseProjectsClickListener(final BrowseProjects inputClickListener){
        this.mClickListener = inputClickListener;
    }

    public interface BrowseProjectsClickListener {
        void onListItemClicked(View view, int position);
    }
}
