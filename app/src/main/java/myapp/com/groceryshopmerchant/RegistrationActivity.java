package myapp.com.groceryshopmerchant;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import myapp.com.groceryshopmerchant.DBHandlers.DatabaseHandler;
import myapp.com.groceryshopmerchant.DBHandlers.Merchant;

import static myapp.com.groceryshopmerchant.Constants.Constants.appendLog;
import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;
import static myapp.com.groceryshopmerchant.Constants.Constants.isNetworkAvailable;
import static myapp.com.groceryshopmerchant.Constants.Constants.mid;
import static myapp.com.groceryshopmerchant.Constants.Constants.progressDialog;

public class RegistrationActivity extends AppCompatActivity {

    Button BtnAddShopImage, BtnRegister;
    //    TextView ImgPickLocation;
    EditText EdtShopName, EdtShopCity, EdtShopArea, EdtShopAddress, EdtMobileNumber, EdtEmailId, EdtPassword, EdtConfirmPassword;
    String ShopName, ShopCity, ShopArea, ShopAddress, MobNumber, EmailId, ConfirmPassword, Password;
    InputStream inputStream = null;
    String result = null;
    String responceStatus, responseUserId;
    int tempstatus;
    ProgressDialog progress;
    String FCMtoken,regId;
    DatabaseHandler db = new DatabaseHandler(this);

    double m_lat, m_long;
    String KYCStatus = "fail", SettingStatus = "fail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        progress = new ProgressDialog(RegistrationActivity.this);
        EdtShopName = (EditText) findViewById(R.id.shopname);
        EdtShopCity = (EditText) findViewById(R.id.shop_city);
        EdtShopArea = (EditText) findViewById(R.id.shop_area);
        EdtShopAddress = (EditText) findViewById(R.id.shop_address);
        EdtMobileNumber = (EditText) findViewById(R.id.mob_no);
        EdtEmailId = (EditText) findViewById(R.id.email_id);
        EdtPassword = (EditText) findViewById(R.id.password);
        EdtConfirmPassword = (EditText) findViewById(R.id.confirm_password);
        // BtnAddShopImage = (Button) findViewById(R.id.add_shop_image);
        BtnRegister = (Button) findViewById(R.id.register_button);
//        ImgPickLocation = (TextView) findViewById(R.id.img_picLocation);
//        ImgPickLocation.setTypeface(fontAwesomeFont);
//        ImgPickLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                Intent i = new Intent(getApplicationContext(), UploadImageActivity.class);
////                startActivity(i);
//            }
//        });
//        BtnAddShopImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), UploadImageActivity.class);
//                startActivity(i);
//            }
//        });
        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionRegister();
            }
        });

//        EdtShopName.setText("Ganesh Grocery Shop");
//        EdtShopCity.setText("Pune");
//        EdtShopArea.setText("Kothrud");
//        EdtShopAddress.setText("Right Bhusari Colony");
//        EdtMobileNumber.setText("9096326024");
//        EdtEmailId.setText("harishh@sveltoz.com");
//        EdtPassword.setText("1111");
//        EdtConfirmPassword.setText("1111");

    }

