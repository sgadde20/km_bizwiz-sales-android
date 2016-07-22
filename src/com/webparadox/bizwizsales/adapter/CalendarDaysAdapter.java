package com.webparadox.bizwizsales.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.webparadox.bizwizsales.R;

public class CalendarDaysAdapter extends BaseAdapter {
	private Context context;

	public CalendarDaysAdapter(Context context) {
		this.context = context;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 7;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null) {

			gridView = new View(context);
			gridView = inflater
					.inflate(R.layout.calendar_dates_grid_item, null);
			TextView textView = (TextView) gridView
					.findViewById(R.id.textviewday);
			RelativeLayout dayLayout = (RelativeLayout) gridView
					.findViewById(R.id.datelayout);
			switch (position) {
			case 0:
				dayLayout.setBackgroundColor(context.getResources().getColor(
						R.color.sunday_bg_color));
				textView.setText(context.getResources().getString(
						R.string.sunday));
				break;

			case 1:
				dayLayout.setBackgroundColor(context.getResources().getColor(
						R.color.monday_bg_color));
				textView.setText(context.getResources().getString(
						R.string.monday));
				break;
			case 2:
				dayLayout.setBackgroundColor(context.getResources().getColor(
						R.color.tuesday_bg_color));
				textView.setText(context.getResources().getString(
						R.string.thuesday));
				break;
			case 3:
				dayLayout.setBackgroundColor(context.getResources().getColor(
						R.color.wednesday_bg_color));
				textView.setText(context.getResources().getString(
						R.string.wednesday));
				break;
			case 4:
				dayLayout.setBackgroundColor(context.getResources().getColor(
						R.color.thursday_bg_color));
				textView.setText(context.getResources().getString(
						R.string.thuresday));
				break;
			case 5:
				dayLayout.setBackgroundColor(context.getResources().getColor(
						R.color.friday_bg_color));
				textView.setText(context.getResources().getString(
						R.string.friday));
				break;
			case 6:
				dayLayout.setBackgroundColor(context.getResources().getColor(
						R.color.saturday_bg_color));
				textView.setText(context.getResources().getString(
						R.string.saturday));
				break;

			}

		} else {
			gridView = (View) convertView;
		}

		return gridView;
	}

}
