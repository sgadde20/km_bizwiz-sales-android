package com.webparadox.bizwizsales.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.net.ParseException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.webparadox.bizwizsales.R;

public class CalendarDialogAdapter extends BaseAdapter {
	private Context mContext;

	private Calendar month;
	public GregorianCalendar pmonth;
	/**
	 * calendar instance for previous month for getting complete view
	 */
	public GregorianCalendar pmonthmaxset;
	private GregorianCalendar selectedDate;
	int firstDay;
	int maxWeeknumber;
	int maxP;
	int calMaxP;
	int lastWeekDay;
	int leftDays;
	int mnthlength;
	String itemvalue, itemvalueFormated, curentDateString,
			curentDateStringFormated;
	DateFormat df, df1, formatedDate;

	public static String strSelectedDate;
	private ArrayList<String> items;
	public static List<String> dayString;
	public static List<String> formatedDayString;
	TextView txtDateView;
	TextView txtPreviousDateView = null;
	String strCurrentDay;
	int previousPosition;
	View previousView;
	
	SimpleDateFormat input = new SimpleDateFormat("MMM dd, yyyy");
	SimpleDateFormat output = new SimpleDateFormat("MM/dd/yyyy");
	
SequalEmailVideosAdapter sequalvideoadapter;
	public CalendarDialogAdapter(Context c, GregorianCalendar monthCalendar) {
		CalendarDialogAdapter.dayString = new ArrayList<String>();
		CalendarDialogAdapter.formatedDayString = new ArrayList<String>();
		Locale.setDefault(Locale.US);
		month = monthCalendar;
		selectedDate = (GregorianCalendar) monthCalendar.clone();
		mContext = c;
		month.set(GregorianCalendar.DAY_OF_MONTH, 1);
		this.items = new ArrayList<String>();
		df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		df1 = new SimpleDateFormat("EEE, MMM d, yyy", Locale.US);
		formatedDate = new SimpleDateFormat("MMMM dd, yyyy");
		curentDateString = df.format(selectedDate.getTime());
		curentDateStringFormated = formatedDate.format(selectedDate.getTime());
		String[] separatedDay = curentDateString.split("-");
		strCurrentDay = separatedDay[2].replaceFirst("^0*", "");
		Log.i("df1", df1.format(selectedDate.getTime()));
		refreshDays();
	}

	public void setItems(ArrayList<String> items) {
		for (int i = 0; i != items.size(); i++) {
			if (items.get(i).length() == 1) {
				items.set(i, "0" + items.get(i));
			}
		}
		this.items = items;
	}

	public int getCount() {
		return dayString.size();
	}

	public Object getItem(int position) {
		return dayString.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		TextView dayView;
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.dialog_calendar_grid_cell, null);
		}
		
		v.setTag(formatedDayString.get(position));
		dayView = (TextView) v.findViewById(R.id.date);
		txtDateView = dayView;
		String[] separatedTime = dayString.get(position).split("-");
		String gridvalue = separatedTime[2].replaceFirst("^0*", "");
		if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
			dayView.setTextColor(Color.LTGRAY);
			dayView.setClickable(false);
			dayView.setFocusable(false);
		} else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
			dayView.setTextColor(Color.LTGRAY);
			dayView.setClickable(false);
			dayView.setFocusable(false);
		} else if ((Integer.parseInt(gridvalue) <= 15) && (position > 28)) {
			dayView.setTextColor(Color.LTGRAY);
			dayView.setClickable(false);
			dayView.setFocusable(false);
		} else {
			dayView.setTextColor(Color.BLACK);
		}

		if (dayString.get(position).equals(curentDateString)) {

			setSelected(v, position);
			txtPreviousDateView = (TextView) v.findViewById(R.id.date);
			previousView = v;
			previousPosition = position;
		} else {
			v.findViewById(R.id.date).setBackgroundResource(
					R.drawable.selector_calendar_cells);
		}
		dayView.setText(gridvalue);
		String date = dayString.get(position);
		if (date.length() == 1) {
			date = "0" + date;
		}
		String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
		if (monthStr.length() == 1) {
			monthStr = "0" + monthStr;
		}
		return v;
	}
	public View setSelected(View view, int position) {  
		if (txtPreviousDateView != null) {
			txtPreviousDateView
					.setBackgroundResource(R.drawable.selector_calendar_cells);
			if (previousView.getTag().equals(curentDateStringFormated)) {
				Log.i("" + dayString.get(position), " " + curentDateString);
				txtPreviousDateView.setTextColor(Color.RED);
			} else {
				txtPreviousDateView.setTextColor(Color.BLACK);
			}

		}
		txtPreviousDateView = (TextView) view.findViewById(R.id.date);
		view.findViewById(R.id.date).setBackgroundResource(
				R.drawable.shape_calendar_selected_date);
		txtPreviousDateView.setTextColor(Color.WHITE);
		previousView = view;
		strSelectedDate = formatedDayString.get(position);
		
		try {
		    Date oneWayTripDate = null;
			try {
				oneWayTripDate = input.parse(strSelectedDate);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			SequalEmailVideosAdapter.selectedDate = output.format(oneWayTripDate);
		} catch (ParseException e) {
		    e.printStackTrace();
		}
		SequalEmailVideosAdapter.txtDateView.setText(strSelectedDate);
		  return view;  
		 }  

	
	public void refreshDays() {
		// clear items
		items.clear();
		dayString.clear();
		formatedDayString.clear();
		Locale.setDefault(Locale.US);
		pmonth = (GregorianCalendar) month.clone();
		// month start day. ie; sun, mon, etc
		firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
		
		maxWeeknumber = 6;
		// allocating maximum row number for the gridview.
		mnthlength = maxWeeknumber * 7;
		maxP = getMaxP(); // previous month maximum day 31,30....
		calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
		/**
		 * Calendar instance for getting a complete gridview including the three
		 * month's (previous,current,next) dates.
		 */
		pmonthmaxset = (GregorianCalendar) pmonth.clone();
		/**
		 * setting the start date as previous month's required date.
		 */
		pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

		/**
		 * filling calendar gridview.
		 */
		for (int n = 0; n < mnthlength; n++) {

			itemvalue = df.format(pmonthmaxset.getTime());
			itemvalueFormated = formatedDate.format(pmonthmaxset.getTime());
			pmonthmaxset.add(GregorianCalendar.DATE, 1);
			dayString.add(itemvalue);
			formatedDayString.add(itemvalueFormated);

		}
	}


	private int getMaxP() {
		int maxP;
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			pmonth.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}
		maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

		return maxP;
	}

} 