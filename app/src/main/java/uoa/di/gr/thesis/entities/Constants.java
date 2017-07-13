package uoa.di.gr.thesis.entities;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.DetectedActivity;

public class Constants {

    public static final String PACKAGE_NAME = "com.google.android.gms.location.activityrecognition";

    public static final String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public static String getActivityString(Context context, int detectedActivityType) {
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
