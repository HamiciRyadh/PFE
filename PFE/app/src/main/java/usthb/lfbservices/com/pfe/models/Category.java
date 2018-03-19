package usthb.lfbservices.com.pfe.models;

import android.graphics.Bitmap;

/**
 * Created by root on 08/03/18.
 */

public class Category
{
    Bitmap image;
    String title;
    int id;

    public Category(Bitmap image, String title, int id) {
        super();
        this.image = image;
        this.title = title;
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
