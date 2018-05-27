package usthb.lfbservices.com.pfe.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Category.class,
                                    parentColumns = "categoryId",
                                    childColumns = "categoryId",
                                    onDelete = CASCADE))
public class Type {

    @PrimaryKey
    private int typeId;
    private int categoryId;
    @NonNull
    private String typeName = "";



    @Ignore
    private static final List<Type> types = new ArrayList<Type>();


    public static List<Type> Data() {
        types.add(new Type(0,"Ordinateur Portable", 0));
        types.add(new Type(1,"Imprimante & Scanner ", 0));
        types.add(new Type(2,"Ordinateur de Bureau", 0));
        types.add(new Type(3,"Écran  & Data Show", 0));
        types.add(new Type(4,"Smartphone", 1));
        types.add(new Type(5,"Tablette", 1));
        types.add(new Type(6,"Téléphone fixe", 1));
        types.add(new Type( 7,"Téléphone Portable", 1));
        return types;
    }

    @Ignore
    public Type() {

    }

    public Type(int typeId,@NonNull String typeName, int categoryId ) {
        this.typeId = typeId;
        this.categoryId = categoryId;
        this.typeName = typeName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @NonNull
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(@NonNull String typeName) {
        this.typeName = typeName;
    }
}
