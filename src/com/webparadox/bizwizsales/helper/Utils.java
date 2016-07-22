package com.webparadox.bizwizsales.helper;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

public class Utils {

	/**
	 * Push an "openScreen" event with the given screen name. Tags that match
	 * that event will fire.
	 */
	private static Tracker tracker;

	public static void pushOpenScreenEvent(Context context, String screenName) {
		// Instantiate the Tracker
		tracker = EasyTracker.getInstance(context);
		tracker.set("&tid", "UA-50700783-2");
		tracker.set(Fields.SCREEN_NAME, screenName);
		// Send a screenview.
		tracker.send(MapBuilder.createAppView().build());
	}
	/**
	 * Push an "Button clicked" event with the given screen name. Tags that
	 * match that event will fire.
	 */
	public static void pushbtnClickedEvent(Context context, String clickE) {
		tracker = EasyTracker.getInstance(context);
		tracker.set("&tid", "UA-50700783-2");
		// Values set directly on a tracker apply to all subsequent hits.
		tracker.set(Fields.SCREEN_NAME, "Home Screen");

		// This screenview hit will include the screen name "Home Screen".
		tracker.send(MapBuilder.createAppView().build());

		// And so will this event hit.
		tracker.send(MapBuilder.createEvent("UI", "click", "my btn clicked",
				null).build());
	}
	/**
	 * Push a "closeScreen" event with the given screen name. Tags that match
	 * that event will fire.
	 */
	public static void pushCloseScreenEvent(Context context, String screenName) {
		// Instantiate the Tracker
		tracker = EasyTracker.getInstance(context);
		tracker.set("&tid", "UA-50700783-2");
		tracker.set(Fields.SCREEN_NAME, screenName);
		// Send a screenview.
		tracker.send(MapBuilder.createAppView().build());
	}
}