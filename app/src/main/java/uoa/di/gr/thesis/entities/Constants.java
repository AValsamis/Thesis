package uoa.di.gr.thesis.entities;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.DetectedActivity;

/**
 * Created by koemdzhiev on 05/06/16.
 */
public class Constants {
    public static final String MESSAGE_KEY = "message";

    public static final String CONFIDENCE_KEY = "confidence";

    public static final String INTENT_FILTER = "my-intent-filter";

    public static final String LIST_KEY = "list_key";
    public static final String LIST_ITEM_KEY = "list_item_key";

    public static final String PACKAGE_NAME = "com.google.android.gms.location.activityrecognition";

    public static final String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";

    public static final String ACTIVITY_EXTRA = PACKAGE_NAME + ".ACTIVITY_EXTRA";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES";

    public static final String ACTIVITY_UPDATES_REQUESTED_KEY = PACKAGE_NAME +
            ".ACTIVITY_UPDATES_REQUESTED";

    public static final String DETECTED_ACTIVITIES = PACKAGE_NAME + ".DETECTED_ACTIVITIES";

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return "vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "bicycle";
            case DetectedActivity.ON_FOOT:
                return "foot";
            case DetectedActivity.RUNNING:
                return "running";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.TILTING:
                return "tilting";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.WALKING:
                return "walking";
            default:
                return "unknown";
        }
    }
}
