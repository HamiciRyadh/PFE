package usthb.lfbservices.com.pfe.utils;

/**
 * Created by ryadh on 21/04/18.
 */

public class Constantes {

    /**
     * Name of the file in which the history searches will be saved
     */
    public static final String HISTORY_FILE_NAME = "History.dat";
    /**
     * TAG for the fragment "SearchFragment" which is used to identify it
     */
    public static final String FRAGMENT_SEARCH = "FRAGMENT_SEARCH";
    /**
     * TAG for the fragment "ProductsFragment" which is used to identify it
     */
    public static final String FRAGMENT_PRODUCTS = "FRAGMENT_PRODUCTS";
    /**
     * Base url for retrieving photos from google.
     */
    public static final String GOOGLE_PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    /**
     * Name of the {@link android.content.SharedPreferences} responsible for storing the user's data
     */
    public static final String SHARED_PREFERENCES_USER = "user";
    public static final String SHARED_PREFERENCES_USER_EMAIL = "email";
    public static final String SHARED_PREFERENCES_USER_PASSWORD = "password";

    public static final String SHARED_PREFERENCES_POSITION = "position";
    public static final String SHARED_PREFERENCES_POSITION_LATITUDE = "latitude";
    public static final String SHARED_PREFERENCES_POSITION_LONGITUDE = "longitude";

    public static final String INTENT_SALES_POINT_ID = "salesPointID";

    public static final String ITINERAIRE_DRIVING = "driving";
    public static final String ITINERAIRE_WALKING = "walking";
}
