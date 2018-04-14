package usthb.lfbservices.com.pfe.database.tables;

/**
 * Created by ryadh on 14/04/18.
 */

public class Category {

    public static String TABLE_NAME = "Category";

    public static String COLUMN_CATEGORY_ID = "category_id";
    public static String COLUMN_CATEGORY_NAME = "category_name";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_CATEGORY_ID + " INTEGER NOT NULL,"
                    + COLUMN_CATEGORY_NAME + " TEXT NOT NULL," +
                    " PRIMARY KEY ("+COLUMN_CATEGORY_ID+")"
                    + ")";

    public static final String INSERT_VALUES =
            "INSERT INTO "+TABLE_NAME+" ("+COLUMN_CATEGORY_ID+", "+COLUMN_CATEGORY_NAME+") VALUES " +
                    " (0,'Informatique'), " +
                    " (1,'Téléphone'), " +
                    " (2,'Camera'), " +
                    " (3,'Automobile'), " +
                    " (4,'Matériel Professionnel'), " +
                    " (5,'Montres'); ";

    private int categoryId;
    private String categoryName;

    public Category() {
    }

    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
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
