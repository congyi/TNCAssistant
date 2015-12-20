package com.example.congyitan.tncassistant;

/**
 * An object of this class represents one item on the list in ProjectBuilder
 * Created by Congyi Tan on 12/11/2015.
 */

class BrowseProjectsListItem {

    String title;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    BrowseProjectsListItem(String title) {
        this.title = title;
    }
}