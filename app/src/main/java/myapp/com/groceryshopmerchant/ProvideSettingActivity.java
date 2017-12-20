package myapp.com.groceryshopmerchant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class ProvideSettingActivity extends AppCompatActivity {
    CheckBox CBDeliveryOption, CheckBoxCharges;
    LinearLayout LLTime, LLCharges;
    CheckBox CheckBox_Time, CheckBox_Charges;
    String DeliveryOption = "no", DeliveryCharges = "no";
    Button Save;
    EditText EdtHomeDeliveryTime, EdtDeliveryCharges;

    String Time, Charges,ShopOpenTime,ShopCloseTime;

    InputStream inputStream = null;
    String result = null;
    String responceStatus;
    ProgressDialog progress;
    int tempstatus;
    int count;
    static int MerchantId;
    String ShopName, EmailId;
    List<Merchant> merchant;
    DatabaseHandler db = new DatabaseHandler(this);
    ProgressDialog dialog;
    String KYCStatus = "fail", SettingStatus = "fail";

    EditText EdtShopOpenTime,EdtShopCloseTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provide_setting);

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


        LLTime = (LinearLayout) findViewById(R.id.registration_setting_reglinearlayout_time);
        LLCharges = (LinearLayout) findViewById(R.id.registration_setting_reglinearlayout_charges);

        EdtHomeDeliveryTime = (EditText) findViewById(R.id.registration_setting_edt_home_delivery_time);
        EdtDeliveryCharges = (EditText) findViewById(R.id.registration_setting_edt_home_delivery_charges);
        EdtShopOpenTime = (EditText) findViewById(R.id.registration_setting_edt_shop_open_time);
        EdtShopCloseTime = (EditText) findViewById(R.id.registration_setting_edt_shop_close_time);
        Save = (Button) findViewById(R.id.registration_setting_btn_save);


        LLTime.setVisibility(View.GONE);
        LLCharges.setVisibility(View.GONE);

        CBDeliveryOption = (CheckBox) findViewById(R.id.registration_setting_checkBox_time);
        CheckBoxCharges = (CheckBox) findViewById(R.id.registration_setting_checkBox_charges);
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

                ShopOpenTime=EdtShopOpenTime.getText().toString();
                ShopCloseTime=EdtShopCloseTime.getText().toString();
                if (DeliveryOption.equals("yes")) {
                    Time = EdtHomeDeliveryTime.getText().toString();
                }
                if (DeliveryCharges.equals("yes")) {
                    Charges = EdtDeliveryCharges.getText().toString();
                }

                //if both are not selected
                if (!CBDeliveryOption.isChecked() && !CheckBoxCharges.isChecked()) {
                    Time="null";
                    Charges="null";

                    new SendSettingsDetails().execute();

                }

                //if both are selected
                if (CBDeliveryOption.isChecked() && CheckBoxCharges.isChecked()) {
                    if (Time.equals(null) || Time.equals("")) {
                        EdtHomeDeliveryTime.setError("Enter Time");
                        EdtHomeDeliveryTime.requestFocus();
                    } else if (Charges.equals(null) || Charges.equals("")) {
                        EdtDeliveryCharges.setError("Enter Charges");
                        EdtDeliveryCharges.requestFocus();
                    } else {
                        new SendSettingsDetails().execute();
                    }
                }

                //if Time selected
                if (CBDeliveryOption.isChecked() && !CheckBoxCharges.isChecked()) {
                    if (Time.equals(null) || Time.equals("")) {
                        EdtHomeDeliveryTime.setError("Enter Time");
                        EdtHomeDeliveryTime.requestFocus();
                    } else {
                        new SendSettingsDetails().execute();
                    }
                }

                //if Charges selected
                if (!CBDeliveryOption.isChecked() && CheckBoxCharges.isChecked()) {
                    if (Charges.equals(null) || Charges.equals("")) {
                        EdtDeliveryCharges.setError("Enter Charges");
                        EdtDeliveryCharges.requestFocus();
                    } else {
                        new SendSettingsDetails().execute();
                    }
                }

            }
        });
    }

    public class SendSettingsDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                String url = baseUrl + "AddDeliverySettingsOfMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
                jsonObject.accumulate("DeliveryTime", Time);
                jsonObject.accumulate("DeliveryCharges", Charges);
                jsonObject.accumulate("OpenTime", ShopOpenTime);
                jsonObject.accumulate("CloseTime", ShopCloseTime);

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

                } else
                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(ProvideSettingActivity.this, "1 ProvideSettingActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {
             //   progress.dismiss();
                ////////Code for store loc in database
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

                KYCStatus = "success";
                SettingStatus = "success";

                db.UpdateRegistrationStatus(MerchantId,KYCStatus, SettingStatus);
            } else {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}