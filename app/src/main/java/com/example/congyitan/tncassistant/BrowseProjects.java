package com.example.congyitan.tncassistant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BrowseProjects extends AppCompatActivity implements BrowseProjectsAdapter.BrowseProjectsClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private BrowseProjectsAdapter mBrowseProjectsAdapter;

    //for Log.d ; debugging
    private static final String TAG = "BrowseProjects";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "I'm here in BrowseProjects's onCreate");
        Context thisContext = BrowseProjects.this;

        //inflate layout
        setContentView(R.layout.activity_browse_projects);

        //Set toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_home);
            getSupportActionBar().setTitle(R.string.browse_projects);
        }

        //Build the list of items in RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.browse_projects_list);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mBrowseProjectsAdapter = new BrowseProjectsAdapter(constructBrowseProjectsList(), thisContext);
        mRecyclerView.setAdapter(mBrowseProjectsAdapter);
        mBrowseProjectsAdapter.setBrowseProjectsClickListener(this);
    }

    // This method creates an ArrayList that has BrowseProjectListItem objects
    public List<BrowseProjectsListItem> constructBrowseProjectsList() {

        //blank list array to store all the projects we read from the TNCAssistant directory
        List<BrowseProjectsListItem> list = new ArrayList<>();

        //get the directory of TNCAssistant so we can read all the locally stored projects
        File projectDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "TNCAssistant/");
        File[] tempFileArray =  projectDir.listFiles(); //store all the files we find in the above directory in a temp array
        int directorySize = tempFileArray.length;

        //consolidate all the projects in my ArrayList
        for (int i = 0; i < directorySize; i++){

            File fileToRead = new File(tempFileArray[i], "/info.txt");

            try {

                BufferedReader buf = new BufferedReader(new FileReader(fileToRead)); //open reader on the info.txt file
                String postalcode = buf.readLine(); //read the first line (which is the postalcode)
                BrowseProjectsListItem newListItem = new BrowseProjectsListItem(postalcode);
                list.add(newListItem);

            } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return list;
    }

    @Override
    public void onListItemClicked(View view, final String postalcode, int position) {

        Log.d(TAG, "I'm here in BrowseProjects's onListItemClicked");

        //put postal code in intent and start Project Builder activity
        Intent intent = new Intent(BrowseProjects.this, ProjectBuilder.class);
        intent.putExtra("postalcode", postalcode);
        startActivity(intent);

    }

}
