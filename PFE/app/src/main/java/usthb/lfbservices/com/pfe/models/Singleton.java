package usthb.lfbservices.com.pfe.models;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by ryadh on 04/04/18.
 */

public class Singleton {

    private GoogleMap map = null;

    private static final Singleton INSTANCE = new Singleton();

    public static Singleton getInstance() {
        return INSTANCE;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public GoogleMap getMap() {
        return this.map;
    }
}
