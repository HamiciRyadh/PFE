package usthb.lfbservices.com.pfe.models;


public class KeyValue {

    private int typeCharacteristicId;
    private String productCharacteristicValue;


    public KeyValue() {

    }

    public KeyValue(int typeCharacteristicId, String productCharacteristicValue) {
        this.typeCharacteristicId = typeCharacteristicId;
        this.productCharacteristicValue = productCharacteristicValue;
    }

    public int getTypeCharacteristicId() {
        return typeCharacteristicId;
    }

    public void setTypeCharacteristicId(int typeCharacteristicId) {
        this.typeCharacteristicId = typeCharacteristicId;
    }

    public String getProductCharacteristicValue() {
        return productCharacteristicValue;
    }

    public void setProductCharacteristicValue(String productCharacteristicValue) {
        this.productCharacteristicValue = productCharacteristicValue;
    }
}
