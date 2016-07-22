package com.webparadox.bizwizsales;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.Constants;

public class GalleryMainActivity extends BaseActivity implements
OnClickListener {
	DisplayImageOptions options;
	ArrayList<String> allUrlPath = new ArrayList<String>();
	ArrayList<String> checkBoxArrayList = new ArrayList<String>();
	ArrayList<String> selectUrl = new ArrayList<String>();
	GridView gridview;
	ImageAdapter imageAdapder;
	ImageView capture, select, imageViewBack, logoBtn;
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	private static final String DIRECTORY = "BizWizSales";
	Uri imageUri;
	final private int CAPTURE_IMAGE = 1;
	final private int FILE_EXPLORER= 2;
	String dealerID = "", employeeID = "", CustomerId = "";
	private static GalleryMainActivity mInstance;
	SearchView gallerySearchView;
	SmartSearchAsyncTask searchAsyncTask;
	Context context;
	String from = "";
	Button pdfButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.gallerymain);
		mInstance = this;
		context = this;
		getAllGalleryImages(0);

		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageOnFail(R.drawable.ic_error)
		.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
		.cacheOnDisk(true).considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565).build();

		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		editor = userData.edit();
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");

		Bundle extras = getIntent().getExtras();
		CustomerId = extras.getString("CustomerId");

		if (getIntent().hasExtra("From")) {
			from = extras.getString("From");
		} else {
			from = "";
		}
		pdfButton=(Button) findViewById(R.id.pdf_btn);
		pdfButton.setOnClickListener(this);
		gridview = (GridView) findViewById(R.id.gridview);
		if (getScreenOrientation() == 1) {
			gridview.setNumColumns(4);
		} else {
			gridview.setNumColumns(6);
		}

		gallerySearchView = (SearchView) findViewById(R.id.gallery_searchView);

		gallerySearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub

				if (query.trim().length() != 0) {

					searchAsyncTask = new SmartSearchAsyncTask(context,
							dealerID, employeeID, query);
					searchAsyncTask.execute();
				} else {
					Toast.makeText(context, Constants.TOAST_NO_DATA,
							Toast.LENGTH_SHORT).show();
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		imageAdapder = new ImageAdapter();
		gridview.setAdapter(imageAdapder);

		capture = (ImageView) findViewById(R.id.capture);
		capture.setOnClickListener(this);

		select = (ImageView) findViewById(R.id.select);
		select.setOnClickListener(this);

		imageViewBack = (ImageView) findViewById(R.id.back_icon);
		imageViewBack.setOnClickListener(this);

		logoBtn = (ImageView) findViewById(R.id.image_back_icon);
		logoBtn.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Gallery Main Activity");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method

	}
	static class ViewHolder {
		ImageView imageView;
		CheckBox checkBox;
	}

	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return allUrlPath.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater;
			ViewHolder holder;
			if (convertView == null) {
				inflater = (LayoutInflater) getApplicationContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.galleryitem, null);
				holder = new ViewHolder();

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView);
			holder.imageView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (checkBoxArrayList.get(position) != "") {
						checkBoxArrayList.set(position, "");
					} else if (checkBoxArrayList.get(position) == "") {
						checkBoxArrayList.set(position,
								allUrlPath.get(position));
					}
					int checkCount = 0;
					for (int i = 0; i < checkBoxArrayList.size(); i++) {
						if (checkBoxArrayList.get(i) != "") {
							checkCount = checkCount + 1;
						}
					}
					if (checkCount < 11) {
						imageAdapder.notifyDataSetChanged();
					} else {
						checkBoxArrayList.set(position, "");
						imageAdapder.notifyDataSetChanged();
						showToast("Upload ten Images only");
					}
				}
			});
			holder.checkBox = (CheckBox) convertView
					.findViewById(R.id.checkBox);
			holder.checkBox.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox);
					if (cb.isChecked()) {
						checkBoxArrayList.set(position,
								allUrlPath.get(position));
					} else if (!cb.isChecked()) {
						checkBoxArrayList.set(position, "");
					}
					int checkCount = 0;
					for (int i = 0; i < checkBoxArrayList.size(); i++) {
						if (checkBoxArrayList.get(i) != "") {
							checkCount = checkCount + 1;
						}
					}
					if (checkCount < 11) {
					} else {
						checkBoxArrayList.set(position, "");
						imageAdapder.notifyDataSetChanged();
						showToast("Upload ten Images only");
					}
				}
			});
			if (checkBoxArrayList.size() > position) {
				if (checkBoxArrayList.get(position) != "") {
					holder.checkBox.setChecked(true);
				} else {
					holder.checkBox.setChecked(false);
				}
				imageLoader.displayImage("file://" + allUrlPath.get(position),
						holder.imageView, options);
			}

			return convertView;
		}
	}

	private void showToast(String toastmsg) {
		Toast.makeText(getApplicationContext(), toastmsg, Toast.LENGTH_LONG)
		.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.capture:
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String fileName = "IMG_" + sdf.format(new Date()) + ".jpg";
			File myDirectory = new File(
					Environment.getExternalStorageDirectory() + "/" + DIRECTORY
					+ "/");
			if (!myDirectory.exists())
				myDirectory.mkdirs();
			File file = new File(myDirectory, fileName);
			imageUri = Uri.fromFile(file);
			Intent intent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, CAPTURE_IMAGE);
			break;
		case R.id.select:
			ArrayList<String> str = new ArrayList<String>();
			if (checkBoxArrayList.size() > 0) {
				for (int j = 0; j < checkBoxArrayList.size(); j++) {
					if (!checkBoxArrayList.get(j).equals("")) {
						str.add(checkBoxArrayList.get(j));
					}
				}
			}
			if (str.size() > 0) {
				if (from == "") {
					Intent uploadIntent = new Intent(GalleryMainActivity.this,
							GalleryUploadActivity.class);
					uploadIntent.putStringArrayListExtra("UPLOADPHOTOURL", str);
					uploadIntent.putExtra("CustomerId", CustomerId);
					uploadIntent.putExtra("From", from);
					startActivity(uploadIntent);
				} else {
					Intent returnIntent = new Intent();
					returnIntent.putStringArrayListExtra("UPLOADPHOTOURL", str);
					setResult(RESULT_OK, returnIntent);
					finish();
				}
			} else {
				showToast("Please select at least one image");
			}
			break;

		case R.id.back_icon:
			finish();
			break;

		case R.id.image_back_icon:
			Intent backIntent = new Intent(GalleryMainActivity.this,
					MainActivity.class);
			backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(backIntent);
			break;
		case R.id.pdf_btn:
			try{
				Intent intentfileExplorer = new Intent(Intent.ACTION_GET_CONTENT);
				intentfileExplorer.setType("file/*");
				if(intentfileExplorer.resolveActivity(getPackageManager()) != null)
					startActivityForResult(intentfileExplorer, FILE_EXPLORER);
			}catch (Exception e) {
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE) {
			if (resultCode == Activity.RESULT_OK) {
				MediaScannerConnection.scanFile(getApplicationContext(),
						new String[] { imageUri.getPath() }, null,
						new MediaScannerConnection.OnScanCompletedListener() {
					@Override
					public void onScanCompleted(String path, Uri uri) {
						Log.i("Scanned = ", "Scanned " + path);
						runOnUiThread(new Runnable() {
							public void run() {
								getAllGalleryImages(1);
								imageAdapder.notifyDataSetChanged();
							}
						});
					}
				});
			}
			else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(),
						"Picture could not be taken.", Toast.LENGTH_SHORT)
						.show();
			}

		} else if (requestCode ==FILE_EXPLORER  && data!=null) {
			String strpdf=data.getData().getLastPathSegment().replace(" ", "").toString();
			if (strpdf.substring(strpdf.length()-3).toLowerCase().equals("pdf")) {
				ArrayList<String> str = new ArrayList<String>();
				str.add(data.getData().getPath());
				Intent uploadIntent = new Intent(GalleryMainActivity.this,
						GalleryUploadActivity.class);
				uploadIntent.putStringArrayListExtra("UPLOADPHOTOURL", str);
				uploadIntent.putExtra("CustomerId", CustomerId);
				uploadIntent.putExtra("From", from);
				startActivity(uploadIntent);
			} else {
				Toast.makeText(getApplicationContext(), "Invalid File", Toast.LENGTH_SHORT).show();
			}



		}else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void getAllGalleryImages(int value) {
		ArrayList<String> str = new ArrayList<String>();
		if (checkBoxArrayList.size() > 0) { 

			for (int j = 0; j < checkBoxArrayList.size(); j++) {
				if (!checkBoxArrayList.get(j).equals("")) {
					str.add(checkBoxArrayList.get(j));
				}
			}
		}
		allUrlPath.clear();
		checkBoxArrayList.clear();
		final String[] columns = { MediaStore.Images.Media.DATA,
				MediaStore.Images.Media._ID };
		final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
		@SuppressWarnings("deprecation")
		Cursor imagecursor1 = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy + " DESC");
		if (imagecursor1 != null) {
			for (int i = 0; i < imagecursor1.getCount(); i++) {
				imagecursor1.moveToPosition(i);
				int dataColumnIndex = imagecursor1
						.getColumnIndex(MediaStore.Images.Media.DATA);
				if (new File(imagecursor1.getString(dataColumnIndex)).length() > 0) {
					allUrlPath.add(imagecursor1.getString(dataColumnIndex));
				}
			}
		}

		if (value == 0) {
			str.add("");
		} else {
			if (str.size() < 10) {
				str.add(allUrlPath.get(0));
			}
		}

		for (int i = 0; i < allUrlPath.size(); i++) {
			if (str.size() > 0) {
				if (str.contains(allUrlPath.get(i))) {
					checkBoxArrayList.add(allUrlPath.get(i));
				} else {
					checkBoxArrayList.add("");
				}
			} else {
				checkBoxArrayList.add("");
			}
		}
	}

	public int getScreenOrientation() {
		int i = 0;
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			i = 1; // Portrait Mode

		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			i = 2; // Landscape mode
		}
		return i;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("imageUri", imageUri);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		imageUri = savedInstanceState.getParcelable("imageUri");
	}

	public static boolean closeActivity() {
		if (mInstance != null) {
			mInstance.finish();
			return true;
		}
		return false;
	}

	public void onDestroy() {
		super.onDestroy();
		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.gc();
	}
}