package usthb.lfbservices.com.pfe.database.tables;

/**
 * Created by ryadh on 14/04/18.
 */

public class City {

    public static String TABLE_NAME = "City";

    public static String COLUMN_CITY_ID = "city_id";
    public static String COLUMN_CITY_WILAYA_ID = "city_wilaya_id";
    public static String COLUMN_CITY_NAME = "city_name";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_CITY_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_CITY_WILAYA_ID + " INTEGER NOT NULL,"
                    + COLUMN_CITY_NAME + " TEXT"
                    + ")";

    private int cityId;
    private int cityWilayaId;
    private String cityName;

    public City() {

    }

    public City(int cityWilayaId, int cityId, String cityName) {
        this.cityWilayaId = cityWilayaId;
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public int getCityWilayaId() {
        return cityWilayaId;
    }

    public void setCityWilayaId(int cityWilayaId) {
        this.cityWilayaId = cityWilayaId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
