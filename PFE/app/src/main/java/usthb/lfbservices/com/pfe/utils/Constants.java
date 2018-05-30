package usthb.lfbservices.com.pfe.utils;

/**
 * Contains the constants used by the application.
 */

public class Constants {

    /**
     * Name of the file in which the history searches will be saved.
     */
    public static final String HISTORY_FILE_NAME = "History.dat";

    /**
     * The TAGs associated with the used fragments.
     */
    public static final String FRAGMENT_SEARCH = "FRAGMENT_SEARCH";
    public static final String FRAGMENT_PRODUCTS = "FRAGMENT_PRODUCTS";
    public static final String FRAGMENT_MAP = "FRAGMENT_MAP";
    public static final String FRAGMENT_FAVORITE = "FRAGMENT_FAVORITE";
    public static final String FRAGMENT_NOTIFICATIONS = "FRAGMENT_NOTIFICATIONS";
    public static final String FRAGMENT_PARAMETERS = "FRAGMENTS_PARAMETERS";
    public static final String FRAGMENT_BARCODE_SCANNER = "FRAGMENT_BARCODE_SCANNER";
    public static final String FRAGMENT_PRODUCT_DESCRIPTION = "FRAGMENT_PRODUCT_DESCRIPTION";

    /**
     * Base url for retrieving photos from google.
     */
    public static final String GOOGLE_PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?";

    /**
     * Name of the {@link android.content.SharedPreferences} responsible for storing the user's data.
     */
    public static final String SHARED_PREFERENCES_USER = "user";
    public static final String SHARED_PREFERENCES_USER_EMAIL = "email";
    public static final String SHARED_PREFERENCES_USER_PASSWORD = "password";
    /**
     * Name of the field where is stored the device's Firebase Token Id.
     */
    public static final String SHARED_PREFERENCES_FIREBASE_TOKEN_ID = "firebaseTokenId";

    /**
     * Name of the {@link android.content.SharedPreferences} responsible for storing the user's position.
     */
    public static final String SHARED_PREFERENCES_POSITION = "position";
    public static final String SHARED_PREFERENCES_POSITION_LATITUDE = "latitude";
    public static final String SHARED_PREFERENCES_POSITION_LONGITUDE = "longitude";

    /**
     * Name of the extra used in an {@link android.content.Intent} to store a {@link usthb.lfbservices.com.pfe.models.SalesPoint#salesPointId}.
     */
    public static final String INTENT_SALES_POINT_ID = "salesPointID";

    /**
     * The available options for the itinerary.
     */
    public static final String ITINERAIRE_DRIVING = "driving";
    public static final String ITINERAIRE_WALKING = "walking";

    /**
     * The {@link android.content.SharedPreferences} used for user parameters.
     */
    public static final String SHARED_PREFERENCES_USER_MAP_STYLE = "mapStyle";
    public static final String SATELLITE = "satellit";
    public static final String STANDARD = "standard";

    /**
     * Request codes used when requesting a permission.
     */
    public static final int REQUEST_CAMERA_PERMISSION = 201;
    public static final int REQUEST_GPS_PERMISSION = 123;

}
