package com.webparadox.bizwizsales.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.asynctasks.CheckoutAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.helper.Signature;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class CheckoutFragment extends Fragment implements OnClickListener{
	static String amount = "";
	Context context = null;
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	String result = "", dealerId, customerId, employeeId, statement = "",apptResultId = "";
	Typeface droidSans, droidSansBold;
	LinearLayout mContent;
	RelativeLayout penLayout;
	Signature m_signature;
	TextView checkoutHeader, checkoutSignature, checkoutAgree;
	EditText checkoutDesc;
	Button checkoutContinue, checkoutClear;
	ImageView signatureImage;
	CheckBox checkoutCb;
	AgreeTask agreeTask;
	View mView;
	ActivityIndicator dialog = null;
	Boolean hasSign = false;
	ImageLoader imageLoader;
	DisplayImageOptions options;
	CheckoutAsyncTask chtrask;

	public static CheckoutFragment newInstance(String Amount) {
		CheckoutFragment f = new CheckoutFragment();
		amount = Amount;
		return f;	
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageOnFail(R.drawable.ic_error)
		.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
		.cacheOnDisk(true).considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565).build();

		userData = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		editor = userData.edit();

		apptResultId = userData.getString(Constants.KEY_APPT_RESULT_ID, "0");

		dealerId = userData.getString("DealerId", "");
		employeeId = userData.getString("EmployeeId", "");
		customerId = userData.getString("CustomerId", "");
		droidSans = Typeface.createFromAsset(context.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(context.getAssets(),
				"DroidSans-Bold.ttf");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView1 = inflater.inflate(R.layout.checkout_activity, container,false);
		RelativeLayout linearLayout1 = (RelativeLayout)rootView1.findViewById(R.id.toplinearLayout1);
		linearLayout1.setVisibility(View.GONE);
		mContent = (LinearLayout) rootView1.findViewById(R.id.sig_layout);
		penLayout = (RelativeLayout) rootView1.findViewById(R.id.pen_layout);
		m_signature = new Signature(context, null);
		mContent.addView(m_signature, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mContent.setVisibility(View.GONE);
		penLayout.setVisibility(View.VISIBLE);
		checkoutHeader = (TextView) rootView1.findViewById(R.id.chechout_header);
		checkoutHeader.setTypeface(droidSansBold);
		checkoutSignature = (TextView)rootView1.findViewById(R.id.checkout_signature);
		checkoutSignature.setTypeface(droidSansBold);
		checkoutDesc = (EditText) rootView1.findViewById(R.id.checkout_dec);
		checkoutDesc.setTypeface(droidSans);
		checkoutContinue = (Button)rootView1.findViewById(R.id.checkout_continue);
		checkoutContinue.setTypeface(droidSansBold);
		checkoutContinue.setOnClickListener(this);
		checkoutAgree = (TextView)rootView1.findViewById(R.id.checkoyut_agree);
		checkoutAgree.setTypeface(droidSans);
		checkoutCb = (CheckBox)rootView1.findViewById(R.id.checkout_checkbox);
		checkoutDesc = (EditText)rootView1.findViewById(R.id.checkout_dec);
		mView = mContent;
		checkoutDesc.clearFocus();

		agreeTask = new AgreeTask();
		agreeTask.execute();

		checkoutDesc.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				checkoutDesc.setCursorVisible(true);
				return false;
			}
		});

		signatureImage = (ImageView)rootView1.findViewById(R.id.signature_image);
		if (Singleton.getInstance().proposalList.size() > 0) {
			signatureImage.setVisibility(View.GONE);
			penLayout.setBackgroundColor(getResources().getColor(
					R.color.gray_list1_bg));
			if (Singleton.getInstance().proposalList.get(0)
					.getSignatureExists().equals("False")) {
				hasSign = false;
			} else {
				penLayout.setBackgroundColor(getResources().getColor(
						R.color.white));
				hasSign = true;
				signatureImage.setVisibility(View.VISIBLE);
				imageLoader.displayImage(Singleton.getInstance().proposalList
						.get(0).getSignatureURL(), signatureImage, options);
			}
		}

		penLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (!hasSign) {
					penLayout.setVisibility(View.GONE);
					mContent.setVisibility(View.VISIBLE);
				}

				return false;
			}
		});
		checkoutClear = (Button)rootView1.findViewById(R.id.checkout_clear);
		checkoutClear.setTypeface(droidSans);
		checkoutClear.setOnClickListener(this);

		return rootView1;
	}

	public class AgreeTask extends AsyncTask<Void, Integer, Void> {

		JSONObject responseJson;
		ServiceHelper serviceHelper;
		ArrayList<JSONObject> mRequestJson = new ArrayList<JSONObject>();
		JSONArray localJsonArray = new JSONArray();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			serviceHelper = new ServiceHelper(context);
			responseJson = new JSONObject();
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
					dialog = null;
				} else {
					dialog = null;
				}
			}
			dialog = new ActivityIndicator(context);
			dialog.show();

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), Constants.DEALER_SETTING_URL + "?"
							+ Constants.SAVE_CALL_DEALERID + "=" + dealerId,
							Constants.REQUEST_TYPE_GET);

			return null;
		}

		@Override
		protected void onPostExecute(Void Result) {
			super.onPostExecute(Result);

			if (responseJson != null) {

				if (responseJson.has(Constants.DEALER_SETTING)) {

					try {
						localJsonArray = responseJson
								.getJSONArray(Constants.DEALER_SETTING);
						for (int i = 0; i < localJsonArray.length(); i++) {
							JSONObject jobj = localJsonArray.getJSONObject(0);
							statement = jobj.getString("SalesAppContractText");

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					checkoutAgree.setText(statement);
				} else {
					Toast.makeText(context, Constants.TOAST_CONNECTION_ERROR,
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(context, Constants.TOAST_NO_DATA,
						Toast.LENGTH_SHORT).show();
			}
			dissmissDialog();
		}
	}

	public void dissmissDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.checkout_clear:
			m_signature.clear();
			break;

		case R.id.checkout_continue:
			if (!m_signature.isNull() || hasSign) {
				if (checkoutCb.isChecked()) {
					Constants.isCheckOut = true;
					mView.setDrawingCacheEnabled(true);
					m_signature.save(mView);
					chtrask = new CheckoutAsyncTask(context,dealerId,customerId,employeeId,apptResultId);
					chtrask.execute();

				} else {
					Toast.makeText(context,
							"Please accept the terms & conditions.",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(context, "Please register your signature.",
						Toast.LENGTH_SHORT).show();
			}

		default:
			break;
		}

	}

	public void onDestroy() {
		super.onDestroy();
		if (agreeTask != null) {
			agreeTask.cancel(true);
			agreeTask = null;
		}
		if (chtrask != null) {
			chtrask.cancel(true);
			chtrask = null;
		}
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.cancel();
			}
			dialog = null;
		}
	}
}
