package myapp.com.groceryshopmerchant.DBHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SSPL on 01-08-2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GroceryMerchant.db";

    // Merchant Table
    private static final String TABLE_MERCHANT = "registration";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_MERCHANT_ID = "user_id";
    private static final String KEY_SHOP_NAME = "shop_name";
    private static final String KEY_EMAIL_ID = "email_id";

    //  Registration Status Table
    private static final String TABLE_REGISTRATION_STATUS = "registration_status";
    private static final String KEY_KYC_STATUS = "kyc_status";
    private static final String KEY_SETTING_STATUS = "setting_status";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_MERCHANT + "("
                + KEY_MERCHANT_ID + " INTEGER,"
                + KEY_DEVICE_ID + " TEXT,"
                + KEY_SHOP_NAME + " TEXT,"
                + KEY_EMAIL_ID + " TEXT,"
                + KEY_KYC_STATUS + " TEXT,"
                + KEY_SETTING_STATUS + " TEXT"+ ")";

   db.execSQL(CREATE_USERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MERCHANT);
        onCreate(db);
    }

    // ############################# User table ###########################
    // code to add User
    public void addMerchant(Merchant merchant) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MERCHANT_ID, merchant.get_user_id());
        values.put(KEY_DEVICE_ID, merchant.get_device_id());
        values.put(KEY_SHOP_NAME, merchant.get_shop_name());
        values.put(KEY_EMAIL_ID, merchant.get_email_id());
        values.put(KEY_KYC_STATUS, merchant.get_kyc_status());
        values.put(KEY_SETTING_STATUS, merchant.get_setting_status());

        db.insert(TABLE_MERCHANT, null, values);

        db.close(); // Closing database connection
    }

    public Merchant getMerchantDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_MERCHANT;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

        }
        Merchant merchant = new Merchant(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5));
        return merchant;
    }

    // code to get all Users in a list view
    public List<Merchant> getAllMerchants() {
        List<Merchant> merchantList = new ArrayList<Merchant>();
        String selectQuery = "SELECT  * FROM " + TABLE_MERCHANT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Merchant merchant = new Merchant();
                merchant.set_user_id(cursor.getInt(0));
                merchant.set_shop_name(cursor.getString(2));
                merchant.set_email_id(cursor.getString(3));
                merchantList.add(merchant);
            } while (cursor.moveToNext());
        }
        return merchantList;
    }

    //// code to update registration status
    public int UpdateRegistrationStatus(int merchantId,String kycStatus , String settingStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_KYC_STATUS,kycStatus);
        values.put(KEY_SETTING_STATUS, settingStatus);

        // updating row
        return db.update(TABLE_MERCHANT, values, KEY_MERCHANT_ID + " = ?",
                new String[]{String.valueOf(merchantId)});
    }
}
