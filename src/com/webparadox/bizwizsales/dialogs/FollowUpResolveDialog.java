package com.webparadox.bizwizsales.dialogs;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.CustomerFollowUpsModel;

public class FollowUpResolveDialog extends Dialog implements OnClickListener {

	Typeface droidSans, droidSansBold;
	int position;
	ArrayList<CustomerFollowUpsModel> cusFollowupModelList;
	Context mContext;
	Boolean isResolved = false;
	ActivityIndicator pDialog;
	ReloadFollowup reloadFollowup;
	ServiceHelper serviceHelper;
	JSONObject responseJson;
	ArrayList<JSONObject> mRequestJson;
	JSONObject localJsonObject;
	String status, cusId, empId, dealerId, followupId, resolvedNote;
	EditText followUpResolveCompletionNote;
	public ResolveTask resolveTask;

	public interface ReloadFollowup {

		void reloadFollowups();
	}

	public FollowUpResolveDialog(Context context, int position,
			String dealerID, String customerId, String employeeID,
			String followUpId) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		droidSans = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans-Bold.ttf");
		this.cusFollowupModelList = Singleton.getInstance().cusFollowupModelList;
		this.position = position;
		this.reloadFollowup = (ReloadFollowup) mContext;

		this.cusId = customerId;
		this.empId = employeeID;
		this.dealerId = dealerID;
		this.followupId = followUpId;
		serviceHelper = new ServiceHelper(this.mContext);
		showFollowUpResolveDialog();
	}

	@SuppressWarnings("deprecation")
	public void showFollowUpResolveDialog() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// getWindow().setFlags(LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT);
		// getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		WindowManager.LayoutParams WMLP = getWindow().getAttributes();
		WMLP.gravity = Gravity.CENTER;
		WMLP.dimAmount = 0.7f;
		getWindow().setAttributes(WMLP);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		// dialog.getWindow().setBackgroundDrawable(new
		// ColorDrawable(android.graphics.Color.TRANSPARENT));
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		setContentView(R.layout.followup_resolve_dialog);
		TextView followUpResolveDate = (TextView) findViewById(R.id.followup_resolve_date);
		followUpResolveDate.setTypeface(droidSansBold);
		followUpResolveDate
				.setText(cusFollowupModelList.get(position).FollowUpDate);
		TextView followUpResolveNote = (TextView) findViewById(R.id.followup_resolve_note);
		followUpResolveNote.setTypeface(droidSansBold);
		TextView followUpResolveCompletion = (TextView) findViewById(R.id.followup_resolve_completion);
		followUpResolveCompletion.setTypeface(droidSansBold);
		followUpResolveCompletionNote = (EditText) findViewById(R.id.followup_resolve_completion_note);
		followUpResolveCompletionNote.setTypeface(droidSans);
		followUpResolveCompletionNote.setText(cusFollowupModelList
				.get(position).FollowupResolvedNotes);
		TextView followUpNote = (TextView) findViewById(R.id.followup_resolve_followup_note);
		followUpNote.setTypeface(droidSans);
		followUpNote.setText(cusFollowupModelList.get(position).FollowupNotes);
		final Button followupResolveClose = (Button) findViewById(R.id.followup_resolve_close_btn);
		followupResolveClose.setTypeface(droidSansBold);
		followupResolveClose.setOnClickListener(this);

		final Button followupResolveBtn = (Button) findViewById(R.id.followup_resolve_resolve_btn);
		followupResolveBtn.setTypeface(droidSansBold);
		followupResolveBtn.setOnClickListener(this);
		followupResolveBtn.setBackgroundDrawable(mContext.getResources()
				.getDrawable(R.drawable.resolve_disable_background));
		followupResolveBtn.setClickable(false);
		if (cusFollowupModelList.get(position).FollowupIsCompleted
				.equals(Constants.COMPLETED)) {
			isResolved = true;
			followUpResolveCompletionNote.setEnabled(false);
			followUpResolveCompletionNote.setText(cusFollowupModelList
					.get(position).FollowupResolvedNotes);
			followupResolveBtn.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.resolve_disable_background));
			followupResolveBtn.setClickable(false);
		}

		ScrollView view = (ScrollView) findViewById(R.id.followup_dialog_scrollview);
		view.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.requestFocusFromTouch();
				return false;
			}
		});
		followUpResolveCompletionNote.clearFocus();
		// followUpNote.setMovementMethod(new ScrollingMovementMethod());
		followUpResolveCompletionNote.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				followUpResolveCompletionNote.setCursorVisible(true);
				return false;
			}
		});
		followUpResolveCompletionNote.addTextChangedListener(new TextWatcher() {

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().trim().length() > 0) {
					followupResolveBtn.setClickable(true);
					followupResolveBtn.setBackgroundDrawable(mContext
							.getResources().getDrawable(
									R.drawable.selector_prospect_save_button));
				} else {
					followupResolveBtn.setClickable(false);
					followupResolveBtn.setBackgroundDrawable(mContext
							.getResources().getDrawable(
									R.drawable.resolve_disable_background));
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.followup_resolve_close_btn:
			dismiss();

			break;
		case R.id.followup_resolve_resolve_btn:
			if (isResolved) {

			} else {
				resolvedNote = followUpResolveCompletionNote.getText()
						.toString();
				if (resolvedNote.length() > 0) {
					mRequestJson = new ArrayList<JSONObject>();
					final JSONObject reqObj_data = new JSONObject();
					try {
						reqObj_data
								.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
						reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, empId);
						reqObj_data.put(Constants.JSON_KEY_CUSTOMER_ID, cusId);
						reqObj_data.put(Constants.JSON_KEY_FOLLOWUP_ID,
								followupId);
						reqObj_data.put(
								Constants.JSON_KEY_FOLLOWUP_RESOLVED_NOTE,
								resolvedNote);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mRequestJson.add(reqObj_data);
					resolveTask = new ResolveTask();
					resolveTask.execute();
				} else {

				}

			}

			break;
		default:
			break;
		}

	}

	public class ResolveTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected void onPreExecute() {
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				pDialog = null;
			}
			pDialog = new ActivityIndicator(mContext);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), Constants.RESOLVE_FOLLOWUP_URL,
					Constants.REQUEST_TYPE_POST);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			try {
				dismiss();
				if (responseJson != null) {
					if (responseJson.has(Constants.RESOLVE_FOLLOEUP)) {
						try {
							localJsonObject = responseJson
									.getJSONObject(Constants.RESOLVE_FOLLOEUP);
							status = localJsonObject
									.getString(Constants.KEY_ADDPROSPECT_STATUS);
						} catch (JSONException e) { // TODO Auto-generated catch
													// block
							e.printStackTrace();
						}
						if (status.equals(Constants.VALUE_SUCCESS)) {
							// dismiss();
							reloadFollowup.reloadFollowups();

						} else {

							Toast.makeText(mContext,
									Constants.TOAST_CONNECTION_ERROR,
									Toast.LENGTH_SHORT).show();
						}

					} else {
						Toast.makeText(mContext,
								Constants.TOAST_CONNECTION_ERROR,
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(mContext, Constants.TOAST_INTERNET,
							Toast.LENGTH_SHORT).show();

				}
			} catch (Exception e) {
				// TODO: handle exception
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
		}
	}
}
