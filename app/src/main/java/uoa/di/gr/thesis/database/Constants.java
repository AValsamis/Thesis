package uoa.di.gr.thesis.database;

/**
 * Created by Sevle on 2/16/2015.
 */
public class Constants {
    public static final String URL_USERS="/test";
    public static final String URL_REGISTER="/register";
    public static final String URL_LOGIN="/login";
    public static final String URL_REGISTERZONE="/registerZone";
    public static final String URL_FIND_ZONE="/getZone";
    public static final String URL_SEND_DATA_PACKET="/sendDataPacket";
    public static final String URL_EVENTS="/around";
    public static final String POST_EVENT="/events/";
    public static final String URL_SAFE_ZONES="/safeZones/{user}";
    public static final String URL_DANG_ZONES="/dangerZones/{user}";
    public static final String URL_FALL_DETECTION="/startFallDetection/{userId}";
    public static final String URL_IS_ELDERLY="/isElderly/{username}";
    public static final String URL_SHOULD_RUN="/shouldRun/{username}";
    public static final String URL_START_SCAN="/startDataCollection";
    public static final String URL_STOP_SCAN="/stopDataCollection";
}
