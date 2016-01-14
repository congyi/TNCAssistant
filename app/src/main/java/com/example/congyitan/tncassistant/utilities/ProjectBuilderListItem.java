package com.example.congyitan.tncassistant.utilities;

/**
 * An object of this class represents one item on the list in ProjectBuilder
 * Created by Congyi Tan on 12/11/2015.
 */

public class ProjectBuilderListItem {
    int iconId;
    String title;

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ProjectBuilderListItem(int iconId, String title) {
        this.iconId = iconId;
        this.title = title;
    }
}