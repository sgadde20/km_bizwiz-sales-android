package com.webparadox.bizwizsales.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.models.NotesModel;

public class CustomerNotesAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater layoutInflater;
	Typeface droidSans, droidSansBold;

	public CustomerNotesAdapter(Context context) {
		this.mContext = context;
		this.layoutInflater = LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
		droidSans = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(
				mContext.getAssets(), "DroidSans-Bold.ttf");
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Singleton.getInstance().noteModel.size();
	}

	@Override
	public NotesModel getItem(int position) {
		// TODO Auto-generated method stub
		return Singleton.getInstance().noteModel.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
			convertView = layoutInflater.inflate(R.layout.customer_notes_row,
					null);
			TextView textDate = (TextView) convertView
					.findViewById(R.id.textView_note_date);
			textDate.setTypeface(droidSansBold);
			TextView textTime = (TextView) convertView
					.findViewById(R.id.textView_note_time);
			textTime.setTypeface(droidSans);
			TextView textName = (TextView) convertView
					.findViewById(R.id.textview_name);
			textName.setTypeface(droidSansBold);
			TextView textNotes = (TextView) convertView
					.findViewById(R.id.textView_notes);
			textNotes.setTypeface(droidSans);
			textName.setText(Singleton.getInstance().noteModel.get(position).EmployeeName);
			textNotes.setText(Singleton.getInstance().noteModel.get(position).note);
			textDate.setText(Singleton.getInstance().noteModel.get(position).NoteType);
			textTime.setText(Singleton.getInstance().noteModel.get(position).AdjustedDateCreated);
		return convertView;
	}
}
