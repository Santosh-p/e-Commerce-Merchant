package myapp.com.groceryshopmerchant.DBHandlers;

/**
 * Created by apple on 9/7/17.
 */

public class Address {
    String city,area,building,pincode,state,landmark,name,phonenumber,alernatenumber,addressType,status;
    int addressId;

    public Address(String city, String area, String building, String pincode, String state, String landmark, String name, String phonenumber, String alernatenumber, String addressType, int addressId) {
        this.city = city;
        this.area = area;
        this.building = building;
        this.pincode = pincode;
        this.state = state;
        this.landmark = landmark;
        this.name = name;
        this.phonenumber = phonenumber;
        this.alernatenumber = alernatenumber;
        this.addressType = addressType;
        this.addressId = addressId;
    }

    public Address(String city, String area, String building, String pincode, String state, String landmark, String name, String phonenumber, String alernatenumber, String addressType) {
        this.city = city;
        this.area = area;
        this.building = building;
        this.pincode = pincode;
        this.state = state;
        this.landmark = landmark;
        this.name = name;
        this.phonenumber = phonenumber;
        this.alernatenumber = alernatenumber;
        this.addressType = addressType;
    }

    public Address() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAlernatenumber() {
        return alernatenumber;
    }

    public void setAlernatenumber(String alernatenumber) {
        this.alernatenumber = alernatenumber;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
}
