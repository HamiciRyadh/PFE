package usthb.lfbservices.com.pfe.models;

/**
 * Created by ryadh on 05/03/18.
 */

public class Product {

    private int productId;
    private String productName;
    private int productType;
    private String productTradeMark;


    public Product() {
        this(0,"Unknown",0,"Unknown");
    }


    public Product(int productId, String productName, int productType, String productTradeMark) {
        super();
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.productTradeMark = productTradeMark;
    }



    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }


    public String getProductTradeMark() {
        return productTradeMark;
    }

    public void setProductTradeMark(String productTradeMark) {
        this.productTradeMark = productTradeMark;
    }



}