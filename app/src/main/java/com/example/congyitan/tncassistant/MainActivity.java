package com.example.congyitan.tncassistant;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the two on screen buttons
        //onclick listeners are attached to activity_main.xml
        Button newProject = (Button) findViewById(R.id.new_project);
        Button browseProjects = (Button) findViewById(R.id.browse_projects);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void newProject(View view){
        //Toast.makeText(MainActivity.this, "New Project", Toast.LENGTH_SHORT).show();

        FragmentManager manager = getFragmentManager();
        NewProjectDialog newprojectdialog = new NewProjectDialog();
        newprojectdialog.show(manager,"New Project Dialog");
    }

    public void browseProjects(View view){
        Toast.makeText(MainActivity.this, "Browse Projects", Toast.LENGTH_SHORT).show();
    }


}
