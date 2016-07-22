package com.webparadox.bizwizsales;

import android.app.Activity;

import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class BaseActivity extends Activity {
	protected ImageLoader imageLoader = ImageLoader.getInstance();
}