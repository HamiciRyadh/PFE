package usthb.lfbservices.com.pfe.models;


public class KeyValue {

    private int typeCaracteristicId;
    private String productCaracteristicValue;


    public KeyValue() {

    }

    public KeyValue(int typeCaracteristicId, String productCaracteristicValue) {
        this.typeCaracteristicId = typeCaracteristicId;
        this.productCaracteristicValue = productCaracteristicValue;
    }

    public int getTypeCaracteristicId() {
        return typeCaracteristicId;
    }

    public void setTypeCaracteristicId(int typeCaracteristicId) {
        this.typeCaracteristicId = typeCaracteristicId;
    }

    public String getProductCaracteristicValue() {
        return productCaracteristicValue;
    }

    public void setProductCaracteristicValue(String productCaracteristicValue) {
        this.productCaracteristicValue = productCaracteristicValue;
    }
}
