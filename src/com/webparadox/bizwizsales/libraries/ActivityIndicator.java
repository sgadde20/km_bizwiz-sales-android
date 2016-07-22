package com.webparadox.bizwizsales.libraries;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActivityIndicator extends Dialog {

	TextView tv;
	LinearLayout lay;
	String loadingText = "Loading...";

	public ActivityIndicator(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ActivityIndicator(Context context, int theme) {
		// TODO Auto-generated constructor stub
		super(context, theme);
	}

	private void init(Context context) {
		// TODO Auto-generated constructor stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		this.lay = new LinearLayout(context);
		this.lay.setOrientation(LinearLayout.VERTICAL);
		this.lay.addView(new ProgressBar(context), new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.tv = new TextView(context);
		this.setLoadingText(loadingText);
		this.tv.setTextColor(Color.WHITE);
		this.lay.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		this.lay.addView(tv);
		this.addContentView(lay, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		this.setCancelable(false);
	}

	public void setLoadingText(String loadingText) {
		this.tv.setText(loadingText);
	}
}
