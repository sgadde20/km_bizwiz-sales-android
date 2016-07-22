package com.webparadox.bizwizsales;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.webparadox.bizwizsales.adapter.SalesProcessProductGridViewAdapter;
import com.webparadox.bizwizsales.asynctasks.GetProposalAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SaveSummaryAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SpProductsAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SummaryEmailAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.fragment.EditProposalFragment;
import com.webparadox.bizwizsales.fragment.SalesProcessProductSubCategoryFragment;
import com.webparadox.bizwizsales.fragment.UpdateProposalFragment;
import com.webparadox.bizwizsales.helper.Helper;
import com.webparadox.bizwizsales.helper.ObjectSerializer;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.SpProductSubCatAndMaterialModel;


public class NewProposalSummaryActivity extends Activity implements OnClickListener {
	ImageView summary_back_icon,summary_image_back_icon;
	static ArrayList<Integer> changedItems = new ArrayList<Integer>();
	static ArrayList<Integer> quatitychangedItems = new ArrayList<Integer>();
	static ArrayList<String> amountCheck = new ArrayList<String>();
	static ArrayList<String> quatityCheck = new ArrayList<String>();
	static Boolean isCheck = false;
	static Boolean canSave = false;
	static EditText summaryEd;
	static String description = "";
	static double amount = 0;
	SaveSummaryAsyncTask saveTask;
	public static String dealerId, empId, appointmentResultId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.fragment_layout);
		summary_back_icon = (ImageView)findViewById(R.id.summary_back_icon);
		summary_back_icon.setOnClickListener(this);
		summary_image_back_icon = (ImageView)findViewById(R.id.summary_image_back_icon);
		summary_image_back_icon.setOnClickListener(this);
	}

	public static class TitlesFragment extends Fragment implements OnItemClickListener{
		
		SharedPreferences preferenceSettings;
		SharedPreferences.Editor settingsEditor;
		SharedPreferences userData;
		Button summary_email_btn;
		SummaryEmailAsyncTask summaryEmailTask;  
		GetProposalAsyncTask getProposalAsyncTask;
		Context ctx = null;
		Typeface droidSans, droidSansBold;
		TextView summaryHeader, summaryTotal, summaryDec,proposalText;
		ImageView proposalSign;
		Button summary_add_more_btn,summary_checkout_btn;
		ListView summaryListview;
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.loading_indicator)
		.showImageOnFail(R.drawable.ic_error)
		.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
		.cacheOnDisk(true).considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565).build();
		SaveSummaryAsyncTask saveTask;
		ActivityIndicator mDialog;
		public static ProposalSummaryAdapter proposalSummaryAdapter;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			ctx = this.getActivity();
			preferenceSettings = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
			settingsEditor = preferenceSettings.edit();

			userData = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
			dealerId = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
			empId = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
			appointmentResultId = userData.getString(Constants.KEY_APPT_RESULT_ID,"0");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			final View rootView = inflater.inflate(R.layout.proposal_summary_activity, container,false);
			droidSans = Typeface.createFromAsset(ctx.getAssets(),
					"DroidSans.ttf");
			droidSansBold = Typeface.createFromAsset(ctx.getAssets(),
					"DroidSans-Bold.ttf");
			proposalText = (TextView)rootView.findViewById(R.id.proposal_signature);
			proposalText.setTypeface(droidSansBold);
			proposalSign = (ImageView)rootView.findViewById(R.id.proposal_sign);
			summaryEd = (EditText)rootView.findViewById(R.id.summary_dec_ed);
			summaryEd.setTypeface(droidSans);
			summaryEd.clearFocus();
			summaryListview = (ListView)rootView.findViewById(R.id.summary_listView);
			summaryListview.setOnItemClickListener(this);
			summaryTotal = (TextView)rootView.findViewById(R.id.summary_total);
			summaryTotal.setTypeface(droidSansBold, Typeface.BOLD);

			summary_add_more_btn = (Button)rootView.findViewById(R.id.summary_add_more_btn);
			summary_add_more_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
						DetailsFragment f = new DetailsFragment();
						FragmentTransaction ft = getFragmentManager()
								.beginTransaction();
						ft.replace(R.id.details, f);
						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						ft.commit();
					}else{
						Constants.isProposalList = false;
						Constants.isSelectProduct = true;
						Intent cusAppointIntent = new Intent(ctx,
								CustomerAppointmentsActivity.class);
						cusAppointIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(cusAppointIntent);
						getActivity().finish();
					}
				}
			});


			summary_checkout_btn = (Button)rootView.findViewById(R.id.summary_checkout_btn);
			summary_checkout_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					isCheck = true;
					if (Singleton.getInstance().quotedProductModel.size() > 0) {
						canSave = true;
						for (int i = 0; i < Singleton.getInstance().quotedProductModel
								.size(); i++) {
							if (Singleton.getInstance().quotedProductModel.get(i).QuotedAmount
									.length() < 1
									|| Singleton.getInstance().quotedProductModel
									.get(i).Quantity.length() < 1) {
								canSave = false;
							}
						}
						if (canSave) {
							if (Singleton.getInstance().proposalList.size() > 0) {
								if (Singleton.getInstance().proposalList.get(0)
										.getSignatureExists().equalsIgnoreCase("false")) {
									description = summaryEd.getText().toString();
									saveTask = new SaveSummaryAsyncTask(ctx,
											saveRequest(), String.valueOf(amount));
									saveTask.execute();


								} else if (Singleton.getInstance().proposalList.get(0)
										.getSignatureExists().equalsIgnoreCase("true")) {
									Toast.makeText(
											ctx,
											"Your signature is already exists in proposal.",
											Toast.LENGTH_SHORT).show();
									Constants.isCheckOut = true;
									Constants.isProposalList = false;
									Constants.isSelectProduct = false;
									Intent cusAppo = new Intent(ctx,
											CustomerAppointmentsActivity.class);
									cusAppo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
											| Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(cusAppo);
									getActivity().finish();
								}
							}
						} else {
							Toast.makeText(ctx,
									"Please enter valid quantity & amount. ",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(ctx, "Please select atleast one product",
								Toast.LENGTH_SHORT).show();
					}
				}
			});


			summary_email_btn = (Button)rootView.findViewById(R.id.summary_email_btn);
			summary_email_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(Singleton.getInstance().quotedProductModel.size() > 0) {
						summaryEmailTask = new SummaryEmailAsyncTask(ctx, dealerId,appointmentResultId);
						summaryEmailTask.execute();
					}else {
						Toast.makeText(ctx, "No data found", Toast.LENGTH_LONG).show();
					}
				}
			});

			getProposalAsyncTask = new GetProposalAsyncTask(new FragmentCallback() {
				@Override
				public void onTaskDone() {
					doneGetProposalAsyncTask();
				}
			},dealerId,appointmentResultId,"TitlesFragment",ctx);
			getProposalAsyncTask.execute();

			return rootView;
		}


		private void doneGetProposalAsyncTask(){
			proposalSign.setVisibility(View.GONE);
			proposalText.setVisibility(View.GONE);
			if (Singleton.getInstance().proposalList.size() > 0) {
				if (Singleton.getInstance().proposalList.get(0).getWorkOrderNotes() != null
						&& Singleton.getInstance().proposalList.get(0)
						.getWorkOrderNotes().length() > 0) {
					summaryEd.setText(Singleton.getInstance().proposalList.get(0)
							.getWorkOrderNotes());
				}
				if (!Singleton.getInstance().proposalList.get(0)
						.getSignatureExists().equals("False")) {
					proposalSign.setVisibility(View.VISIBLE);
					proposalText.setVisibility(View.VISIBLE);
					imageLoader.displayImage(Singleton.getInstance().proposalList
							.get(0).getSignatureURL(), proposalSign, options);

				}
			}
			setTotal();
			proposalSummaryAdapter = new ProposalSummaryAdapter();
			summaryListview.setAdapter(proposalSummaryAdapter);
			Helper.getListViewSize(summaryListview);

			if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				DetailsFragment f = new DetailsFragment();
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.replace(R.id.details, f);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
			}
		}

		public class ProposalSummaryAdapter extends BaseAdapter {

			Context mContext;
			LayoutInflater mLayout;
			Typeface droidSans, droidSansBold;
			protected ImageLoader imageLoader = ImageLoader.getInstance();
			DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageOnFail(R.drawable.ic_error)
			.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
			.cacheOnDisk(true).considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();
			double amount = 0;

			public ProposalSummaryAdapter() {
				this.mContext = ctx;
				droidSans = Typeface.createFromAsset(mContext.getAssets(),
						"DroidSans.ttf");
				droidSansBold = Typeface.createFromAsset(mContext.getAssets(),
						"DroidSans-Bold.ttf");
				imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
				for (int po = 0; po < Singleton.getInstance().quotedProductModel
						.size(); po++) {
					amountCheck.add(Singleton.getInstance().quotedProductModel
							.get(po).QuotedAmount.replace("$", ""));
				}
				for (int po = 0; po < Singleton.getInstance().quotedProductModel
						.size(); po++) {
					quatityCheck.add(Singleton.getInstance().quotedProductModel
							.get(po).Quantity);
				}
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return Singleton.getInstance().quotedProductModel.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return Singleton.getInstance().quotedProductModel.get(position).BoundproductId;
			}

			@Override
			public int getViewTypeCount() {

				if (getCount() != 0)
					return getCount();

				return 1;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public View getView(final int initialposition, View convertView,
					ViewGroup parent) {
				// TODO Auto-generated method stub
				final ViewHolder holder;

				convertView = null;
				if (convertView == null) {
					holder = new ViewHolder();
					mLayout = (LayoutInflater) mContext
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = mLayout.inflate(
							R.layout.proposal_summary_adapter, parent, false);

					holder.summaryName = (TextView) convertView
							.findViewById(R.id.summary_name);
					holder.summaryNote = (TextView) convertView
							.findViewById(R.id.summary_note);
					holder.summaryAmount = (EditText) convertView
							.findViewById(R.id.summary_amount);
					holder.summaryImage = (ImageView) convertView
							.findViewById(R.id.summary_image);
					holder.summaryQtyEd = (EditText) convertView
							.findViewById(R.id.summary_qty_count);
					holder.summaryQty = (TextView) convertView
							.findViewById(R.id.summary_qty);
					holder.summaryAmount.setTag(initialposition);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.summaryQty.setTypeface(droidSans);
				holder.summaryQtyEd.setTypeface(droidSans);
				holder.summaryNote.setTypeface(droidSans);
				final int position = (Integer) holder.summaryAmount.getTag();
				if (Singleton.getInstance().quotedProductModel.get(position).BoundProductNotes
						.length() > 0) {
					holder.summaryNote.setVisibility(View.VISIBLE);
					holder.summaryNote
					.setText(Singleton.getInstance().quotedProductModel
							.get(position).BoundProductNotes);
				} else {
					holder.summaryNote.setVisibility(View.GONE);
				}
				holder.summaryName
				.setText(Singleton.getInstance().quotedProductModel
						.get(position).ProductQuoted);
				holder.summaryName.setTypeface(droidSansBold);
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(2);

				String amountValue = addDollerSymbol(Singleton.getInstance().quotedProductModel
						.get(position).QuotedAmount.replace("$", ""));
				holder.summaryAmount.setText(amountValue);
				holder.summaryAmount.setId(position);

				holder.summaryAmount.setTypeface(droidSansBold);
				imageLoader
				.displayImage(Singleton.getInstance().quotedProductModel
						.get(position).ProductImageURL,
						holder.summaryImage, options);
				holder.summaryQtyEd.setId(position);
				holder.summaryQtyEd
				.setText(Singleton.getInstance().quotedProductModel
						.get(position).Quantity);

				holder.summaryQtyEd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
							EditProposalFragment f = new EditProposalFragment();
							FragmentTransaction ft = getFragmentManager().beginTransaction();
							EditProposalFragment.newInstance(position,"summary");
							ft.replace(R.id.details, f);
							ft.addToBackStack(null);
							ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
							ft.commit();
						}else{
							Intent editProposalActivity = new Intent(ctx,
									EditProposalActivity.class);
							editProposalActivity.putExtra("Position", position);
							editProposalActivity.putExtra("From", "summary");
							startActivity(editProposalActivity);
						}
					}
				});
				holder.summaryAmount.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
							EditProposalFragment f = new EditProposalFragment();
							FragmentTransaction ft = getFragmentManager().beginTransaction();
							EditProposalFragment.newInstance(position,"summary");
							ft.replace(R.id.details, f);
							ft.addToBackStack(null);
							ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
							ft.commit();
						}else{
							Intent editProposalActivity = new Intent(ctx,
									EditProposalActivity.class);
							editProposalActivity.putExtra("Position", position);
							editProposalActivity.putExtra("From", "summary");
							startActivity(editProposalActivity);
						}
					}
				});

				holder.summaryAmount.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						final int position = holder.summaryAmount.getId();
						final EditText Caption = (EditText) holder.summaryAmount;
						Singleton.getInstance().quotedProductModel.get(position).QuotedAmount = Caption
								.getText().toString().replace("$", "");
						if (Singleton.getInstance().quotedProductModel
								.get(position).QuotedAmount.length() > 1) {
							setTotal();
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						if (!s.toString().startsWith("$")) {
							holder.summaryAmount.setText("$");
							Selection.setSelection(holder.summaryAmount.getText(),
									holder.summaryAmount.getText().length());

						}

					}

				});
				holder.summaryQtyEd.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						final int position = holder.summaryQtyEd.getId();
						final EditText Caption = (EditText) holder.summaryQtyEd;
						Singleton.getInstance().quotedProductModel.get(position).Quantity = Caption
								.getText().toString();
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});
				return convertView;
			}

			class ViewHolder {
				public TextView summaryName;
				public EditText summaryAmount;
				public ImageView summaryImage;
				public EditText summaryQtyEd;
				public TextView summaryQty;
				public TextView summaryNote;
			}

		}


		public double setTotal() {
			amount = 0;
			for (int i = 0; i < Singleton.getInstance().quotedProductModel.size(); i++) {

				Log.d("Previous Amount", amount + "");
				amount = amount
						+ Double.parseDouble(Singleton.getInstance().quotedProductModel
								.get(i).QuotedAmount.toString().replace(",", ""));
			}
			String amountValue = addDollerSymbol(String.valueOf(amount).replace(",", "").replace("$", ""));
			summaryTotal.setText(amountValue);
			settingsEditor.putString("Amount", amountValue);
			settingsEditor.commit();
			return amount;
		}


		public String addDollerSymbol(String value) {
			if (Double.parseDouble(value.replace(",", "").replace("-", "")) < 1) {
				NumberFormat formatter = new DecimalFormat("#0.00");
				value = formatter.format(Double.parseDouble(value));
				value = "$" + value;
			}else{
				if (value.contains("-")) {

					double amount = Double.parseDouble(value
							.substring(1, value.length()).replace(",", "")
							.replace("$", ""));
					DecimalFormat formatter = new DecimalFormat("#,###.00");
					String formatted = formatter.format(amount);

					value = value.subSequence(0, 1) + "$" + formatted;
				} else {
					double amount = Double.parseDouble(value.replace(",", "").replace(
							"$", ""));
					DecimalFormat formatter = new DecimalFormat("#,###.00");
					String formatted = formatter.format(amount);

					value = "$" + formatted;
				}
			}	
			return value;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				EditProposalFragment f = new EditProposalFragment();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				EditProposalFragment.newInstance(position,"summary");
				ft.replace(R.id.details, f);
				ft.addToBackStack(null);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
			}else{
				Intent editProposalActivity = new Intent(ctx,
						EditProposalActivity.class);
				editProposalActivity.putExtra("Position", position);
				editProposalActivity.putExtra("From", "summary");
				startActivity(editProposalActivity);
			}
		}

		@Override
		public void onDestroyView() {
			super.onDestroyView();
			if (summaryEmailTask != null) {
				summaryEmailTask.cancel(true);
				summaryEmailTask = null;
			}
			if (getProposalAsyncTask != null) {
				getProposalAsyncTask.dismissDialog();
				getProposalAsyncTask.cancel(true);
				getProposalAsyncTask = null;
			}
			if (saveTask != null) {
				saveTask.cancel(true);
				saveTask = null;
			}
			dissmissDismiss();
		}

		public void dissmissDismiss(){
			if(mDialog !=null){
				if(mDialog.isShowing()){
					mDialog.dismiss();
				}
				mDialog=null;
			}
		}

		public interface FragmentCallback {
			public void onTaskDone();
		}
	}

	public static class DetailsFragment extends Fragment {
		SharedPreferences preferenceSettings;
		SharedPreferences.Editor settingsEditor;
		SharedPreferences userData;
		String dealerId, empId, appointmentResultId;
		Context ctx = null;
		Button generateProposal;
		GridView gridViewProductsCategory,gridViewRecentProducts;
		SpProductsAsyncTask spProductsAsyncTask;
		public static ArrayList<SpProductSubCatAndMaterialModel> mRecentProductsList = new ArrayList<SpProductSubCatAndMaterialModel>();
		ObjectSerializer mObjetserilizer = new ObjectSerializer();
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			ctx = this.getActivity();
			preferenceSettings = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
			settingsEditor = preferenceSettings.edit();

			userData = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
			dealerId = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
			empId = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
			appointmentResultId = userData.getString(Constants.KEY_APPT_RESULT_ID,"0");
		}

		@SuppressWarnings({ "unchecked", "static-access" })
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			final View rootView1 = inflater.inflate(R.layout.products_picker_layout, container,false);
			LinearLayout layoutcatHeader;
			layoutcatHeader = (LinearLayout)rootView1.findViewById(R.id.productpickerLayout);
			layoutcatHeader.setVisibility(View.VISIBLE);

			generateProposal = (Button) rootView1.findViewById(R.id.generate_proposal_btn);
			generateProposal.setVisibility(View.GONE);

			gridViewProductsCategory = (GridView) rootView1.findViewById(R.id.gridViewProducts);
			gridViewProductsCategory.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					SalesProcessProductSubCategoryFragment f = new SalesProcessProductSubCategoryFragment();
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					SalesProcessProductSubCategoryFragment.newInstance(position,((TextView) view.findViewById(R.id.textViewProductName)).getText().toString());
					ft.replace(R.id.details, f);
					ft.addToBackStack(null);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					ft.commit();
				}
			});
			if (preferenceSettings.contains(Constants.PREF_RECENT_PRODUCT_KEY)) {
				mRecentProductsList.clear();
				try {
					mRecentProductsList = (ArrayList<SpProductSubCatAndMaterialModel>) mObjetserilizer
							.deserialize(preferenceSettings
									.getString(
											Constants.PREF_RECENT_PRODUCT_KEY,
											mObjetserilizer
											.serialize(new ArrayList<SpProductSubCatAndMaterialModel>())));
					Collections.reverse(mRecentProductsList);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

			spProductsAsyncTask = new SpProductsAsyncTask(ctx,gridViewProductsCategory, dealerId);
			spProductsAsyncTask.execute();

			gridViewRecentProducts = (GridView) rootView1.findViewById(R.id.gridViewRecentProducts);
			gridViewRecentProducts.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					UpdateProposalFragment f = new UpdateProposalFragment();
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					UpdateProposalFragment.newInstance(mRecentProductsList.get(position).mSubcategoryName,
							mRecentProductsList.get(position).mSubcategoryId,
							mRecentProductsList.get(position).mMaterialName,
							mRecentProductsList.get(position).mMaterialId,
							mRecentProductsList.get(position).mProductDescription,
							mRecentProductsList.get(position).mProductImageURL,
							mRecentProductsList.get(position).mProductVideoURL,
							mRecentProductsList.get(position).mUnitSellingPrice,
							mRecentProductsList.get(position).mProductType);
					ft.replace(R.id.details, f);
					ft.addToBackStack(null);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					ft.commit();
				}
			});
			SalesProcessProductGridViewAdapter salesProcessProductGridAdapter = new SalesProcessProductGridViewAdapter(
					ctx, mRecentProductsList, true);
			gridViewRecentProducts.setAdapter(salesProcessProductGridAdapter);
			return rootView1;
		}

		@Override
		public void onDestroyView() {
			super.onDestroyView();

			if (spProductsAsyncTask != null) {
				spProductsAsyncTask.dismissDialog();
				spProductsAsyncTask.cancel(true);
				spProductsAsyncTask = null;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.summary_back_icon:
			checkAmount("isBack");
			break;
		case R.id.summary_image_back_icon:
			checkAmount("isHome");
			break;

		default:
			break;
		}		
	}

	public void checkAmount(String fromTExt) {
		isCheck = false;
		changedItems.clear();
		for (int ic = 0; ic < Singleton.getInstance().quotedProductModel.size(); ic++) {
			if (amountCheck
					.get(ic)
					.equals(Singleton.getInstance().quotedProductModel.get(ic).QuotedAmount
							.replace("$", ""))) {
			} else {
				changedItems.add(ic);
			}
		}

		quatitychangedItems.clear();
		for (int ic = 0; ic < Singleton.getInstance().quotedProductModel.size(); ic++) {
			if (quatityCheck
					.get(ic)
					.equals(Singleton.getInstance().quotedProductModel.get(ic).Quantity)) {
			} else {
				quatitychangedItems.add(ic);
			}
		}

		String oldNotes = "";
		if (Singleton.getInstance().proposalList.size() > 0) {
			oldNotes = Singleton.getInstance().proposalList.get(0)
					.getWorkOrderNotes();
		}

		if (changedItems.size() > 0 || quatitychangedItems.size() > 0
				|| !(oldNotes.equalsIgnoreCase(summaryEd.getText().toString()))) {
			canSave = true;
			for (int i = 0; i < Singleton.getInstance().quotedProductModel
					.size(); i++) {
				if (Singleton.getInstance().quotedProductModel.get(i).QuotedAmount
						.length() < 1
						|| Singleton.getInstance().quotedProductModel.get(i).Quantity
						.length() < 1) {
					canSave = false;
				}
			}
			if (canSave) {
				description = summaryEd.getText().toString();
				saveTask = new SaveSummaryAsyncTask(NewProposalSummaryActivity.this, saveRequest(),
						String.valueOf(amount), fromTExt);
				saveTask.execute();
			} else {
				Constants.isProposalList = true;
				Constants.isSelectProduct = false;
				Intent cusAppIntent = new Intent(NewProposalSummaryActivity.this,
						CustomerAppointmentsActivity.class);
				cusAppIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(cusAppIntent);
				finish();
			}
		} else {
			if (fromTExt.equals("isHome")) {
				Intent backIntent = new Intent(NewProposalSummaryActivity.this,
						MainActivity.class);
				backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(backIntent);
				finish();

			} else {
				Constants.isProposalList = true;
				Constants.isSelectProduct = false;
				Intent cusAppIntent = new Intent(NewProposalSummaryActivity.this,
						CustomerAppointmentsActivity.class);
				cusAppIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(cusAppIntent);
				finish();
			}
		}
	}

	public static JSONObject saveRequest() {

		Log.d("size", Singleton.getInstance().quotedProductModel.size() + "");
		JSONArray mRequestJson = new JSONArray();

		JSONObject ProductsData = new JSONObject();
		JSONObject product = new JSONObject();

		for (int l = 0; l < Singleton.getInstance().quotedProductModel.size(); l++) {
			// if (changedItems.contains(l) || quatitychangedItems.contains(l)
			// || isCheck) {
			// }

			JSONObject reqObj_data = new JSONObject();
			try {
				reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
				reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, empId);
				reqObj_data.put(Constants.KEY_APPT_RESULT_ID,
						appointmentResultId);
				reqObj_data
						.put(Constants.JSON_KEY_SALES_PROCESS_MATERIAL_ID,
								Singleton.getInstance().quotedProductModel
										.get(l).MaterialId);
				reqObj_data
						.put(Constants.QUOTED_QUANTITY,
								Singleton.getInstance().quotedProductModel
										.get(l).Quantity);
				reqObj_data
						.put(Constants.QUOTED_AMOUNT,
								Singleton.getInstance().quotedProductModel
										.get(l).QuotedAmount.replace("$", "")
										.replace(",", ""));
				reqObj_data
						.put(Constants.BOUND_PRODUCT_ID,
								Singleton.getInstance().quotedProductModel
										.get(l).BoundproductId);
				reqObj_data
						.put(Constants.JSON_KEY_FOLLOWUP_NOTES,
								Singleton.getInstance().quotedProductModel
										.get(l).BoundProductNotes);
				reqObj_data
						.put(Constants.JSON_KEY_SALES_PROCESS_PRODUCT_TYPE,
								Singleton.getInstance().quotedProductModel
										.get(l).ProductType);
				if (Singleton.getInstance().quotedProductModel.get(l).ProductType != null) {
					Log.d("Value = ",
							""
									+ Singleton.getInstance().quotedProductModel
											.get(l).ProductType);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mRequestJson.put(reqObj_data);

		}
		try {
			product.put("PRODUCTS", mRequestJson);
			JSONObject reqObj_data = new JSONObject();
			reqObj_data.put(Constants.DESCRIPTION, description);
			reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
			reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, empId);
			reqObj_data.put(Constants.KEY_APPT_RESULT_ID, appointmentResultId);

			JSONArray mRequestJson2 = new JSONArray();
			mRequestJson2.put(reqObj_data);
			product.put("WORKDESCRIPTION", mRequestJson2);

			ProductsData.put("ProductsData", product);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("res", ProductsData.toString());
		return ProductsData;

	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (saveTask != null) {
			saveTask.cancel(true);
			saveTask = null;
		}
	}
}
