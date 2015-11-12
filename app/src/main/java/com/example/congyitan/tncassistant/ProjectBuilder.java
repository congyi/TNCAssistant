package com.example.congyitan.tncassistant;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectBuilder extends AppCompatActivity {

    private Toolbar mToolbar;
    private ProjectBuilderAdapter adapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ProjectBuilderListItem> list;

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
        mRecyclerView = (RecyclerView)findViewById(R.id.project_builder_list);
        mAdapter = new ProjectBuilderAdapter(constructProjectBuilderList());
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

   class ProjectBuilderListItem{
        int iconId;
        String title;

       ProjectBuilderListItem  (int iconId, String title){
           this.iconId = iconId;
           this.title = title;
       }

       ProjectBuilderListItem  (){
       }
    }

    // This method creates an ArrayList that has ProjectBuilderListItem objects
    public  List<ProjectBuilderListItem> constructProjectBuilderList(){

        List<ProjectBuilderListItem> list = new ArrayList<>();

        int[] icons = {R.drawable.project_description, R.drawable.image_icon};
        String[] title = {"Project Info", "Add Images"};

        for (int i = 0; i < title.length && i < icons.length; i++){
            ProjectBuilderListItem currentListItem = new ProjectBuilderListItem();
            currentListItem.iconId = icons[i];
            currentListItem.title = title[i];
            list.add(currentListItem);
        }

        return list;
    }

    public class ProjectBuilderAdapter extends RecyclerView.Adapter<ProjectBuilderAdapter.ViewHolder>{

        List<ProjectBuilderListItem> myList = Collections.emptyList();

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView title;
            ImageView icon;

            public ViewHolder (View itemView){
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.itemlist_project_builder_textview);
                icon = (ImageView) itemView.findViewById(R.id.itemlist_project_builder_imageview);
            }
        }

        public ProjectBuilderAdapter(Context context){

            this.myList = myList;
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
            return 0;
        }
    }
}
