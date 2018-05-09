package usthb.lfbservices.com.pfe.models;

/**
 * Created by ryadh on 05/03/18.
 */

public class Product {

    private String productBarcode;
    private String productName;
    private int productType;
    private String productTradeMark;


    public Product() {
        this("","Unknown",0,"Unknown");
    }


    public Product(String productBarcode, String productName, int productType, String productTradeMark) {
        super();
        this.productBarcode = productBarcode;
        this.productName = productName;
        this.productType = productType;
        this.productTradeMark = productTradeMark;
    }



    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
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