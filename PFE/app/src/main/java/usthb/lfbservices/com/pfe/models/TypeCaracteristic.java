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

public class TypeCaracteristic {

    @PrimaryKey
    private int typeCaracteristicId;
    private int typeId;
    @NonNull
    private String typeCaracteristicName = "";

    @Ignore
    private static final List<TypeCaracteristic> typesCaracteristics = new ArrayList<>();


    public static List<TypeCaracteristic> Data() {
        typesCaracteristics.add (new TypeCaracteristic(1, 0, "CPU"));
        typesCaracteristics.add (new TypeCaracteristic(2, 0, "Ecran"));
        typesCaracteristics.add (new TypeCaracteristic(3, 0, "Résolution"));
        typesCaracteristics.add (new TypeCaracteristic(4, 0, "GPU"));
        typesCaracteristics.add (new TypeCaracteristic(5, 0, "Batterie"));
        typesCaracteristics.add (new TypeCaracteristic(6, 0, "Batterie"));
        typesCaracteristics.add (new TypeCaracteristic(7, 0, "Autonomie"));
        typesCaracteristics.add (new TypeCaracteristic(8, 0, "Système d'exploitattion"));
        typesCaracteristics.add (new TypeCaracteristic(9, 0, "Webcam"));
        typesCaracteristics.add (new TypeCaracteristic(10, 0, "Clavier"));


        return typesCaracteristics;
    }

    public TypeCaracteristic() {
    }

    public TypeCaracteristic(int typeCaracteristicId, int typeId,@NonNull String typeCaracteristicName) {
        this.typeCaracteristicId = typeCaracteristicId;
        this.typeId = typeId;
        this.typeCaracteristicName = typeCaracteristicName;
    }

    public int getTypeCaracteristicId() {
        return typeCaracteristicId;
    }

    public void setTypeCaracteristicId(int typeCaracteristicId) {
        this.typeCaracteristicId = typeCaracteristicId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @NonNull
    public String getTypeCaracteristicName() {
        return typeCaracteristicName;
    }

    public void setTypeCaracteristicName(@NonNull String typeCaracteristicName) {
        this.typeCaracteristicName = typeCaracteristicName;
    }
}
