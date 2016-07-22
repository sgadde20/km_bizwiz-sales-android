package com.webparadox.bizwizsales;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.webparadox.bizwizsales.adapter.CalMonthApptsAdapter;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.CalendarListPaginationModel;

public class CalendarAdapter extends BaseAdapter {
	private Context mContext;

	private Calendar month;
	public GregorianCalendar pmonth;
	/**
	 * calendar instance for previous month for getting complete view
	 */
	public GregorianCalendar pmonthmaxset;
	int firstDay;
	int maxWeeknumber;
	int maxP;
	int calMaxP;
	int lastWeekDay;
	int leftDays;
	int mnthlength;

	private ArrayList<String> items;
	public static List<String> dayString;
	public static List<String> formatedDayString;
	TextView txtDateView;
	TextView txtPreviousDateView = null;
	int previousPosition;
	public static View previousView;
	public static GregorianCalendar selectedDate;
	public static String itemvalue, itemvalueFormated, curentDateString,
			curentDateStringFormated;
	public static DateFormat df, df1, formatedDate;

	public static String strSelectedDate;
	public static String strCurrentDay;

	public CalendarAdapter(Context c, GregorianCalendar monthCalendar) {
		CalendarAdapter.dayString = new ArrayList<String>();
		CalendarAdapter.formatedDayString = new ArrayList<String>();
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
			v = vi.inflate(R.layout.calendar_grid_row_view, null);
		}
		/*
		 * RelativeLayout.LayoutParams lp = new
		 * RelativeLayout.LayoutParams(CalendarActivity.calendarCellWidth,
		 * CalendarActivity.calendarCellWidth ); RelativeLayout rl =
		 * (RelativeLayout) v.findViewById(R.id.relativecell);
		 * rl.setLayoutParams(lp);
		 */
		/*
		 * rl.getLayoutParams().height = CalendarActivity.calendarCellWidth;
		 * rl.getLayoutParams().width = CalendarActivity.calendarCellWidth;
		 */
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
			if (!CalendarActivity.isCalendarLoadBtn) {
				String datearr[] = curentDateString.split("-");
				if (month.get(GregorianCalendar.MONTH) + 1 == Integer
						.parseInt(datearr[1])) {
					setSelected(v, position);
				}
			}
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

		ImageView imgAppoinMents = (ImageView) v
				.findViewById(R.id.ImageView_Green_Circle);
		ImageView imgFollowUps = (ImageView) v
				.findViewById(R.id.ImageView_Blue_Circle);
		ImageView imgNotes = (ImageView) v
				.findViewById(R.id.ImageView_Orange_Circle);
		imgAppoinMents.setVisibility(View.GONE);
		imgFollowUps.setVisibility(View.GONE);
		imgNotes.setVisibility(View.GONE);

		// ArrayList<String> sdfdsf =
		// Singleton.getInstance().mCalendarAppointmentsData;
		// ArrayList<String> sdfdfdfsf =
		// Singleton.getInstance().mCalendarFollowUpsData;
		// ArrayList<String> sdfdsfddd =
		// Singleton.getInstance().mCalendarNotesData;

		if (Singleton.getInstance().mCalendarAppointmentsData
				.contains(formatedDayString.get(position))) {
			imgAppoinMents.setVisibility(View.VISIBLE);
		}
		if (Singleton.getInstance().mCalendarFollowUpsData
				.contains(formatedDayString.get(position))) {
			imgFollowUps.setVisibility(View.VISIBLE);
		}
		if (Singleton.getInstance().mCalendarNotesData
				.contains(formatedDayString.get(position))) {
			imgNotes.setVisibility(View.VISIBLE);
		}

