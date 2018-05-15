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
        typesCaracteristics.add (new TypeCaracteristic(2, 0, "ECRAN"));
        typesCaracteristics.add (new TypeCaracteristic(3, 0, "RAM"));
        typesCaracteristics.add (new TypeCaracteristic(4, 0, "CARTE GRAPHIQUE"));

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
