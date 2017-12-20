package myapp.com.groceryshopmerchant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.Random;

import myapp.com.groceryshopmerchant.DBHandlers.DatabaseHandler;
import myapp.com.groceryshopmerchant.DBHandlers.Merchant;

import static myapp.com.groceryshopmerchant.Constants.Constants.appendLog;
import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;
import static myapp.com.groceryshopmerchant.Constants.Constants.isNetworkAvailable;
import static myapp.com.groceryshopmerchant.Constants.Constants.progressDialog;

public class LoginActivity extends AppCompatActivity {

    Button BtnLogin;
    EditText EdtLoginPin;
    String LoginPin;
    ProgressDialog progress;
    InputStream inputStream = null;
    String result = null;
    String responceStatus, AdminStatus, responseUserId;
    List<Merchant> merchant;
    DatabaseHandler db = new DatabaseHandler(this);
    int count, MerchantId;
    String ShopName, EmailId, DeviceId;
    String KYCStatus, SettingStatus;
    TextView tvForgotLogin;
    public static String otp;
    int status;
    private static final String _CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progress = new ProgressDialog(LoginActivity.this);

        //Get Merchant details from local database
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

        tvForgotLogin = (TextView) findViewById(R.id.textviewForgotpass);
        tvForgotLogin.setText(Html.fromHtml("<u>Forgot Password</u>"));

        EdtLoginPin = (EditText) findViewById(R.id.login_pin);
        EdtLoginPin.setText("");
        BtnLogin = (Button) findViewById(R.id.login_button);
        EdtLoginPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pass = EdtLoginPin.getText().toString();

                if (pass.length() == 4) {
                    ActionLogin();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionLogin();
            }
        });

        tvForgotLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(LoginActivity.this)) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);


                    alertDialogBuilder.setMessage("Are you sure you want to send OTP to " + EmailId + "?");
                    alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            progressDialog(progress, "", "Sending OTP...");
                            otp = random();
                            new SendOtpDetails().execute();
                        }
                    });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    Toast.makeText(getApplicationContext(), "No network Available!", Toast.LENGTH_LONG).show();
                }
            }
        });


        merchant = db.getAllMerchants();
        count = 0;
        for (Merchant cn : merchant) {
            count++;
        }

        if (!(count > 0)) {
            Intent mainIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(mainIntent);
        }
        //Get Merchant Details
        if ((count > 0)) {
            Merchant merchant = db.getMerchantDetails();
            MerchantId = merchant.get_user_id();
            DeviceId = merchant.get_device_id();
            ShopName = merchant.get_shop_name();
            EmailId = merchant.get_email_id();

//            KYCStatus = merchant.get_kyc_status();
//            SettingStatus = merchant.get_setting_status();
//
//            if (!KYCStatus.equals("success")) {
//                Intent i = new Intent(getApplicationContext(), UploadShopDetailsActivity.class);
//                startActivity(i);
//            } else if (!SettingStatus.equals("success")) {
//                Intent i = new Intent(getApplicationContext(), ProvideSettingActivity.class);
//                startActivity(i);
//            }

        }
    }

    public void ActionLogin() {
        LoginPin = EdtLoginPin.getText().toString();
        if (isNetworkAvailable(LoginActivity.this)) {
            if (LoginPin == null || LoginPin.equals("")) {
                Toast.makeText(getApplicationContext(), "Enter PIN", Toast.LENGTH_LONG).show();
            } else if (LoginPin.length() < 4) {
                Toast.makeText(getApplicationContext(), "Enter 4 Digit PIN", Toast.LENGTH_LONG).show();
            } else {
                new SendLoginDetails().execute();
                progressDialog(progress, "Loading", "Please wait...");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();

        }
    }

    public class SendLoginDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {//http://192.168.10.143/GroceryWebAPI/api/Home/MerchantLogin
                String url = baseUrl + "MerchantLogin";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
                jsonObject.accumulate("Password", LoginPin);
                jsonObject.accumulate("DeviceId", DeviceId);

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
                status = httpResponse.getStatusLine().getStatusCode();
                if (status == 200) {
                    inputStream = httpResponse.getEntity().getContent();
                    // 10. convert inputstream to string
                    if (inputStream != null) {
                        result = convertInputStreamToString(inputStream);
                        result = result.replace("[", "");
                        result = result.replace("]", "");
                        JSONObject mainObject = new JSONObject(result);
                        /// String areaID = mainObject.getString("$id");
                        responceStatus = mainObject.getString("Status");
                        AdminStatus = mainObject.getString("AdminStatus");
                        // responseUserId = mainObject.getString("UserId");

                    } else
                        result = "Did not work!";
                }
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                //   appendLog(LoginActivity.this, "1 LoginActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            if (status == 200) {
                if (responceStatus.equals("success")) {
                    ////////Code for store loc in database
                    if (AdminStatus.equals("not approved")) {
                        ////////Code for store loc in database
                        // Toast.makeText(getApplicationContext(), "Merchant Not Approved", Toast.LENGTH_LONG).show();
                        AlertForApproval();
                    } else {

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                    }

                } else if (responceStatus.equals("Wrong DeviceId")) {
                    Toast.makeText(getApplicationContext(), "Application installed on other device", Toast.LENGTH_LONG).show();

                    EdtLoginPin.setText("");

                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect PIN", Toast.LENGTH_LONG).show();

                    EdtLoginPin.setText("");
                }
            }
            if (status == 503) {
                Toast.makeText(getApplicationContext(), "Temporary service not available", Toast.LENGTH_LONG).show();
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

    // Double back press exit
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //    super.onBackPressed();
            //   return;
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

    public void AlertForApproval() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Merchant not approved.");
        alertDialogBuilder.setMessage("Contact us at sspl@sveltoz.com. for approval");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //  Toast.makeText(LoginActivity.this,"Contact us at sspl@sveltoz.com",Toast.LENGTH_LONG).show();
                        //  new LoginActivity().UpdateProductDetails().execute();

                    }
                });

//        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        merchant = db.getAllMerchants();
        count = 0;
        for (Merchant cn : merchant) {
            count++;
        }
        if (!(count > 0)) {
            Intent mainIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(mainIntent);
        }

        EdtLoginPin.setText("");


        super.onStart();
    }

    public class SendOtpDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {

                String url = baseUrl + "SendEmailOtp";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Email", EmailId);
                jsonObject.accumulate("OTP", otp);

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
                inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    result = result.replace("[", "");
                    result = result.replace("]", "");
                    JSONObject mainObject = new JSONObject(result);

                    responceStatus = mainObject.getString("Status");

                } else
                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(LoginActivity.this, "1 LoginActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent i = new Intent(LoginActivity.this, SubmitOTPActivity.class);
            i.putExtra("OTP", otp);
            // i.putExtra("FROM", "LoginActivity");
            startActivity(i);
            progress.dismiss();
        }
    }


    // generate random OTP
    public String random() {
        StringBuffer randStr = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            int number = getRandomNumber();
            char ch = _CHAR.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }

    private int getRandomNumber() {
        int randomInt = 0;
        randomInt = random.nextInt(_CHAR.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }

}
