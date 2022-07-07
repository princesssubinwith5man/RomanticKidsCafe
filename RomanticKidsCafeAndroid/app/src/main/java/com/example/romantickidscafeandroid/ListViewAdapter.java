package com.example.romantickidscafeandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    public ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();


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

        TextView Title;
        TextView Content;


        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_custom, viewGroup, false);
        }

        Title = (TextView) view.findViewById(R.id.Title);
        Content = (TextView) view.findViewById(R.id.Content);


        ListViewItem listViewItem = listViewItemList.get(i);

        Title.setText(listViewItem.getTitle());
        Content.setText(listViewItem.getContent());


        return view;
    }

    public void addItem(String Title, String Content){
        ListViewItem item = new ListViewItem();
        item.setTitle(Title);
        item.setContent(Content);

        listViewItemList.add(item);
    }

    public void clearItem(){
        listViewItemList.clear();
    }
}