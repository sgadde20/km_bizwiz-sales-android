package com.webparadox.bizwizsales.fragment;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.asynctasks.SaveSummaryAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ObjectSerializer;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.libraries.Utilities;
import com.webparadox.bizwizsales.models.ProposalCartModel;
import com.webparadox.bizwizsales.models.SpProductSubCatAndMaterialModel;

public class UpdateProposalFragment extends Fragment implements OnClickListener{

	static String subCatName,subCatId,materialName,materialId,productDescription,productImageURL,productVideoURL,unitSellingPrice,productType;
	Context context = null;
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	String dealerID = "";
	String employeeID = "";
	String employeeName = "";
	String appointmentResultId = "";
	static Typeface droidSans,droidSansBold;
	SharedPreferences preferenceSettings;
	SharedPreferences.Editor settingsEditor;
	boolean mShowPrice = true,isAddnew = false;
	ArrayList<SpProductSubCatAndMaterialModel> mRecentProductsList = new ArrayList<SpProductSubCatAndMaterialModel>();
	SpProductSubCatAndMaterialModel mMaterial = new SpProductSubCatAndMaterialModel();
	ObjectSerializer mObjetserilizer = new ObjectSerializer();
	ScrollView gestureLayout;
	TextView txtMaterialName,txtProductDescription,txtProposalAmount,txtAmount;
	ImageView imgPlayButton,imgThumbnail;
	Button btnUpdateProposal,btnAddNewProduct;
	EditText editTxtQuantity,editTxtAmount,editTxtNotes;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	.showImageOnLoading(R.drawable.ic_stub)
	.showImageOnFail(R.drawable.ic_error)
	.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
	.cacheOnDisk(true).considerExifParams(true)
	.bitmapConfig(Bitmap.Config.RGB_565).build();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	Boolean isOther = false;
	SmartSearchAsyncTask searchAsyncTask;
	ProposalCartModel proposalCart;
	SaveSummaryAsyncTask saveTask;

