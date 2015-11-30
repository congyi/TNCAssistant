package com.example.congyitan.tncassistant;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity  implements NewProjectDialog.NewProjectDialogListener {

    //for Log.d ; debugging
    private static final String TAG = "MainActivity";
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
    public void onDialogOK(Bundle myData) {
        createProjectFile(myData);
        Intent intent = new Intent(MainActivity.this, ProjectBuilder.class );
        intent.putExtras(myData);
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

    private boolean createProjectFile(Bundle newProjectData){

        String blkno = newProjectData.getString("blkno");
        String street = newProjectData.getString("street");
        String postalcode = String.valueOf(newProjectData.getInt("postalcode"));

        File newProjectDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"TNCAssistant/" + postalcode);

        // Create the storage directory if it does not exist
        if (!newProjectDir.exists()) {
            if (!newProjectDir.mkdirs()) {
                Log.d(TAG, "Failed to create directory");
                return false;
            }
        }
        //debugging test to see if directory was created
        Log.d(TAG, "Directory is:" + newProjectDir.getAbsolutePath());

        File newProjectFile = new File (newProjectDir, "info.txt");

        try {
            FileWriter newFileWriter = new FileWriter(newProjectFile);
            newFileWriter.write(postalcode + "\n" + street + "\n" + blkno );
            newFileWriter.flush();
            newFileWriter.close();
        } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, "Error creating/writing file");
            }

        return true;
    }
}
