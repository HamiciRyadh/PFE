package usthb.lfbservices.com.pfe.database.tables;

/**
 * Created by ryadh on 14/04/18.
 */

public class Type {

    public static String TABLE_NAME = "Type";

    public static String COLUMN_TYPE_ID = "type_id";
    public static String COLUMN_CATEGORY_ID = "category_id";
    public static String COLUMN_TYPE_NAME = "type_name";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_TYPE_ID + " INTEGER NOT NULL,"
                    + COLUMN_CATEGORY_ID + " INTEGER NOT NULL,"
                    + COLUMN_TYPE_NAME + " TEXT NOT NULL," +
                    " PRIMARY KEY ("+COLUMN_TYPE_ID+")," +
                    " FOREIGN KEY ("+COLUMN_CATEGORY_ID+") " +
                    " REFERENCES "+Category.TABLE_NAME+"("+Category.COLUMN_CATEGORY_ID+")"
                    + ");";

    public static final String INSERT_VALUES =
            "INSERT INTO "+TABLE_NAME+ " ("+COLUMN_TYPE_ID+", "+COLUMN_TYPE_NAME+", "+COLUMN_CATEGORY_ID+") VALUES " +
                    " (0,'Ordinateur Portable', 0), " +
                    " (1,'Imprimante & Scanner ', 0), " +
                    " (2,'Ordinateur de Bureau', 0), " +
                    " (3,'Écran  & Data Show', 0), " +
                    " (4,'Smartphone', 1), " +
                    " (5,'Tablette', 1), " +
                    " (6,'Téléphone fixe', 1), " +
                    " (7,'Téléphone Portable', 1); ";


    private int typeId;
    private int categoryId;
    private String typeName;

    public Type() {

    }

    public Type(int typeId, int categoryId, String typeName) {
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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
