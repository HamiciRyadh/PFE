package usthb.lfbservices.com.pfe.database.tables;

/**
 * Created by ryadh on 14/04/18.
 */

public class Wilaya {

    public static String TABLE_NAME = "Wilaya";

    public static String COLUMN_WILAYA_ID = "wilaya_id";
    public static String COLUMN_WILAYA_NAME = "wilaya_name";



    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_WILAYA_ID + " INTEGER NOT NULL,"
                    + COLUMN_WILAYA_NAME + " TEXT NOT NULL," +
                    " PRIMARY KEY ("+COLUMN_WILAYA_ID+")"
                    + ")";

    public static final String INSERT_VALUES =
            "INSERT INTO "+TABLE_NAME+" ("+COLUMN_WILAYA_ID+", "+COLUMN_WILAYA_NAME+") VALUES " +
                    " (1,'Adrar'), " +
                    " (2,'Chlef'), " +
                    " (3,'Laghouat'), " +
                    " (4,'Oum El Bouaghi'), " +
                    " (5,'Batna'), " +
                    " (6,'Bejaia'), " +
                    " (7,'Biskra'), " +
                    " (8,'Bechar'), " +
                    " (9,'Blida'), " +
                    " (10,'Bouira'), " +
                    " (11,'Tamanrasset'), " +
                    " (12,'Tébessa'), " +
                    " (13,'Tlemcen'), " +
                    " (14,'Tiaret'), " +
                    " (15,'Tizi Ouzou'), " +
                    " (16,'Alger'), " +
                    " (17,'Djelfa'), " +
                    " (18,'Jijel'), " +
                    " (19,'Sétif'), " +
                    " (20,'Saïda'), " +
                    " (21,'Skikda'), " +
                    " (22,'Sidi Bel Abbès'), " +
                    " (23,'Annaba'), " +
                    " (24,'Guelma'), " +
                    " (25,'Constantine'), " +
                    " (26,'Médéa'), " +
                    " (27,'Mostaganem'), " +
                    " (28,'M''Sila'), " +
                    " (29,'Mascara'), " +
                    " (30,'Ouargla'), " +
                    " (31,'Oran'), " +
                    " (32,'El Bayadh'), " +
                    " (33,'Illizi'), " +
                    " (34,'Bordj Bou Arreridj'), " +
                    " (35,'Boumerdès'), " +
                    " (36,'El Tarf'), " +
                    " (37,'Tindouf'), " +
                    " (38,'Tissemsilt'), " +
                    " (39,'El Oued'), " +
                    " (40,'Khenchela'), " +
                    " (41,'Souk Ahras'), " +
                    " (42,'Tipaza'), " +
                    " (43,'Mila'), " +
                    " (44,'Aïn Defla'), " +
                    " (45,'Naâma'), " +
                    " (46,'Aïn Témouchent'), " +
                    " (47,'Ghardaïa'), " +
                    " (48,'Relizane'); ";


    private int wilayaId;
    private String wilayaName;

    public Wilaya() {

    }

    public Wilaya(int wilayaId, String wilayaName) {
        this.wilayaId = wilayaId;
        this.wilayaName = wilayaName;
    }

    public int getWilayaId() {
        return wilayaId;
    }

    public void setWilayaId(int wilayaId) {
        this.wilayaId = wilayaId;
    }

    public String getWilayaName() {
        return wilayaName;
    }

    public void setWilayaName(String wilayaName) {
        this.wilayaName = wilayaName;
    }
}
