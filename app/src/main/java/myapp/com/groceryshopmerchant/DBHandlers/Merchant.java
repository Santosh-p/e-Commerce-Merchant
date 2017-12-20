package myapp.com.groceryshopmerchant.DBHandlers;

/**
 * Created by SSPL on 01-08-2017.
 */

public class Merchant {

    int _user_id;
    String _device_id;
    String _shop_name;
    String _email_id;
    String _kyc_status;
    String _setting_status;

    public Merchant() {
    }

    public Merchant(int _user_id, String _shop_name, String _email_id) {
        this._user_id = _user_id;
        this._shop_name = _shop_name;
        this._email_id = _email_id;
    }

    public Merchant(int _user_id, String _device_id, String _shop_name, String _email_id, String _kyc_status, String _setting_status) {
        this._user_id = _user_id;
        this._device_id = _device_id;
        this._shop_name = _shop_name;
        this._email_id = _email_id;
        this._kyc_status = _kyc_status;
        this._setting_status = _setting_status;
    }

    public String get_kyc_status() {
        return _kyc_status;
    }

    public void set_kyc_status(String _kyc_status) {
        this._kyc_status = _kyc_status;
    }

    public String get_setting_status() {
        return _setting_status;
    }

    public void set_setting_status(String _setting_status) {
        this._setting_status = _setting_status;
    }

    public int get_user_id() {
        return _user_id;
    }

    public void set_user_id(int _user_id) {
        this._user_id = _user_id;
    }

    public String get_shop_name() {
        return _shop_name;
    }

    public void set_shop_name(String _shop_name) {
        this._shop_name = _shop_name;
    }

    public String get_email_id() {
        return _email_id;
    }

    public void set_email_id(String _email_id) {
        this._email_id = _email_id;
    }

    public String get_device_id() {
        return _device_id;
    }

    public void set_device_id(String _device_id) {
        this._device_id = _device_id;
    }
}
