package com.example.congyitan.tncassistant;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static android.content.DialogInterface.*;

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

        TextView postalcodeTV;
        ImageButton deleteIB;

        public ViewHolder(View itemView) {
            super(itemView);

            postalcodeTV = (TextView) itemView.findViewById(R.id.itemlist_browse_projects_textview);
            deleteIB = (ImageButton) itemView.findViewById(R.id.itemlist_browse_projects_delete);

            postalcodeTV.setOnClickListener(this);
            deleteIB.setOnClickListener(this);

        }

        private void recursiveDelete(File fileOrDirectory){

            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    recursiveDelete(child);

            fileOrDirectory.delete();

        }

        private void deleteProject(final String postalcode){

            //user clicked delete project -> show an alertDialog to confirm
            new AlertDialog.Builder(mContext)
                    .setTitle("Delete confirmation")
                    .setMessage("Are you sure you want delete this project? This cannot be undone.")
                    .setNegativeButton(android.R.string.no, new OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    })
                    .setPositiveButton(android.R.string.yes, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //remove project from arraylist
                            int position = getAdapterPosition();

                            mList.remove(position);
                            notifyItemRemoved(position);

                            //get the Documents directory of TNCAssistant
                            File docDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                                    "TNCAssistant/" + postalcode + "/");

                            recursiveDelete(docDir);

                            //get the Pictures directory of TNCAssistant
                            File imageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                    "TNCAssistant/" + postalcode + "/");

                            recursiveDelete(imageDir);

                        }
                    }).create().show();
        }

        @Override
        public void onClick(View view) {

            //Log.d(TAG, "I'm here in Adapter's onClick and getAdapterPosition is " + getAdapterPosition());

            //Get the ViewGroup that contains the delete icon ImageButton
            ViewGroup vg = (ViewGroup) view.getParent();

            //I'll search the ViewGroup for the textView and extract the postalcode
            TextView textView;
            String postalcode = null;

            for (int i = 0; i < vg.getChildCount(); i++) {

                View thisChild = vg.getChildAt(i);

                if (thisChild instanceof TextView) {
                    textView = (TextView) thisChild; //Found it! there's only 1 textview
                    postalcode = textView.getText().toString();
                    break;
                }
            }

            Log.d(TAG, "Postal Code retrieved is: " + postalcode);

            //check if user pressed delete
            if(view.getId() == R.id.itemlist_browse_projects_delete) {
                //user pressed delete, so remove item from recyclerview and delete from file
                deleteProject(postalcode);

            } else
                //user did not press delete
                mClickListener.onListItemClicked(view, postalcode, getAdapterPosition());
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
        holder.postalcodeTV.setText(currentListItem.postalcode);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void setBrowseProjectsClickListener(final BrowseProjects inputClickListener){
        this.mClickListener = inputClickListener;
    }

    public interface BrowseProjectsClickListener {
        void onListItemClicked(View view, String postalcode, int position);
    }
}