	public static UpdateProposalFragment newInstance(String SubCatName,
			String SubCatId,
			String MaterialName,
			String MaterialId,
			String ProductDescription,
			String ProductImageURL,
			String ProductVideoURL,
			String UnitSellingPrice,
			String ProductType) {
		UpdateProposalFragment f = new UpdateProposalFragment();
		subCatName = SubCatName;
		subCatId = SubCatId;
		materialName = MaterialName;
		materialId = MaterialId;
		productDescription = ProductDescription;
		productImageURL = ProductImageURL;
		productVideoURL = ProductVideoURL;
		unitSellingPrice = UnitSellingPrice;
		productType = ProductType;

		return f;	
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		userData = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		editor = userData.edit();
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		employeeName = userData.getString(Constants.KEY_EMPLOYEE_NAME, "");
		appointmentResultId = userData.getString(Constants.KEY_APPT_RESULT_ID,"0");
		droidSans = Typeface.createFromAsset(context.getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(context.getAssets(),"DroidSans-Bold.ttf");
		mMaterial.mSubcategoryName = subCatName;
		mMaterial.mSubcategoryId = subCatId;
		mMaterial.mMaterialName = materialName;
		mMaterial.mMaterialId = materialId;
		mMaterial.mProductDescription = productDescription;
		mMaterial.mProductImageURL = productImageURL;
		mMaterial.mProductVideoURL = productVideoURL;
		mMaterial.mUnitSellingPrice = unitSellingPrice;
		mMaterial.mProductType = productType;
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView1 = inflater.inflate(R.layout.activity_update_proposal, container,false);

		preferenceSettings = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, 0);
		settingsEditor = preferenceSettings.edit();
		if (preferenceSettings.contains(Constants.PREF_SWITCH_SHOW_PRICE)) {
			mShowPrice = preferenceSettings.getBoolean(
					Constants.PREF_SWITCH_SHOW_PRICE, false);
		}

		if (preferenceSettings.contains(Constants.PREF_RECENT_PRODUCT_KEY)) {
			mRecentProductsList.clear();
			try {

				mRecentProductsList = (ArrayList<SpProductSubCatAndMaterialModel>) mObjetserilizer
						.deserialize(preferenceSettings
								.getString(
										Constants.PREF_RECENT_PRODUCT_KEY,
										mObjetserilizer
										.serialize(new ArrayList<SpProductSubCatAndMaterialModel>())));
				Iterator<SpProductSubCatAndMaterialModel> iter = mRecentProductsList
						.iterator();
				boolean isPresent = false;
				while (iter.hasNext()) {
					SpProductSubCatAndMaterialModel str = iter.next();

					if (str.mMaterialId.equalsIgnoreCase(mMaterial.mMaterialId)) {
						isPresent = true;
					}
				}
				if (!isPresent) {
					if (mRecentProductsList.size() < 5) {
						mRecentProductsList.add(mMaterial);
					} else {
						mRecentProductsList.remove(0);
						mRecentProductsList.add(mMaterial);
					}
					settingsEditor.putString(Constants.PREF_RECENT_PRODUCT_KEY,
							ObjectSerializer.serialize(mRecentProductsList));
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else if (!preferenceSettings
				.contains(Constants.PREF_RECENT_PRODUCT_KEY)
				&& mRecentProductsList.size() == 0) {
			try {
				mRecentProductsList.add(mMaterial);
				settingsEditor.putString(Constants.PREF_RECENT_PRODUCT_KEY,
						mObjetserilizer.serialize(mRecentProductsList));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		settingsEditor.commit();

		RelativeLayout relativeLayout = (RelativeLayout)rootView1.findViewById(R.id.linearLayout1);
		relativeLayout.setVisibility(View.GONE);

		gestureLayout = (ScrollView)rootView1.findViewById(R.id.scrollView1);
		txtMaterialName = (TextView)rootView1.findViewById(R.id.textViewMaterialName);
		txtProductDescription = (TextView)rootView1.findViewById(R.id.textViewproductDescription);
		txtProposalAmount = (TextView) rootView1.findViewById(R.id.textViewproposalamount);
		txtAmount = (TextView)rootView1.findViewById(R.id.textViewAmount);
		txtMaterialName.setTypeface(droidSans);
		txtProductDescription.setTypeface(droidSans);
		txtProposalAmount.setTypeface(droidSansBold);
		txtAmount.setTypeface(droidSansBold);
		txtMaterialName.setText(mMaterial.mMaterialName);
		if (mMaterial.mProductDescription.equals("")
				|| mMaterial.mProductDescription.length() < 1) {
			txtProductDescription.setText("N/A");
		} else {
			txtProductDescription.setText(mMaterial.mProductDescription);
		}
		if (mShowPrice) {
			txtAmount.setText("$" + mMaterial.mUnitSellingPrice);
		} else {
			txtAmount.setText("*******");
		}

		imgPlayButton = (ImageView)rootView1.findViewById(R.id.imageViewVideoPlay);
		imgThumbnail = (ImageView)rootView1.findViewById(R.id.imageViewThumnail);
		btnUpdateProposal = (Button)rootView1.findViewById(R.id.buttonupdateProposal);
		btnAddNewProduct = (Button)rootView1.findViewById(R.id.buttonaddNew);
		btnUpdateProposal.setOnClickListener(this);
		btnAddNewProduct.setOnClickListener(this);
		imgPlayButton.setOnClickListener(this);
		setImageThumbnill();
		editTxtQuantity = (EditText)rootView1.findViewById(R.id.editTextQuantity);
		editTxtAmount = (EditText)rootView1.findViewById(R.id.editTextAmount);
		editTxtNotes = (EditText)rootView1.findViewById(R.id.editTextNotes);
		editTxtQuantity.requestFocus();
		editTxtQuantity.clearFocus();

		editTxtQuantity.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				float qty = (float) 0.00;
				if(Utilities.isNumeric(s.toString())){
					if (s.length() > 0) {
						qty = Float.parseFloat(s.toString());
					}
				}

				float unitPrice = Float.parseFloat(mMaterial.mUnitSellingPrice
						.replaceAll(",", ""));
				if (unitPrice > 0) {
					BigDecimal bd = new BigDecimal(("" + (qty * unitPrice)));
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					String valueAmount = String.valueOf(bd);
					editTxtAmount.setText(valueAmount);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				isOther = true;
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				isOther = false;
			}
		});

		editTxtAmount.addTextChangedListener(new TextWatcher() {
			private boolean mFormatting; // this is a flag which prevents the

			// stack overflow.

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!mFormatting) {
					mFormatting = true;
					String editValue = editTxtAmount.getText().toString();
					editValue = editValue.replace(".", "");
					editValue = editValue.replace("$", "");
					editValue = editValue.replaceAll("\\s+", "");
					editValue = editValue.replaceAll("[-.^:,()]", "");
					editValue = editValue.replaceAll("[^\\d.]", "");
					if (editValue.length() == 1) {
						editTxtAmount.setText("0.0" + editValue);
						editTxtAmount.setSelection(editTxtAmount.getText()
								.toString().length());
					} else if (editValue.length() == 2) {
						editTxtAmount.setText("0." + editValue);
						editTxtAmount.setSelection(editTxtAmount.getText()
								.toString().length());
					} else if (editValue.length() > 2) {
						int len = editValue.length();
						double amount = Double.parseDouble(editValue.substring(
								0, len - 2));
						NumberFormat formatter = new DecimalFormat(
								"###,###,###.##");
						System.out.println("The Decimal Value is: "
								+ formatter.format(amount));
						Log.d("The Decimal Value is: ",
								"$"
										+ formatter.format(amount)
										+ "."
										+ editValue.substring(len - 2,
												editValue.length()));

						editTxtAmount.setText(formatter.format(amount)
								+ "."
								+ editValue.substring(len - 2,
										editValue.length()));
						editTxtAmount.setSelection(editTxtAmount.getText()
								.toString().length());
					}
					mFormatting = false;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// for video icon display
		if (mMaterial.mProductVideoURL.equals("")) {
			imgPlayButton.setVisibility(View.GONE);
		}

		return rootView1;
	}

	public void showPrice() {
		if (mShowPrice) {
			mShowPrice = false;
			txtAmount.setText("*******");

		} else {
			mShowPrice = true;
			txtAmount.setText("$" + mMaterial.mUnitSellingPrice);
		}
	}

	public void setImageThumbnill() {
		imageLoader.displayImage(mMaterial.mProductImageURL, imgThumbnail,
				options);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageViewVideoPlay:
			openSequalVideoFile();
			break;
		case R.id.buttonupdateProposal:
			isAddnew = false;
			addToCart();
			break;
		case R.id.buttonaddNew:
			isAddnew = true;
			addToCart();
			break;
		}
	}

	public void onDestroy() {
		super.onDestroy();

		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
		if (saveTask != null) {
			saveTask.dissmissDialog();
			saveTask.cancel(true);
			saveTask = null;
		}

	}

	private void openSequalVideoFile() {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.parse(mMaterial.mProductVideoURL);
		intent.setDataAndType(uri, "video/*");
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.no_application), Constants.TOASTMSG_TIME)
							.show();
			e.printStackTrace();
		}
	}

	public void addToCart() {
		proposalCart = new ProposalCartModel();
		Singleton.getInstance().proposalCartList.clear();
		proposalCart.mDealerId = dealerID;
		proposalCart.mEmployeeId = employeeID;
		proposalCart.mAppointmentResultId = appointmentResultId;
		proposalCart.mMaterialId = mMaterial.mMaterialId;
		proposalCart.mMaterialName = mMaterial.mMaterialName;
		proposalCart.mMaterialThumb = mMaterial.mProductImageURL;
		proposalCart.mQuotedAmount = mMaterial.mUnitSellingPrice;

		if (mMaterial.mProductType != null) {
			Log.d("mMaterial.mProductType ", "" + mMaterial.mProductType);
		}
		proposalCart.mProductType = mMaterial.mProductType;
		proposalCart.mBoundProductId = "0";
		proposalCart.mNotes = editTxtNotes.getText().toString();
		if (!editTxtAmount.getText().toString().equals("0")
				&& !editTxtAmount.getText().toString().isEmpty()) {
			proposalCart.mQuotedAmount = editTxtAmount.getText().toString();
		}
		if (!editTxtQuantity.getText().toString().equals("0")
				&& !editTxtQuantity.getText().toString().isEmpty()) {
			proposalCart.mQuotedQuantity = editTxtQuantity.getText().toString();
			// Singleton.getInstance().proposalCartList.add(proposalCart);
			Log.d("ANONMOFS", proposalCart.mQuotedAmount);
			Singleton.getInstance().proposalCartList.add(proposalCart);
			saveTask = new SaveSummaryAsyncTask(context, saveRequest(),
					isAddnew);
			saveTask.execute();

		} else {
			Toast.makeText(context, "Quantity should not be empty.",
					Toast.LENGTH_LONG).show();
		}
	}

	public JSONObject saveRequest() {

		Log.d("size", Singleton.getInstance().proposalCartList.size() + "");
		JSONArray mRequestJson = new JSONArray();

		JSONObject ProductsData = new JSONObject();
		JSONObject product = new JSONObject();

		for (int l = 0; l < Singleton.getInstance().proposalCartList.size(); l++) {

			JSONObject reqObj_data = new JSONObject();
			try {
				reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
				reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeID);
				reqObj_data.put(Constants.KEY_APPT_RESULT_ID,
						appointmentResultId);
				reqObj_data
				.put(Constants.JSON_KEY_SALES_PROCESS_MATERIAL_ID,
						Singleton.getInstance().proposalCartList.get(l).mMaterialId);
				reqObj_data
				.put(Constants.QUOTED_QUANTITY,
						Singleton.getInstance().proposalCartList.get(l).mQuotedQuantity);
				reqObj_data
				.put(Constants.QUOTED_AMOUNT,
						Singleton.getInstance().proposalCartList.get(l).mQuotedAmount
						.replace("$", "").replace(",", ""));
				reqObj_data
				.put(Constants.BOUND_PRODUCT_ID,
						Singleton.getInstance().proposalCartList.get(l).mBoundProductId);
				reqObj_data.put(Constants.JSON_KEY_FOLLOWUP_NOTES,
						Singleton.getInstance().proposalCartList.get(l).mNotes);
				reqObj_data
				.put(Constants.JSON_KEY_SALES_PROCESS_PRODUCT_TYPE,
						Singleton.getInstance().proposalCartList.get(l).mProductType);
				if (Singleton.getInstance().proposalCartList.get(l).mProductType != null) {
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
			if(Singleton.getInstance().proposalList.size()>0){
				reqObj_data.put(Constants.DESCRIPTION, Singleton.getInstance().proposalList.get(0).getWorkOrderNotes());
			}else{
				reqObj_data.put(Constants.DESCRIPTION, "");
			}
			reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
			reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeID);
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
}
