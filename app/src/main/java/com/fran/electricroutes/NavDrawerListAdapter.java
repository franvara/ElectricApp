package com.fran.electricroutes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NavDrawerListAdapter extends BaseAdapter {
	
	private Activity activity;
	ArrayList<Item_objct> arrayitems;
	
	public NavDrawerListAdapter(Activity activity, ArrayList<Item_objct> listarray){
		super();
        this.activity = activity;
		this.arrayitems = listarray;
	}

    //Return the array list object
    @Override
    public Object getItem(int position) {
        return arrayitems.get(position);
    }

	@Override
	public int getCount() {
		return arrayitems.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

    //Row class
    public static class Row {
        TextView title_item;
        ImageView icon;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Row view;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {

            view = new Row();
            //Set item object y get to array
            Item_objct item = arrayitems.get(position);
            convertView = inflater.inflate(R.layout.drawer_list_item, null);
            //Title
            view.title_item = (TextView) convertView.findViewById(R.id.title_item);
            //Set the title with the object name
            view.title_item.setText(item.getTitle());
            //Icon
            view.icon = (ImageView) convertView.findViewById(R.id.icon);
            //Set the icon
            view.icon.setImageResource(item.getIcon());
            convertView.setTag(view);

        }else{
            view = (Row) convertView.getTag();
        }
        
        return convertView;
	}

}
