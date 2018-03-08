package usthb.lfbservices.com.pfe.models;

/**
 * Created by ryadh on 05/03/18.
 */

public class Product {

    private int productId;
    private String productName;
    private String productType;
    private String productCategory;
    private String productTradeMark;


    public Product() {
        this(0,"Unknown","Unknown","Unknown","Unknown");
    }


    public Product(int productId, String productName, String productType, String productCategory,
                   String productTradeMark) {
        super();
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.productCategory = productCategory;
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

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductTradeMark() {
        return productTradeMark;
    }

    public void setProductTradeMark(String productTradeMark) {
        this.productTradeMark = productTradeMark;
    }

}