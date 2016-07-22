package com.webparadox.bizwizsales.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.webparadox.bizwizsales.R;

public class MainMenuAdapter extends BaseAdapter {
	ArrayList<String> mList;
	Context mContext;
	LayoutInflater listinflate;
	Integer[] mMenuIcons;

	public MainMenuAdapter(Context context, ArrayList<String> list,
			Integer[] menu_icons) {
		// TODO Auto-generated constructor stub
		this.mList = list;
		this.mContext = context;
		this.mMenuIcons = menu_icons;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		listinflate = (LayoutInflater) mContext
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
		convertView = listinflate
				.inflate(R.layout.list_mainmenu, parent, false);
		TextView menutv = (TextView) convertView
				.findViewById(R.id.mainmenu_row_tv);

		ImageView image_menuIcon = (ImageView) convertView
				.findViewById(R.id.menu_icons);
		menutv.setText(mList.get(position));
		image_menuIcon.setImageResource((mMenuIcons[position]));
		return convertView;
	}

}
