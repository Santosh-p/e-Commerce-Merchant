package myapp.com.groceryshopmerchant.DBHandlers;

/**
 * Created by SSPL on 08-08-2017.
 */

public class UnitAndPrice {
    double _price;
    double _mrp;
    String _unit;

    public UnitAndPrice() {
    }

    public UnitAndPrice(double _price, double _mrp, String _unit) {
        this._price = _price;
        this._mrp = _mrp;
        this._unit = _unit;
    }

    public double get_mrp() {
        return _mrp;
    }

    public void set_mrp(double _mrp) {
        this._mrp = _mrp;
    }

    public double get_price() {
        return _price;
    }

    public void set_price(double _price) {
        this._price = _price;
    }

    public String get_unit() {
        return _unit;
    }

    public void set_unit(String _unit) {
        this._unit = _unit;
    }
}
