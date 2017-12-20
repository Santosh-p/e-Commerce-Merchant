package myapp.com.groceryshopmerchant.DBHandlers;

/**
 * Created by SSPL on 29-08-2017.
 */

public class CustomerOrderDetails {

    int _productId;
    String _productName;
    String _customerName;
    String _productImage;
    String _manufacturer;
    String _categoryName;
    String _unit;
    float _price;
    float _mrp;
    int _quantity;
    String _orderStatus;
    String _totalRatings;
    String _avgRatings;
    String _date;
    String _time;
    String _order_id;
    String _total_price;
    String packed_flag;
    int _address_id;
    String _order_type;

    public CustomerOrderDetails() {
    }

    public CustomerOrderDetails(int _productId, String _productName, String _customerName, String _productImage, String _manufacturer, String _categoryName, String _unit, float _price, String _orderStatus, String _totalRatings, String _avgRatings, String _date, String _time, String _order_id, String _total_price) {
        this._productId = _productId;
        this._productName = _productName;
        this._customerName = _customerName;
        this._productImage = _productImage;
        this._manufacturer = _manufacturer;
        this._categoryName = _categoryName;
        this._unit = _unit;
        this._price = _price;
        this._orderStatus = _orderStatus;
        this._totalRatings = _totalRatings;
        this._avgRatings = _avgRatings;
        this._date = _date;
        this._time = _time;
        this._order_id = _order_id;
        this._total_price = _total_price;
    }

    public int get_productId() {
        return _productId;
    }

    public void set_productId(int _productId) {
        this._productId = _productId;
    }

    public String get_productName() {
        return _productName;
    }

    public void set_productName(String _productName) {
        this._productName = _productName;
    }

    public String get_customerName() {
        return _customerName;
    }

    public void set_customerName(String _customerName) {
        this._customerName = _customerName;
    }

    public String get_productImage() {
        return _productImage;
    }

    public void set_productImage(String _productImage) {
        this._productImage = _productImage;
    }

    public String get_manufacturer() {
        return _manufacturer;
    }

    public void set_manufacturer(String _manufacturer) {
        this._manufacturer = _manufacturer;
    }

    public String get_categoryName() {
        return _categoryName;
    }

    public void set_categoryName(String _categoryName) {
        this._categoryName = _categoryName;
    }

    public String get_unit() {
        return _unit;
    }

    public void set_unit(String _unit) {
        this._unit = _unit;
    }

    public float get_price() {
        return _price;
    }

    public void set_price(float _price) {
        this._price = _price;
    }

    public String get_orderStatus() {
        return _orderStatus;
    }

    public void set_orderStatus(String _orderStatus) {
        this._orderStatus = _orderStatus;
    }

    public String get_totalRatings() {
        return _totalRatings;
    }

    public void set_totalRatings(String _totalRatings) {
        this._totalRatings = _totalRatings;
    }

    public String get_avgRatings() {
        return _avgRatings;
    }

    public void set_avgRatings(String _avgRatings) {
        this._avgRatings = _avgRatings;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public String get_time() {
        return _time;
    }

    public void set_time(String _time) {
        this._time = _time;
    }

    public String get_order_id() {
        return _order_id;
    }

    public void set_order_id(String _order_id) {
        this._order_id = _order_id;
    }

    public String get_total_price() {
        return _total_price;
    }

    public void set_total_price(String _total_price) {
        this._total_price = _total_price;
    }

    public String getPacked_flag() {
        return packed_flag;
    }

    public void setPacked_flag(String packed_flag) {
        this.packed_flag = packed_flag;
    }

    public int get_quantity() {
        return _quantity;
    }

    public void set_quantity(int _quantity) {
        this._quantity = _quantity;
    }

    public int get_address_id() {
        return _address_id;
    }

    public void set_address_id(int _address_id) {
        this._address_id = _address_id;
    }

    public String get_order_type() {
        return _order_type;
    }

    public void set_order_type(String _order_type) {
        this._order_type = _order_type;
    }

    public float get_mrp() {
        return _mrp;
    }

    public void set_mrp(float _mrp) {
        this._mrp = _mrp;
    }
}
