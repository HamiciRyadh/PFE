package usthb.lfbservices.com.pfe.database.tables;

/**
 * Created by ryadh on 14/04/18.
 */

public class Product {

    private int productId;
    private int productType;
    private String productName;
    private String productTradeMark;

    public Product() {

    }

    public Product(int productId, int productType, String productName, String productTradeMark) {
        this.productId = productId;
        this.productType = productType;
        this.productName = productName;
        this.productTradeMark = productTradeMark;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductTradeMark() {
        return productTradeMark;
    }

    public void setProductTradeMark(String productTradeMark) {
        this.productTradeMark = productTradeMark;
    }
}
