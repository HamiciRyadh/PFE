package usthb.lfbservices.com.pfe.models;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryadh on 04/04/18.
 */

public class Singleton {

    private GoogleMap map = null;
    private List<Product> productList = new ArrayList<Product>();
    private List<SalesPoint> salesPointList= new ArrayList<SalesPoint>();
    private List<ProductSalesPoint> productSalesPointList = new ArrayList<ProductSalesPoint>();

    private static final Singleton INSTANCE = new Singleton();

    public static Singleton getInstance() {
        return INSTANCE;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public GoogleMap getMap() {
        return this.map;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList.clear();
        this.productList.addAll(productList);
    }

    public List<SalesPoint> getSalesPointList() {
        return salesPointList;
    }

    public void setSalesPointList(List<SalesPoint> salesPointList) {
        this.salesPointList.clear();
        this.salesPointList.addAll(salesPointList);
    }

    public List<ProductSalesPoint> getProductSalesPointList() {
        return productSalesPointList;
    }

    public void setProductSalesPointList(List<ProductSalesPoint> productSalesPointList) {
        this.productSalesPointList.clear();
        this.productSalesPointList.addAll(productSalesPointList);
    }
}
