package com.example.congyitan.tncassistant.utilities;

/**
 * An object of this class represents one item on the list in ProjectBuilder
 * Created by Congyi Tan on 12/11/2015.
 */

public class BrowseProjectsListItem {

    String postalcode;

    //getter
    public String getTitle() {
        return postalcode;
    }

    //setter
    public void setTitle(String title) {
        this.postalcode = title;
    }

    //constructor
    public BrowseProjectsListItem(String postalcode) {
        this.postalcode = postalcode;
    }
}