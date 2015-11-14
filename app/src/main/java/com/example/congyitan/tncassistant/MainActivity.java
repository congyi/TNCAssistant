package com.example.congyitan.tncassistant;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  implements NewProjectDialog.NewProjectDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the two on screen buttons
        //onclick listeners are attached to activity_main.xml
        Button newProject = (Button) findViewById(R.id.new_project);
        Button browseProjects = (Button) findViewById(R.id.browse_projects);
    }

    public void newProject(View view) {
        //Open up the DialogFragment that prompts user for the title
        DialogFragment newFragment = new NewProjectDialog();
        newFragment.show(getFragmentManager(), "New Project Dialog");
    }

    @Override
    public void onDialogOK(String projectTitle) {
        //Toast.makeText(MainActivity.this, projectTitle, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, ProjectBuilder.class );
        intent.putExtra("projectTitle", projectTitle);
        startActivity(intent);
    }

    @Override
    public void onDialogCancel() {
        //user pressed cancel in NewProjectDialog
        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
    }

    public void browseProjects(View view){
        Toast.makeText(MainActivity.this, "Browse Projects", Toast.LENGTH_SHORT).show();
    }


}
