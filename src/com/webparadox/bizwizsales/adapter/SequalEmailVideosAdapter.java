package com.webparadox.bizwizsales.adapter;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.SequelVideoActivity;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.SequalEmailVideosModel;

public class SequalEmailVideosAdapter extends BaseAdapter implements
		OnClickListener {

	Context mContext;
	LayoutInflater sequalEmaiVideosInfulate, spinnerLayoutInflate;
	Typeface droidSans, droidSansBold;
	ArrayList<SequalEmailVideosModel> sequalEmailVideosList;
	public CalendarDialogAdapter adapter;
	public CalendarDaysAdapter daysadapter;
	public Handler handler;
	public ArrayList<String> items;
	GregorianCalendar month;
	GregorianCalendar itemmonth;
	public static TextView txtDateView;
	Dialog dialog;
	GridView gridDates;
	Spinner spinnerAppts;
	String apptsId;
	SpinnerAdapter spinnerAdapterObj;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	public static GestureDetector gesturedetector = null;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageOnFail(R.drawable.ic_error)
			.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
			.cacheOnDisk(true).considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	public static String selectedDate;
	String videoId;
	VideoSendAsyncTask sequalVideoSendAsyncTask;

	ActivityIndicator pDialog;

	public SequalEmailVideosAdapter(Context mContext2) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext2;
		droidSans = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans-Bold.ttf");
		sequalEmailVideosList = Singleton.getInstance().sequalEmailVideosList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sequalEmailVideosList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return sequalEmailVideosList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		sequalEmaiVideosInfulate = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = sequalEmaiVideosInfulate.inflate(
				R.layout.sequal_videos_list_row, parent, false);

		TextView txtSequalEmail = (TextView) convertView
				.findViewById(R.id.textViewSequalEmail);
		txtSequalEmail.setTypeface(droidSansBold);

		Button btnSchedule = (Button) convertView
				.findViewById(R.id.buttonschedule);
		ImageView imgVideoThumbnail = (ImageView) convertView
				.findViewById(R.id.imageViewThumnail);
		btnSchedule.setTag(position);
		btnSchedule.setOnClickListener(this);
		imgVideoThumbnail.setTag(position);
		imgVideoThumbnail.setOnClickListener(this);
		imageLoader.displayImage(sequalEmailVideosList.get(position).mImageURL,
				imgVideoThumbnail, options);

		txtSequalEmail
				.setText(sequalEmailVideosList.get(position).mSequelEmail);

		return convertView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int position = (Integer) v.getTag();

		switch (v.getId()) {
		case R.id.imageViewThumnail:
			openSequalVideoFile(sequalEmailVideosList.get(position).mVideoURL);
			break;
		case R.id.buttonschedule:
			videoId = sequalEmailVideosList.get((Integer) v.getTag()).mEmailId;
			showCalendarDialog();
			break;
		default:
			break;
		}

	}

	private void openSequalVideoFile(String path) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.parse(path);
		intent.setDataAndType(uri, "video/*");
		try {
			mContext.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext,
					mContext.getResources().getString(R.string.no_application),
					Constants.TOASTMSG_TIME).show();
			e.printStackTrace();
		}
	}

	public void showCalendarDialog() {
		items = new ArrayList<String>();
		month = (GregorianCalendar) GregorianCalendar.getInstance();
		itemmonth = (GregorianCalendar) month.clone();

		dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_calendar);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		spinnerAppts = (Spinner) dialog.findViewById(R.id.spinner_appts);
		spinnerAdapterObj = new SpinnerAdapter(mContext);
		spinnerAppts.setAdapter(spinnerAdapterObj);
		spinnerAppts.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				apptsId = Singleton.getInstance().appointmentArray
						.get(position).getAppointmentId();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		Button btnSchedule = (Button) dialog
				.findViewById(R.id.buttonscheduledate);
		Button btnCancel = (Button) dialog.findViewById(R.id.buttoncancel);
		btnSchedule.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String dealerId = SequelVideoActivity.dealerID;
				String empId = SequelVideoActivity.employeeID;
				String custId = SequelVideoActivity.CustomerId;

				Log.i("dealerid", dealerId);
				Log.i("empId", empId);
				Log.i("custId", custId);
				Log.i("videoId", videoId);
				Log.i("apptsId", apptsId);
				Log.i("selectedDate", selectedDate);

				final JSONObject reqObj_data = new JSONObject();
				final ArrayList<JSONObject> reqData = new ArrayList<JSONObject>();
				try {
					reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
					reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, empId);
					reqObj_data.put(Constants.JSON_KEY_CUSTOMER_ID, custId);
					reqObj_data.put(Constants.JSON_KEY_EMAIL_ID, videoId);
					reqObj_data.put(Constants.KEY_APPOINTMENT_ID, apptsId);
					reqObj_data.put(Constants.JSON_KEY_DATE_TO_SEND,
							selectedDate);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				reqData.add(reqObj_data);
				sequalVideoSendAsyncTask = new VideoSendAsyncTask(mContext,
						Constants.SEND_SEQUEL_EMAIL_VIDEOS,
						Constants.REQUEST_TYPE_POST, reqData);
				sequalVideoSendAsyncTask.execute();
				dialog.cancel();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		GridView gridDays = (GridView) dialog.findViewById(R.id.gridViewdays);
		gridDates = (GridView) dialog.findViewById(R.id.gridview);
		txtDateView = (TextView) dialog.findViewById(R.id.textView1);
		gridDates.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				String selectedGridDate = CalendarDialogAdapter.dayString
						.get(position);

				String[] separatedTime = selectedGridDate.split("-");
				String gridvalueString = separatedTime[2].replaceFirst("^0*",
						"");
				int gridvalue = Integer.parseInt(gridvalueString);
				if ((gridvalue > 10) && (position < 8)) {
					setPreviousMonth();
					refreshCalendar();
				} else if ((gridvalue < 7) && (position > 28)) {
					setNextMonth();
					refreshCalendar();
				}

				((CalendarDialogAdapter) parent.getAdapter()).setSelected(v,
						position);

				// popupLayout.setVisibility(View.VISIBLE);
			}
		});

		daysadapter = new CalendarDaysAdapter(mContext);
		adapter = new CalendarDialogAdapter(mContext, month);
		gridDays.setAdapter(daysadapter);
		gridDates.setAdapter(adapter);
		gesturedetector = new GestureDetector(new MyGestureListener());
		gridDates.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gesturedetector.onTouchEvent(event);
				if (gesturedetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		});

		handler = new Handler();
		handler.post(calendarUpdater);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	public Runnable calendarUpdater = new Runnable() {
		@Override
		public void run() {
			items.clear();
			for (int i = 0; i < 7; i++) {
				itemmonth.add(GregorianCalendar.DATE, 1);
			}
			adapter.notifyDataSetChanged();
		}
	};

	private void setNextMonth() {

		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMaximum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) + 1),
					month.getActualMinimum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) + 1);
		}

	}

	private void setPreviousMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}

	}

	public void refreshCalendar() {
		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		handler.post(calendarUpdater); // generate some calendar items

		txtDateView.setText(android.text.format.DateFormat.format("MMMM yyyy",
				month));

	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		private static final int SWIPE_MIN_DISTANCE = 140;

		private static final int SWIPE_MAX_OFF_PATH = 90;

		private static final int SWIPE_THRESHOLD_VELOCITY = 70;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,

		float velocityY) {

			float dX = e2.getX() - e1.getX();
			float dY = e1.getY() - e2.getY();
			if (Math.abs(dY) >= SWIPE_THRESHOLD_VELOCITY
					&& Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY
					&& Math.abs(dX) >= SWIPE_MIN_DISTANCE) {

				if (dX > 0) {
					// Right Swipe
					setPreviousMonth();
					refreshCalendar();
				} else {
					// Left Swipe
					setNextMonth();
					refreshCalendar();
				}

				return true;

			} else if (Math.abs(dX) >= SWIPE_THRESHOLD_VELOCITY &&

			Math.abs(velocityY) >= SWIPE_THRESHOLD_VELOCITY &&

			Math.abs(dY) >= SWIPE_MIN_DISTANCE) {

				if (dY > 0) {
					// Up Swipe

				} else {
					// Down Swipe
				}

				return true;
			}
			return false;
		}
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		dispatchTouchEvent(ev);
		return gesturedetector.onTouchEvent(ev);
	}

	class VideoSendAsyncTask extends AsyncTask<Void, Integer, Void> {
		String toDisplay;
		String mRequestUrl, mMethodType;
		ArrayList<JSONObject> mRequestJson;
		ServiceHelper serviceHelper;
		JSONObject responseJson;
		Context mContext;

		public VideoSendAsyncTask(Context context, String send_video_url,
				String rEQUEST_TYPE_POST, ArrayList<JSONObject> reqArr) {
			this.mContext = context;
			this.mMethodType = rEQUEST_TYPE_POST;
			this.mRequestUrl = send_video_url;
			this.mRequestJson = reqArr;
			serviceHelper = new ServiceHelper(mContext);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			try {
				if (pDialog != null) {
					if (pDialog.isShowing()) {
						pDialog.dismiss();
					}
					pDialog = null;
				}
				pDialog = new ActivityIndicator(mContext);
				pDialog.show();
			} catch (Exception e) {
				// TODO: handle exception
			}

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			Log.i("request data doinbak", mRequestJson.toString());
			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), mRequestUrl, mMethodType);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
		if(responseJson != null){
			if (responseJson.has(Constants.JSON_KEY_EMAILSCHEDULER)) {
				try {
					JSONArray localJsonArray = responseJson
							.getJSONArray(Constants.JSON_KEY_EMAILSCHEDULER);
					String status = localJsonArray.getJSONObject(0).getString(
							Constants.JSON_KEY_STATUS);
					if (status.equals(Constants.VALUE_SUCCESS)) {
						toDisplay = "Scheduled successfully";
					} else {
						toDisplay = status;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				toDisplay = "Failed, please try again later";
			}
			Toast.makeText(mContext, toDisplay, Toast.LENGTH_SHORT).show();
		}
		try {
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				pDialog = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
			super.onPostExecute(result);
		}
	}

	protected void onDestroy() {
		if (sequalVideoSendAsyncTask != null) {
			sequalVideoSendAsyncTask.cancel(true);
			sequalVideoSendAsyncTask = null;
		}

	}

	public class SpinnerAdapter extends BaseAdapter {

		Context spinnerContext;

		public SpinnerAdapter(Context msContext) {
			// TODO Auto-generated constructor stub
			this.spinnerContext = msContext;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Singleton.getInstance().appointmentArray.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return Singleton.getInstance().appointmentArray.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			spinnerLayoutInflate = (LayoutInflater) spinnerContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = spinnerLayoutInflate.inflate(
					R.layout.spinner_appts_dialog_calendar, parent, false);
			TextView apptsDate = (TextView) convertView
					.findViewById(R.id.appts_date);
			TextView apptsLeadtype = (TextView) convertView
					.findViewById(R.id.appts_leadtype);
			apptsDate.setText(Singleton.getInstance().appointmentArray.get(
					position).getFormattedApptDate());
			apptsLeadtype.setText(Singleton.getInstance().appointmentArray.get(
					position).getLeadType());
			return convertView;
		}

	}
}
