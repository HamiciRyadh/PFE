package usthb.lfbservices.com.pfe.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Wilaya.class,
                        parentColumns = "wilayaId",
                        childColumns = "wilayaId",
                        onDelete = CASCADE),
        primaryKeys = { "wilayaId", "cityId" })

public class City {

    private int cityId;
    private int wilayaId;
    private String cityName;

    @Ignore
    private static final List<City> cities = new ArrayList<>();

    public static List<City> Data() {
        cities.add( new City(9,"Ain romana", 1));
        cities.add( new City(9,"Ben khellil",2 ));
        cities.add( new City(9,"Beni mered",3 ));
        cities.add( new City(9,"Beni tamou",4 ));
        cities.add( new City(9,"Blida",5 ));
        cities.add( new City(9,"Bouarfa",6 ));
        cities.add( new City(9,"Boufarik", 7));
        cities.add( new City(9,"Bougara", 8));
        cities.add( new City(9,"Bouinan", 9));
        cities.add( new City(9,"Chebli",10 ));
        cities.add( new City(9,"Chiffa", 11));
        cities.add( new City(9,"Chrea", 12));
        cities.add( new City(9,"Djebara", 13));
        cities.add( new City(9,"El Affroun", 14));
        cities.add( new City(9,"Guerrouaou",15 ));
        cities.add( new City(9,"Hamam melouane", 16));
        cities.add( new City(9,"Larabaa",17 ));
        cities.add( new City(9,"Meftah",18 ));
        cities.add( new City(9,"Mouzaia",19 ));
        cities.add( new City(9,"Ouled djer",20 ));
        cities.add( new City(9,"Ouled el alleug",21 ));
        cities.add( new City(9,"Ouled selama",22 ));
        cities.add( new City(9,"Ouled yaich", 23));
        cities.add( new City(9,"Sidi moussa", 24));
        cities.add( new City(9,"Souhane",25 ));
        cities.add( new City(9,"Soumaa", 26));
        cities.add( new City(16,"Ouled Fayet",1 ));
        cities.add( new City(16,"Ain Banian", 2));
        cities.add( new City(16,"Ain Naadja",3 ));
        cities.add( new City(16,"Ain Taya",4 ));
        cities.add( new City(16,"Alger Centre", 5));
        cities.add( new City(16,"Bab el Oued", 6));
        cities.add( new City(16,"Bab ezzouar",7 ));
        cities.add( new City(16,"Bab hassen",8 ));
        cities.add( new City(16,"Bachdjerrah",9 ));
        cities.add( new City(16,"Baraki",10 ));
        cities.add( new City(16,"Belouizdad", 11));
        cities.add( new City(16,"Ben aknoun",12 ));
        cities.add( new City(16,"Beni messous",13 ));
        cities.add( new City(16,"Bir mourad rais", 14));
        cities.add( new City(16,"Birkhadem", 15));
        cities.add( new City(16,"Birtouta", 16));
        cities.add( new City(16,"Ouled Fayet",17 ));
        cities.add( new City(16,"Kouba",18 ));
        cities.add( new City(16,"Hammamet",19 ));
        cities.add( new City(16,"Hydra",20 ));
        cities.add( new City(16,"Birkhadem",21 ));

        return cities;
    }

    public City() {

    }

    public City(int wilayaId, String cityName, int cityId) {
        this.wilayaId = wilayaId;
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getWilayaId() {
        return wilayaId;
    }

    public void setWilayaId(@NonNull int wilayaId) {
        this.wilayaId = wilayaId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}

