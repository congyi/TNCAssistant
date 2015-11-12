package com.example.congyitan.tncassistant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ProjectBuilder extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //for Log.d ; debugging
    private static final String TAG = "ProjectBuilder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get message from Intent (specifically the projectTitle)
        Bundle bundle = getIntent().getExtras();
        String projectTitle = bundle.getString("projectTitle");

        //Set view and populate title for the toolbar
        setContentView(R.layout.activity_project_builder);
        mToolbar = (Toolbar) findViewById(R.id.project_builder_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.new_project_home);
        getSupportActionBar().setTitle(R.string.new_project_home);

        //Build the list of items
        mRecyclerView = (RecyclerView) findViewById(R.id.project_builder_list);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ProjectBuilderAdapter(constructProjectBuilderList());
        mRecyclerView.setAdapter(mAdapter);
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

        int[] icons = {R.drawable.project_description, R.drawable.image_icon};
        String[] title = {"Project Info", "Add Images"};

        for (int i = 0; i < title.length && i < icons.length; i++) {
            ProjectBuilderListItem tempListItem = new ProjectBuilderListItem();
            tempListItem.iconId = icons[i];
            tempListItem.title = title[i];
            list.add(tempListItem);
        }

        return list;
    }
}


