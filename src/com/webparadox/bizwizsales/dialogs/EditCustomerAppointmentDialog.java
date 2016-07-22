package com.webparadox.bizwizsales.dialogs;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.format.Time;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.webparadox.bizwizsales.R;

public class EditCustomerAppointmentDialog extends Dialog implements android.view.View.OnClickListener{

	Context mContext;
	Button dialogClose,saveButton;
	ImageView timeImage,DateImage;
	TextView dateTv,timeTv,appointmentHeader;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	String timeSet = "";
	Typeface droidSans, droidSansBold;
	public EditCustomerAppointmentDialog(Context context) {
		super(context,android.R.style.Theme_Translucent_NoTitleBar);
		// TODO Auto-generated constructor stub
		this.mContext=context;
		droidSans = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans-Bold.ttf");
		showDialog();
	}
	public void showDialog(){

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams WMLP = getWindow().getAttributes();
		WMLP.gravity = Gravity.CENTER;
		WMLP.dimAmount = 0.7f;
		getWindow().setAttributes(WMLP);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		setContentView(R.layout.edit_customer_appointment_dialog);
		dialogClose=(Button)findViewById(R.id.edit_appointment_cancel);
		dialogClose.setOnClickListener(this);
		dialogClose.setTypeface(droidSansBold);
		saveButton=(Button)findViewById(R.id.edit_appointment_save);
		saveButton.setOnClickListener(this);
		saveButton.setTypeface(droidSansBold);
		
		timeImage=(ImageView)findViewById(R.id.edit_customer_time_img);
		timeImage.setOnClickListener(this);
		DateImage=(ImageView)findViewById(R.id.edit_customer_date_img);
		DateImage.setOnClickListener(this);
		appointmentHeader=(TextView)findViewById(R.id.appointment_type_header);
		appointmentHeader.setTypeface(droidSansBold);
		dateTv=(TextView)findViewById(R.id.edit_appointmant_date);
		dateTv.setOnClickListener(this);
		dateTv.setTypeface(droidSans);
		timeTv=(TextView)findViewById(R.id.edit_appointmant_time);
		timeTv.setOnClickListener(this);
		timeTv.setTypeface(droidSans);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.edit_appointment_cancel:
			dismiss();
			break;

		case R.id.edit_appointmant_time:

			getTime();
			break;

		case R.id.edit_appointmant_date:
			getDate();
			break;	

		case R.id.edit_customer_time_img:

			getTime();
			break;

		case R.id.edit_customer_date_img:
			getDate();
			break;	
		case R.id.edit_appointment_save:
			
			Toast.makeText(mContext, "Work in progress..", Toast.LENGTH_SHORT).show();
			dismiss();
			break;	


		default:
			break;
		}

	}

	public void getDate(){
		Time currDate = new Time(Time.getCurrentTimezone());
		currDate.setToNow();
		DatePickerDialog dateDialog = new DatePickerDialog(mContext,
				pickerListener, currDate.year, currDate.month,
				currDate.monthDay);
		dateDialog.show();
	}
	public void getTime(){
		final Calendar cal = Calendar.getInstance();
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
		TimePickerDialog timeDialog = new TimePickerDialog(mContext,
				timePickerListener, hour, minute, false);
		timeDialog.show();
	}

	public DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			String monthName = new DateFormatSymbols().getMonths()[month];
			dateTv.setText(day + " " + monthName.substring(0, 3) + " "
					+ year);

		}
	};

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
			// TODO Auto-generated method stub
			hour = hourOfDay;
			minute = minutes;

			if (hour > 12) {
				hour -= 12;
				timeSet = "PM";
			} else if (hour == 0) {
				hour += 12;
				timeSet = "AM";
			} else if (hour == 12)
				timeSet = "PM";
			else
				timeSet = "AM";
			String min=String.valueOf(minutes);
			if(min.length()==1){
				timeTv.setText(hour + ":" + "0"+minutes + timeSet);
			}else{
				timeTv.setText(hour + ":" + minutes + timeSet);
			}
		
		}

	};

}
