package usthb.lfbservices.com.pfe.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Wilaya {

    @PrimaryKey
    private int wilayaId;
    @NonNull
    private String wilayaName = "";
    @Ignore
    private static final List<Wilaya> wilayas = new ArrayList<>() ;

    static {
        wilayas.add(new Wilaya(1,"Adrar"));
        wilayas.add(new Wilaya(2,"Chlef"));
        wilayas.add(new Wilaya(3,"Laghouat"));
        wilayas.add(new Wilaya(4,"Oum El Bouaghi"));
        wilayas.add(new Wilaya(5,"Batna"));
        wilayas.add(new Wilaya(6,"Bejaia"));
        wilayas.add(new Wilaya(7,"Biskra"));
        wilayas.add(new Wilaya(8,"Bechar"));
        wilayas.add(new Wilaya(9,"Blida"));
        wilayas.add(new Wilaya(10,"Bouira"));
        wilayas.add(new Wilaya(11,"Tamanrasset"));
        wilayas.add(new Wilaya(12,"Tébessa"));
        wilayas.add(new Wilaya(13,"Tlemcen"));
        wilayas.add(new Wilaya(14,"Tiaret"));
        wilayas.add(new Wilaya(15,"Tizi Ouzou"));
        wilayas.add(new Wilaya(16,"Alger"));
        wilayas.add(new Wilaya(17,"Djelfa"));
        wilayas.add(new Wilaya(18,"Jijel"));
        wilayas.add(new Wilaya(19,"Sétif"));
        wilayas.add(new Wilaya(20,"Saïda"));
        wilayas.add(new Wilaya(21,"Skikda"));
        wilayas.add(new Wilaya(22,"Sidi Bel Abbès"));
        wilayas.add(new Wilaya(23,"Annaba"));
        wilayas.add(new Wilaya(24,"Guelma"));
        wilayas.add(new Wilaya(25,"Constantine"));
        wilayas.add(new Wilaya(26,"Médéa"));
        wilayas.add(new Wilaya(27,"Mostaganem"));
        wilayas.add(new Wilaya(28,"MSila"));
        wilayas.add(new Wilaya(29,"Mascara"));
        wilayas.add(new Wilaya(30,"Ouargla"));
        wilayas.add(new Wilaya(31,"Oran"));
        wilayas.add(new Wilaya(32,"El Bayadh"));
        wilayas.add(new Wilaya(33,"Illizi"));
        wilayas.add(new Wilaya(34,"Bordj Bou Arreridj"));
        wilayas.add(new Wilaya(35,"Boumerdès"));
        wilayas.add(new Wilaya(36,"El Tarf"));
        wilayas.add(new Wilaya(37,"Tindouf"));
        wilayas.add(new Wilaya(38,"Tissemsilt"));
        wilayas.add(new Wilaya(39,"El Oued"));
        wilayas.add(new Wilaya(40,"Khenchela"));
        wilayas.add(new Wilaya(41,"Souk Ahras"));
        wilayas.add(new Wilaya(42,"Tipaza"));
        wilayas.add(new Wilaya(43,"Mila"));
        wilayas.add(new Wilaya(44,"Aïn Defla"));
        wilayas.add(new Wilaya(45,"Naâma"));
        wilayas.add(new Wilaya(46,"Aïn Témouchent"));
        wilayas.add(new Wilaya(47,"Ghardaïa"));
        wilayas.add(new Wilaya(48,"Relizane"));
    }

    public static List<Wilaya> Data() {
        return  wilayas;
    }


    public Wilaya() {

    }

    public Wilaya(int wilayaId,@NonNull String wilayaName) {
        this.wilayaId = wilayaId;
        this.wilayaName = wilayaName;
    }

    public int getWilayaId() {
        return wilayaId;
    }

    public void setWilayaId(int wilayaId) {
        this.wilayaId = wilayaId;
    }

    @NonNull
    public String getWilayaName() {
        return wilayaName;
    }

    public void setWilayaName(@NonNull String wilayaName) {
        this.wilayaName = wilayaName;
    }
}
