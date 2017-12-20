package myapp.com.groceryshopmerchant.DBHandlers;

/**
 * Created by SSPL on 08-09-2017.
 */

public class MyOrdersDetails {


    String _orderStatus;
    String _date;
    String _shopName;
    String _time;
    String _order_id;
    String _total_price;

    public MyOrdersDetails() {
    }

    public MyOrdersDetails(String _orderStatus, String _date, String _shopName, String _time, String _order_id, String _total_price) {
        this._orderStatus = _orderStatus;
        this._date = _date;
        this._shopName = _shopName;
        this._time = _time;
        this._order_id = _order_id;
        this._total_price = _total_price;
    }

    public String get_orderStatus() {
        return _orderStatus;
    }

    public void set_orderStatus(String _orderStatus) {
        this._orderStatus = _orderStatus;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public String get_shopName() {
        return _shopName;
    }

    public void set_shopName(String _shopName) {
        this._shopName = _shopName;
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
}
