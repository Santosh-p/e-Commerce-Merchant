package myapp.com.groceryshopmerchant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
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
import static myapp.com.groceryshopmerchant.Constants.Constants.progressDialog;

public class ProfileSettingActivity extends AppCompatActivity {
    ImageView ImgVwShopImage;
    TextView ImgEdtShopName;
    EditText EdtShopName, EdtShopCity, EdtShopArea, EdtShopAddress, EdtMobileNumber, EdtShopOpenTime, EdtShopCloseTime, EdtHomeDeliveryTime, EdtDeliveryCharges;
    CheckBox CBDeliveryOption, CheckBoxCharges;
    LinearLayout LLTime, LLCharges;
    Button Save;
    String ShopName, ShopCity, ShopArea, ShopAddress, MobNumber, ShopOpenTime, ShopCloseTime, Time, Charges;

    String KYCStatus = "fail", SettingStatus = "fail";
    String DeliveryOption = "no", DeliveryCharges = "no";

    InputStream inputStream = null;
    String result = null;
    String responceStatus;
    ProgressDialog progress;
    int tempstatus;
    int count;
    static int MerchantId;
    String EmailId;
    List<Merchant> merchant;
    DatabaseHandler db = new DatabaseHandler(this);
    ProgressDialog dialog;
    int status, custId;
    String Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        progress = new ProgressDialog(ProfileSettingActivity.this);

        merchant = db.getAllMerchants();
        count = 0;
        for (Merchant cn : merchant) {
            count++;
        }
        if ((count > 0)) {
            Merchant merchant = db.getMerchantDetails();
            MerchantId = merchant.get_user_id();
            ShopName = merchant.get_shop_name();
            EmailId = merchant.get_email_id();
        }

        EdtShopName = (EditText) findViewById(R.id.profile_edt_shop_name);
        EdtShopCity = (EditText) findViewById(R.id.shop_city);
        EdtShopArea = (EditText) findViewById(R.id.shop_area);
        EdtShopAddress = (EditText) findViewById(R.id.shop_address);
        EdtMobileNumber = (EditText) findViewById(R.id.mob_no);
        EdtHomeDeliveryTime = (EditText) findViewById(R.id.profile_edt_home_delivery_time);
        EdtDeliveryCharges = (EditText) findViewById(R.id.profile_edt_home_delivery_charges);
        EdtShopOpenTime = (EditText) findViewById(R.id.profile_edt_shop_open_time);
        EdtShopCloseTime = (EditText) findViewById(R.id.profile_edt_shop_close_time);
        Save = (Button) findViewById(R.id.profile_btn_save);
        LLTime = (LinearLayout) findViewById(R.id.profile_linearlayout_time);
        LLCharges = (LinearLayout) findViewById(R.id.profile_linearlayout_charges);
        CBDeliveryOption = (CheckBox) findViewById(R.id.profile_checkBox_time);
        CheckBoxCharges = (CheckBox) findViewById(R.id.profile_checkBox_charges);
//9922033099/8208162995
        Typeface fontAwesomeFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fontawesome-webfont.ttf");
        ImgVwShopImage = (ImageView) findViewById(R.id.profile_img_edt_shop_image);
        //     Picasso.with(getApplicationContext()).load("http://202.88.154.118/GroceryWebAPI/api/Home/GetShopImage?filename=45KYCShopImage.jpg").fit().into(ImgVwShopImage);

//        ImgEdtShopName = (TextView) findViewById(R.id.profile_tv_edt_shopname);
//        ImgEdtShopName.setTypeface(fontAwesomeFont);
//        ImgEdtShopName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "edit", Toast.LENGTH_LONG).show();
//
////                EdtShopName.setText("");
////                EdtShopOpenTime.setText("");
////                EdtShopCloseTime.setText("");
////
////                EdtShopName.setFocusable(true);
////                EdtShopName.setCursorVisible(true);
////                EdtShopName.setEnabled(true);
////
////                EdtShopOpenTime.setFocusable(true);
////                EdtShopOpenTime.setCursorVisible(true);
////                EdtShopCloseTime.setFocusable(true);
////                EdtShopCloseTime.setCursorVisible(true);
//
//
//            }
//        });


        LLTime.setVisibility(View.GONE);
        LLCharges.setVisibility(View.GONE);


        CBDeliveryOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    DeliveryOption = "yes";
                    LLTime.setVisibility(View.VISIBLE);
                } else {
                    DeliveryOption = "no";
                    LLTime.setVisibility(View.GONE);
                }
            }
        });
        CheckBoxCharges.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    DeliveryCharges = "yes";
                    LLCharges.setVisibility(View.VISIBLE);
                } else {
                    DeliveryCharges = "no";
                    LLCharges.setVisibility(View.GONE);
                }
            }
        });


        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShopName = EdtShopName.getText().toString();
                ShopCity = EdtShopCity.getText().toString();
                ShopArea = EdtShopArea.getText().toString();
                ShopAddress = EdtShopAddress.getText().toString();
                MobNumber = EdtMobileNumber.getText().toString();
                ShopOpenTime = EdtShopOpenTime.getText().toString();
                ShopCloseTime = EdtShopCloseTime.getText().toString();

                Time = EdtHomeDeliveryTime.getText().toString();
                Charges = EdtDeliveryCharges.getText().toString();
                ;

                if (isNetworkAvailable(ProfileSettingActivity.this)) {
                    if (ShopName == null || ShopName.equals("") || ShopCity == null || ShopCity.equals("") || ShopArea == null || ShopArea.equals("") || ShopAddress == null || ShopAddress.equals("") || MobNumber == null || MobNumber.equals("") || ShopOpenTime == null || ShopOpenTime.equals("") || ShopCloseTime == null || ShopCloseTime.equals("")) {
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
                    } else if (EdtShopOpenTime.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Enter Shop Open Time", Toast.LENGTH_LONG).show();
                    } else if (EdtShopCloseTime.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Enter Shop Close Time", Toast.LENGTH_LONG).show();
                    } else {

                        new SendEditProfileDetails().execute();

                        progressDialog(progress, "Loading", "Please wait...");
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();
                }


//                if (DeliveryOption.equals("yes")) {
//                    Time = EdtHomeDeliveryTime.getText().toString();
//                }
//                if (DeliveryCharges.equals("yes")) {
//                    Charges = EdtDeliveryCharges.getText().toString();
//                }
//                //if both are not selected
//                if (!CBDeliveryOption.isChecked() && !CheckBoxCharges.isChecked()) {
//                    Time = "null";
//                    Charges = "null";
//
//                    new SendSettingsDetails().execute();
//
//                }
//                //if both are selected
//                if (CBDeliveryOption.isChecked() && CheckBoxCharges.isChecked()) {
//                    if (Time.equals(null) || Time.equals("")) {
//                        EdtHomeDeliveryTime.setError("Enter Time");
//                        EdtHomeDeliveryTime.requestFocus();
//                    } else if (Charges.equals(null) || Charges.equals("")) {
//                        EdtDeliveryCharges.setError("Enter Charges");
//                        EdtDeliveryCharges.requestFocus();
//                    } else {
//                        new SendSettingsDetails().execute();
//                    }
//                }
//                //if Time selected
//                if (CBDeliveryOption.isChecked() && !CheckBoxCharges.isChecked()) {
//                    if (Time.equals(null) || Time.equals("")) {
//                        EdtHomeDeliveryTime.setError("Enter Time");
//                        EdtHomeDeliveryTime.requestFocus();
//                    } else {
//                        new SendSettingsDetails().execute();
//                    }
//                }
//                //if Charges selected
//                if (!CBDeliveryOption.isChecked() && CheckBoxCharges.isChecked()) {
//                    if (Charges.equals(null) || Charges.equals("")) {
//                        EdtDeliveryCharges.setError("Enter Charges");
//                        EdtDeliveryCharges.requestFocus();
//                    } else {
//                        new SendSettingsDetails().execute();
//                    }
//                }
//


            }
        });
        if (isNetworkAvailable(ProfileSettingActivity.this)) {
            new GetMerchantDetails().execute();
            progressDialog(progress, "Loading", "Please wait...");
        } else {
            Toast.makeText(ProfileSettingActivity.this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    public class GetMerchantDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                String url = baseUrl + "GetDetailsMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);//


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
                HttpResponse response = httpclient.execute(httpPost);

                // StatusLine stat = response.getStatusLine();
                status = response.getStatusLine().getStatusCode();
//[{"ShopName":"Ganesh","ShopAddress":"testaddress","MobileNo":"1234567890","ProvideDelivery":"yes","City":"pune","Area":"kothrud","OpenTime":"10am","CloseTime":"11pm","DeliveryTime":"null","DeliveryCharges":"200"}]
                if (status == 200) {
// , , , , , , , , Charges;
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONArray mainObject = new JSONArray(data);
                    JSONObject othposjsonobj = mainObject.getJSONObject(0);
                    JSONArray cityArray = othposjsonobj.getJSONArray("data");
                    for (int n = 0; n < cityArray.length(); n++) {
                        JSONObject object = cityArray.getJSONObject(n);
                        ShopName = object.getString("ShopName");
                        ShopCity = object.getString("City");
                        ShopArea = object.getString("Area");
                        ShopAddress = object.getString("ShopAddress");
                        MobNumber = object.getString("MobileNo");
                        ShopOpenTime = object.getString("OpenTime");
                        ShopCloseTime = object.getString("CloseTime");
                        DeliveryOption = object.getString("ProvideDelivery");
                        Time = object.getString("DeliveryTime");
                        Charges = object.getString("DeliveryCharges");
                    }
                } else
                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(ProfileSettingActivity.this, "11 ProfileSettingActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            if (tempstatus == 200) {
//
                EdtShopName.setText(ShopName);
                EdtShopCity.setText(ShopCity);
                EdtShopArea.setText(ShopArea);
                EdtShopAddress.setText(ShopAddress);
                EdtMobileNumber.setText(MobNumber);
                EdtShopOpenTime.setText(ShopOpenTime);
                EdtShopCloseTime.setText(ShopCloseTime);
                if (Time.equals("null") || Time.equals("")) {
                    LLTime.setVisibility(View.GONE);
                    CBDeliveryOption.setChecked(false);
                } else {
                    EdtHomeDeliveryTime.setText(Time);
                    LLTime.setVisibility(View.VISIBLE);
                    CBDeliveryOption.setChecked(true);


                }
                if (Charges.equals("null") || Charges.equals("")) {

                    LLCharges.setVisibility(View.GONE);
                    CheckBoxCharges.setChecked(false);
                } else {
                    LLCharges.setVisibility(View.VISIBLE);
                    EdtDeliveryCharges.setText(Charges);
                    CheckBoxCharges.setChecked(true);

                }
            } else {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class SendEditProfileDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {

                String url = baseUrl + "EditProfileMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("MerchantId", MerchantId);
                jsonObject.accumulate("ShopName", ShopName);
                jsonObject.accumulate("City", ShopCity);
                jsonObject.accumulate("Area", ShopArea);
                jsonObject.accumulate("ShopAddress", ShopAddress);
                jsonObject.accumulate("MobileNo", MobNumber);
                jsonObject.accumulate("OpenTime", ShopOpenTime);
                jsonObject.accumulate("CloseTime", ShopCloseTime);
                jsonObject.accumulate("ProvideDelivery", DeliveryOption);
                jsonObject.accumulate("DeliveryTime", Time);
                jsonObject.accumulate("DeliveryCharges", Charges);

//{"MerchantId":48,"ShopName":"Ganesh","City":"pune","Area":"kothrud","ShopAddress":"testaddress","MobileNo":"1234567890","OpenTime":"10am","CloseTime":"11pm","ProvideDelivery":"1234567890","DeliveryTime":"null","DeliveryCharges":"200"}
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
                HttpResponse response = httpclient.execute(httpPost);

                // StatusLine stat = response.getStatusLine();
                status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONArray mainObject = new JSONArray(data);
                    //[{"Status":"success","UserId":"6"}]
                    JSONObject othposjsonobj = mainObject.getJSONObject(0);
                    Status = othposjsonobj.getString("Status");
                }

            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(ProfileSettingActivity.this, "1 ProfileSettingActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            if (status == 200) {
                if (Status.equals("success")) {
                    //  db.updateProfile(new Customer(custId, name, mobileNumber));
                    Intent selectpickupAddressIntent = new Intent(ProfileSettingActivity.this, MainActivity.class);
                    startActivity(selectpickupAddressIntent);
                    Toast.makeText(getApplicationContext(), "Changes saved successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), " Error,Please try letter", Toast.LENGTH_LONG).show();
                }
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
}
