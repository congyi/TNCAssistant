package com.example.congyitan.tncassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

public class ProjectInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_project_info);

        //Set toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_file);
            getSupportActionBar().setTitle(R.string.project_info);
        }
    }

    public void onPhaseRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        Spinner tcSpinner = (Spinner) findViewById(R.id.tc_spinner);

        // Check which radio button was clicked
        switch(view.getId()) {

            case R.id.phase5:
                if (checked) {
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                            R.array.phase5_array, android.R.layout.simple_spinner_item);
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    tcSpinner.setAdapter(adapter);

                    break;
                }

            case R.id.phase6:
                if (checked) {
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.phase6_array, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                tcSpinner.setAdapter(adapter);

                break;
            }

            case R.id.sn1:
                if (checked) {
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                            R.array.sn1_array, android.R.layout.simple_spinner_item);
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    tcSpinner.setAdapter(adapter);

                    break;
                }
        }
    }
}
