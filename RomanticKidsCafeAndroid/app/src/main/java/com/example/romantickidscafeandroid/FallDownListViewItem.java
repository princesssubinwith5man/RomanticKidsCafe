package com.example.romantickidscafeandroid;

public class FallDownListViewItem {
    private String Title;
    private String Content;
    private int iconDrawable;
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

    public String getTitle() {
        return this.Title;
    }

    public int getIcon() {
        return this.iconDrawable;
    }

    public String getContent() {
        return this.Content;
    }
}