		return v;
	}

	public View setSelected(View view, int position) {
		CalendarActivity.isSaveNote = false;
		if (txtPreviousDateView != null && previousView != null) {
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
		if (!mContext.getResources().getBoolean(R.bool.is_tablet)) {
			CalendarActivity.isPopupView = true;
		}
		CalendarActivity.popupLayout.setVisibility(View.VISIBLE);
		if (!mContext.getResources().getBoolean(R.bool.is_tablet)) {
			CalendarActivity.popupDateTV.setText(formatedDayString
					.get(position));
		}
		strSelectedDate = formatedDayString.get(position);
		ArrayList<CalendarListPaginationModel> totalMonthData = new ArrayList<CalendarListPaginationModel>();
		// ArrayList<CalendarListPaginationModel> selectedDayApptsData = new
		// ArrayList<CalendarListPaginationModel>();
		Singleton.getInstance().selectedDayApptsData.clear();
		totalMonthData = Singleton.getInstance().mCalendarMonthViewData;
		for (int i = 0; i < totalMonthData.size(); i++) {
			if (totalMonthData.get(i).mFormattedApptDate
					.equals(strSelectedDate)
					&& (!totalMonthData.get(i).mEventType
							.equals(Constants.KEY_CAL_FOLLOW_UPS))) {
				// selectedDayApptsData.add(totalMonthData.get(i));
				Singleton.getInstance().selectedDayApptsData.add(totalMonthData
						.get(i));

			}
		}
		// CalendarActivity.calmonthAdapter = new CalMonthApptsAdapter(mContext,
		// strSelectedDate, selectedDayApptsData);
		CalendarActivity.calmonthAdapter = new CalMonthApptsAdapter(mContext,
				strSelectedDate, Singleton.getInstance().selectedDayApptsData);

		CalendarActivity.calMonthLV
				.setAdapter(CalendarActivity.calmonthAdapter);
		CalendarActivity.btnAppointments
				.setBackgroundResource(R.drawable.ic_appointments_selected);
		CalendarActivity.btnFollowups
				.setBackgroundResource(R.drawable.ic_followups_normal);
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
		// finding number of weeks in current month.
		//
		// maxWeeknumber =
		// cal.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
		/*
		 * GregorianCalendar cal = new GregorianCalendar();
		 * 
		 * 
		 * int maxWeeknumber =
		 * (int)((firstWeekdayOfMonth+month.getActualMaximum(
		 * Calendar.DAY_OF_MONTH)+5)/7);
		 */
		/*
		 * Calendar cal = Calendar.getInstance(); int year =
		 * cal.get(Calendar.YEAR); int monthtest = cal.get(Calendar.MONTH); int
		 * firstWeek = (new
		 * GregorianCalendar(year,monthtest,1).get(Calendar.WEEK_OF_YEAR)); int
		 * lastWeek = (new
		 * GregorianCalendar(year,monthtest,30).get(Calendar.WEEK_OF_YEAR)); int
		 * lastday = month.get(lastWeekDay);
		 */
		// maxWeeknumber = getWeeksOfMonth(month.MONTH, month.YEAR);

		// maxWeeknumber = month.getActualMaximum(Calendar.WEEK_OF_MONTH);
		// loki - after a lot of failed solutions setting the max week number to
		// static value as 6
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
		if (CalendarActivity.isSaveNote) {
			curentDateString = dayString
					.get(CalendarActivity.selectedDatePosition);
			String[] separatedDay = curentDateString.split("-");
			strCurrentDay = separatedDay[2].replaceFirst("^0*", "");
		}
	}

	// Gives correct max week value but need to send correct selected month and
	// year
	/*
	 * public static int getWeeksOfMonth(int month, int year) { Calendar cal =
	 * Calendar.getInstance(); cal.set(Calendar.YEAR, year);
	 * cal.set(Calendar.MONTH, month); cal.set(Calendar.DAY_OF_MONTH, 1);
	 * 
	 * int ndays = cal.getActualMaximum(Calendar.DAY_OF_MONTH); int weeks[] =
	 * new int[ndays]; for (int i = 0; i < ndays; i++) { weeks[i] =
	 * cal.get(Calendar.WEEK_OF_YEAR); cal.add(Calendar.DATE, 1); }
	 * 
	 * return (weeks[(weeks.length) - 1] - weeks[0]) + 1; }
	 */

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