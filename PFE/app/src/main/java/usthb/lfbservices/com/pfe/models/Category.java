package usthb.lfbservices.com.pfe.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "Category")
public  class Category
{
    @PrimaryKey
    private int categoryId;
    private String categoryName;
    @Ignore
    private Bitmap image;

    @Ignore
    private static final List<Category> categories = new ArrayList<Category>();

    public static List<Category> Data() {
        categories.add(new Category(0,"Informatique"));
        categories.add(new Category(1,"Téléphone"));
        categories.add(new Category(2,"Camera"));
        categories.add (new Category(3,"Automobile"));
        categories.add (new Category(4,"Matériel Professionnel"));
        categories.add(new Category(5,"Montres"));

        return categories;
    }

    public Category(){}

    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    @Ignore
    public Category(Bitmap image, String categoryName, int categoryId) {
        super();
        this.image = image;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
    }

    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}

