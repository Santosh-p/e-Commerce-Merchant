package myapp.com.groceryshopmerchant.DBHandlers;

/**
 * Created by SSPL on 10-08-2017.
 */

public class ProductDetails {
    int _productId;
    String _productName;
    String _productImage;
    String _manufacturer;
    String _categoryName;
    String _unit;
    double _price;
    double _mrp;
    int unit_count;

    public ProductDetails() {
    }

    public ProductDetails(int _productId, String _productName, String _productImage, String _manufacturer, String _categoryName, String _unit, double _price, double _mrp, int unit_count) {
        this._productId = _productId;
        this._productName = _productName;
        this._productImage = _productImage;
        this._manufacturer = _manufacturer;
        this._categoryName = _categoryName;
        this._unit = _unit;
        this._price = _price;
        this._mrp = _mrp;
        this.unit_count = unit_count;
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

    public double get_price() {
        return _price;
    }

    public void set_price(double _price) {
        this._price = _price;
    }

    public double get_mrp() {
        return _mrp;
    }

    public void set_mrp(double _mrp) {
        this._mrp = _mrp;
    }

    public int getUnit_count() {
        return unit_count;
    }

    public void setUnit_count(int unit_count) {
        this.unit_count = unit_count;
    }
}
