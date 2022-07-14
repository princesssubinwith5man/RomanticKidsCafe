package com.example.romantickidscafeandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FallDownListVIewAdapter extends BaseAdapter {
    public ArrayList<FallDownListViewItem> listViewItemList = new ArrayList<FallDownListViewItem>();


    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int pos = i;
        final Context context = viewGroup.getContext();
        ImageView warningView;
        TextView Title;
        TextView Content;


        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fall_down_listview_custom, viewGroup, false);
        }

        Title = (TextView) view.findViewById(R.id.Title);
        Content = (TextView) view.findViewById(R.id.Content);
        warningView = (ImageView) view.findViewById(R.id.warning);


        FallDownListViewItem listViewItem = listViewItemList.get(i);

        warningView.setImageResource(listViewItem.getIcon());
        Title.setText(listViewItem.getTitle());
        Content.setText(listViewItem.getContent());


        return view;
    }

    public void addItem(String Title, String Content){
        FallDownListViewItem item = new FallDownListViewItem();
        item.setIcon(R.drawable.warning);
        item.setTitle(Title);
        item.setContent(Content);

        listViewItemList.add(item);
    }

    public void clearItem(){
        listViewItemList.clear();
    }
}