//    //code to check if this checkbox is checked!
//    public void itemClicked(View v) {
//        CheckBox CBDeliveryOption = (CheckBox) v;
//        if (CBDeliveryOption.isChecked()) {
//            DeliveryOption = "yes";
//        } else {
//            DeliveryOption = "no";
//        }
//    }

    public void ActionRegister() {
        SharedPreferences prefs = getSharedPreferences("token", MODE_PRIVATE);
        FCMtoken = prefs.getString("regId", "not define");
//cnwN1BM-CMw:APA91bGozxwj3kFn3ZI9dcW4B0PY4ICi0FBQg1SfA4ng1Laq2LaK0a_rn7NZnDj5Fkn8hRV6PP55UT7Mxi8HNTkqeAqfTeKXJTJZSu_SZfARkkEVHTT9mJRLHyySwl0oW4elTTq-rz1B
        ShopName = EdtShopName.getText().toString();
        ShopCity = EdtShopCity.getText().toString();
        ShopArea = EdtShopArea.getText().toString();
        ShopAddress = EdtShopAddress.getText().toString();
        MobNumber = EdtMobileNumber.getText().toString();
        EmailId = EdtEmailId.getText().toString();
        Password = EdtPassword.getText().toString();
        ConfirmPassword = EdtConfirmPassword.getText().toString();


        if (isNetworkAvailable(RegistrationActivity.this)) {
            if (ShopName == null || ShopName.equals("") || ShopCity == null || ShopCity.equals("") || ShopArea == null || ShopArea.equals("") || ShopAddress == null || ShopAddress.equals("") || MobNumber == null || MobNumber.equals("") || EmailId == null || EmailId.equals("") || ConfirmPassword == null || ConfirmPassword.equals("") || Password == null || Password.equals("")) {
                Toast.makeText(getApplicationContext(), "Enter all valid fields", Toast.LENGTH_LONG).show();
            } else if (EdtShopName.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter Shop Name", Toast.LENGTH_LONG).show();
            } else if (EdtShopCity.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter City", Toast.LENGTH_LONG).show();
            } else if (EdtShopArea.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter Area", Toast.LENGTH_LONG).show();
            } else if (EdtShopAddress.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter Shop address", Toast.LENGTH_LONG).show();
            } else if (MobNumber.length() < 10) {
                Toast.makeText(getApplicationContext(), "Enter correct Mobile number", Toast.LENGTH_LONG).show();
            } else if (!isValidEmail(EmailId)) {
                Toast.makeText(getApplicationContext(), "Invalid Email id", Toast.LENGTH_LONG).show();
            } else if (Password.length() < 4) {
                Toast.makeText(getApplicationContext(), "Enter 4 Digit PIN", Toast.LENGTH_LONG).show();
            } else if (!ConfirmPassword.equals(Password)) {
                Toast.makeText(getApplicationContext(), "PIN missmatch", Toast.LENGTH_LONG).show();
            } else {
//                LatLng latlong = getLocationFromAddress(RegistrationActivity.this, ShopCity + "," + ShopArea);
//
//                m_lat = latlong.latitude;
//                m_long = latlong.longitude;
                m_lat = 73.7898537;
                m_long = 73.7898537;
                if (FCMtoken != null) {

                    new UpdateRegistrationDetails().execute();
                  //  new SendRegistrationDetails().execute();
                    progressDialog(progress, "Loading", "Please wait...");
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public class SendRegistrationDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
//{"ShopName":"Payal Grocery Shop","ShopAddress":"Right Bhusari Colony","MobileNo":"9096326024","EmailId":"testt@sveltoz.com","DeviceId":"eiQQHW8VLnQ:APA91bENpTcQMoXZL_hRugz_1ZAcukh7-hzxUOuVvGZB7oTns55RhhnMeDzbbiQ4beV9TNXqsS6zXVnrV7nZjgb8tawYz5TJGrCKR0z8-po8Mw7H3y7_v2q1ikIcLMYBWDutgjeV_XwD","City":"Pune","Area":"Kothrud","ShopImage":"dbbdbfgg ","Password":"1111","MerchantLattitude":18.5073985,"MerchantLongitude":73.8076504}
            try {//http://202.88.154.118/GroceryWebAPI/api/Home/InsertRegistrationDetailsOfMerchant
                String url = baseUrl + "InsertRegistrationDetailsOfMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("ShopName", ShopName);
                jsonObject.accumulate("ShopAddress", ShopAddress);
                jsonObject.accumulate("MobileNo", MobNumber);
                jsonObject.accumulate("EmailId", EmailId);
                jsonObject.accumulate("DeviceId", FCMtoken);
                jsonObject.accumulate("City", ShopCity);
                jsonObject.accumulate("Area", ShopArea);
                jsonObject.accumulate("ProvideDelivery", "null");
                jsonObject.accumulate("ShopImage", "dbbdbfgg ");
                jsonObject.accumulate("Password", Password);
                jsonObject.accumulate("MerchantLattitude", m_lat);
                jsonObject.accumulate("MerchantLongitude", m_long);
//{"ShopName":"Payal Grocery Shop","ShopAddress":"Right Bhusari Colony","MobileNo":"9096326024","EmailId":"test@sveltoz.com","DeviceId":"eiQQHW8VLnQ:APA91bENpTcQMoXZL_hRugz_1ZAcukh7-hzxUOuVvGZB7oTns55RhhnMeDzbbiQ4beV9TNXqsS6zXVnrV7nZjgb8tawYz5TJGrCKR0z8-po8Mw7H3y7_v2q1ikIcLMYBWDutgjeV_XwD","City":"Pune","Area":"Kothrud","ProvideDelivery":"null","ShopImage":"dbbdbfgg ","Password":"1111","MerchantLattitude":18.5073985,"MerchantLongitude":73.8076504}
                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();
                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);
                se.setContentType("application/json");
                // 6. set httpPost Entity
                httpPost.setEntity(se);
                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                // 9. receive response as inputStream
                tempstatus = httpResponse.getStatusLine().getStatusCode();
                inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    result = result.replace("[", "");
                    result = result.replace("]", "");
                    JSONObject mainObject = new JSONObject(result);
                    /// String areaID = mainObject.getString("$id");
                    responceStatus = mainObject.getString("Status");
                    responseUserId = mainObject.getString("UserId");
                } else
                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(RegistrationActivity.this, "1 RegistrationActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {
                progress.dismiss();
                ////////Code for store loc in database
                db.addMerchant(new Merchant(Integer.parseInt(responseUserId),FCMtoken, ShopName, EmailId, KYCStatus, SettingStatus));

                //  db.AddRegistrationStatus(new RegistrationStatus(KYCStatus, SettingStatus));

                Intent i = new Intent(getApplicationContext(), UploadShopDetailsActivity.class);
                startActivity(i);

//                Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(i);
                //   Toast.makeText(getApplicationContext(), "Register", Toast.LENGTH_LONG).show();
            } else {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "User already exist", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class UpdateRegistrationDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {//http://202.88.154.118/GroceryWebAPI/api/Home/InsertRegistrationDetailsOfMerchant
                String url = baseUrl + "UpdateRegistrationDetailsOfMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", mid);
                jsonObject.accumulate("ShopName", ShopName);
                jsonObject.accumulate("ShopAddress", ShopAddress);
                jsonObject.accumulate("MobileNo", MobNumber);
                jsonObject.accumulate("EmailId", EmailId);
                jsonObject.accumulate("DeviceId", FCMtoken);
                jsonObject.accumulate("City", ShopCity);
                jsonObject.accumulate("Area", ShopArea);
                jsonObject.accumulate("ProvideDelivery", "null");
                jsonObject.accumulate("ShopImage", "dbbdbfgg ");
                jsonObject.accumulate("Password", Password);
                jsonObject.accumulate("MerchantLattitude", m_lat);
                jsonObject.accumulate("MerchantLongitude", m_long);
                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();
                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);
                se.setContentType("application/json");
                // 6. set httpPost Entity
                httpPost.setEntity(se);
                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                // 9. receive response as inputStream
                tempstatus = httpResponse.getStatusLine().getStatusCode();
                inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    result = result.replace("[", "");
                    result = result.replace("]", "");
                    JSONObject mainObject = new JSONObject(result);
                    /// String areaID = mainObject.getString("$id");
                    responceStatus = mainObject.getString("Status");
                    responseUserId = mainObject.getString("UserId");
                } else
                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(RegistrationActivity.this, "1 RegistrationActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {
                progress.dismiss();
                ////////Code for store loc in database
                db.addMerchant(new Merchant(Integer.parseInt(responseUserId),FCMtoken, ShopName, EmailId, KYCStatus, SettingStatus));

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            } else {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Error,Please try letter", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = dff.format(Calendar.getInstance().getTime());
            Log.d("InputStream", ex.getLocalizedMessage());
            ex.printStackTrace();
            appendLog(RegistrationActivity.this, "2 RegistrationActivity " + ex.toString() + date);
        }
        return p1;
    }

    // Double back press exit
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


}
