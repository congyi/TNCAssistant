package com.example.congyitan.tncassistant;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ProjectBuilder extends AppCompatActivity implements ProjectBuilderAdapter.ProjectBuilderClickListener {

    private Toolbar mToolbar;
    private Context thisContext;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProjectBuilderAdapter myProjectBuilderAdapter;

    //for Log.d ; debugging
    private static final String TAG = "ProjectBuilder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisContext = ProjectBuilder.this;

        //Get message from Intent (specifically the projectTitle)
        //Bundle bundle = getIntent().getExtras();
        //String projectTitle = bundle.getString("projectTitle");

        //Set view and populate title for the toolbar
        setContentView(R.layout.activity_project_builder);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_home);
        getSupportActionBar().setTitle(R.string.new_project_home);

        //Build the list of items in RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.project_builder_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        myProjectBuilderAdapter = new ProjectBuilderAdapter(constructProjectBuilderList(),thisContext);
        mRecyclerView.setAdapter(myProjectBuilderAdapter);
        myProjectBuilderAdapter.setProjectBuilderClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project_builder, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    // This method creates an ArrayList that has ProjectBuilderListItem objects
    public List<ProjectBuilderListItem> constructProjectBuilderList() {

        List<ProjectBuilderListItem> list = new ArrayList<>();

        int[] icons = {R.drawable.ic_file_dark, R.drawable.ic_camera_dark, R.drawable.ic_measurements_dark,
                       R.drawable.ic_add_edit_files_dark, R.drawable.ic_code_dark, R.drawable.ic_print_dark};
        String[] title = {"Project Info", "Add/Edit Images", "Add/Edit Measurements", "Add/Remove Attachments",
                          "View/Edit LaTeX code", "View/Generate Report"};

        for (int i = 0; i < title.length && i < icons.length; i++) {
            ProjectBuilderListItem tempListItem = new ProjectBuilderListItem(icons[i],title[i]);
            list.add(tempListItem);
        }
        return list;
    }

    @Override
    public void onListItemClicked(View view, int position) {


        ImageSetBuilder newFragment = new ImageSetBuilder();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activity_project_builder, newFragment,"ImageSetBuilderFragment");

        Log.d(TAG, "I'm here in ProjectBuilder's onListItemClicked - before commit()");
        fragmentTransaction.commit();
        Log.d(TAG, "I'm here in ProjectBuilder's onListItemClicked - after commit()");

    }
}


