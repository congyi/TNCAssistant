package com.example.congyitan.tncassistant;

/**
 * Created by Congyi Tan on 10/24/2015.
 */
public class Projects {

    //internal project name
    String internalProjectName;
    String globalProjectName;

    public Projects() {
    }

    public String getProjectName() {
        return globalProjectName;
    }

    public void setProjectName(String projectName) {
        internalProjectName = projectName.replaceAll(" ", "_");
    }
}
