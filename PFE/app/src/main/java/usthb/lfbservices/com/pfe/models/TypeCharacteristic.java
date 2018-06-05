package usthb.lfbservices.com.pfe.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Type.class,
                        parentColumns = "typeId",
                        childColumns = "typeId",
                        onDelete = CASCADE))

public class TypeCharacteristic {

    @PrimaryKey
    private int typeCharacteristicId;
    private int typeId;
    @NonNull
    private String typeCharacteristicName = "";

    @Ignore
    private static final List<TypeCharacteristic> typesCharacteristics = new ArrayList<>();

    static {
        typesCharacteristics.add (new TypeCharacteristic(1, 0, "CPU"));
        typesCharacteristics.add (new TypeCharacteristic(2, 0, "Mémoire"));
        typesCharacteristics.add (new TypeCharacteristic(3, 0, "RAM"));
        typesCharacteristics.add (new TypeCharacteristic(4, 0, "GPU"));
        typesCharacteristics.add (new TypeCharacteristic(5, 0, "Résolution"));
        typesCharacteristics.add (new TypeCharacteristic(6, 0, "Ecran"));
        typesCharacteristics.add (new TypeCharacteristic(7, 0, "Batterie"));
        typesCharacteristics.add (new TypeCharacteristic(8, 0, "Autonomie"));
        typesCharacteristics.add (new TypeCharacteristic(9, 0, "Système d'exploitattion"));
        typesCharacteristics.add (new TypeCharacteristic(10, 0, "Webcam"));
        typesCharacteristics.add (new TypeCharacteristic(11, 0, "Clavier"));
        typesCharacteristics.add (new TypeCharacteristic(12, 4, "Mémoire"));
        typesCharacteristics.add (new TypeCharacteristic(13, 4, "RAM"));
        typesCharacteristics.add (new TypeCharacteristic(14, 4, "Résolution"));
        typesCharacteristics.add (new TypeCharacteristic(15, 4, "Ecran"));
        typesCharacteristics.add (new TypeCharacteristic(16, 4, "Batterie"));
        typesCharacteristics.add (new TypeCharacteristic(17, 4, "Autonomie"));
        typesCharacteristics.add (new TypeCharacteristic(18, 4, "Système d\'exploitation"));
        typesCharacteristics.add (new TypeCharacteristic(19, 4, "Caméra principale"));
        typesCharacteristics.add (new TypeCharacteristic(20, 4, "Caméra secondaire"));
    }

    public static List<TypeCharacteristic> Data() {
        return typesCharacteristics;
    }

    public TypeCharacteristic() {
    }

    public TypeCharacteristic(int typeCharacteristicId, int typeId, @NonNull String typeCharacteristicName) {
        this.typeCharacteristicId = typeCharacteristicId;
        this.typeId = typeId;
        this.typeCharacteristicName = typeCharacteristicName;
    }

    public int getTypeCharacteristicId() {
        return typeCharacteristicId;
    }

    public void setTypeCharacteristicId(int typeCharacteristicId) {
        this.typeCharacteristicId = typeCharacteristicId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @NonNull
    public String getTypeCharacteristicName() {
        return typeCharacteristicName;
    }

    public void setTypeCharacteristicName(@NonNull String typeCharacteristicName) {
        this.typeCharacteristicName = typeCharacteristicName;
    }
}
