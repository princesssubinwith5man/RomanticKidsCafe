package com.example.romantickidscafeandroid;
public class ListViewItem {
    private String Title;
    private String Content;
    private int iconDrawable;
    private int stateDrawble;
    //final private double lat, lng;

    public void setTitle(String title) {
        Title = title;
    }

    public void setContent(String content) {
        Content = content;
    }

    public void setIcon(int icon) {
        iconDrawable = icon;
    }

    public void setState(int icon) {
        stateDrawble = icon;
    }

    public String getTitle() {
        return this.Title;
    }

    public int getIcon() {
        return this.iconDrawable;
    }
    public int getState() {return this.stateDrawble;}

    public String getContent() {
        return this.Content;
    }


}