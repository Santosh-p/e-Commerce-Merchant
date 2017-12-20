package myapp.com.groceryshopmerchant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import myapp.com.groceryshopmerchant.DBHandlers.DatabaseHandler;

import static myapp.com.groceryshopmerchant.Constants.Constants.appendLog;
import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;
import static myapp.com.groceryshopmerchant.Constants.Constants.mid;
import static myapp.com.groceryshopmerchant.Constants.Constants.progressDialog;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText edtNewPass, edtConfirmPass;
    Button btnSubmit;
    String name, email, phone, pin, trackeepin, type, status;
    int userid;
    DatabaseHandler db = new DatabaseHandler(this);
    InputStream inputStream = null;
    String result = null;
    String responceStatus, responseUserId;
    int tempstatus;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        progress = new ProgressDialog(ChangePasswordActivity.this);
        edtNewPass = (EditText) findViewById(R.id.editTextNewPassword);
        edtNewPass.setFocusable(true);
        edtConfirmPass = (EditText) findViewById(R.id.editTextConfirmPassword);
        btnSubmit = (Button) findViewById(R.id.buttonSubmit);
        edtNewPass.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
                if (s.length() == 4) {
                    edtNewPass.setError(null);
                    if (edtConfirmPass.getText().toString().equals(edtNewPass.getText().toString())) {
                        edtConfirmPass.setError(null);
                        pin = edtConfirmPass.getText().toString();
                    } else {
                        edtConfirmPass.setError("PIN mismatch");
                        pin = null;
                    }
                    // trackerpin = edtTextPin.getText().toString();
                } else {
                    edtNewPass.setError("Enter 4 Digit PIN");
                    pin = null;
                }
            }
        });
        edtConfirmPass.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
                if (edtNewPass.getText().toString().equals(edtConfirmPass.getText().toString())) {
                    edtConfirmPass.setError(null);
                    pin = edtNewPass.getText().toString();
                } else {
                    edtConfirmPass.setError("PIN mismatch");
                    pin = null;
                }
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtNewPass.length() <= 3) {
                    Toast.makeText(getApplicationContext(), "Enter 4 digit no", Toast.LENGTH_LONG).show();

                } else if (!edtNewPass.getText().toString().equals(edtConfirmPass.getText().toString())) {
                    edtConfirmPass.setError("PIN mismatch");
                    edtConfirmPass.setFocusable(true);

                } else {
                    if (pin != null) {
                        new UpdatePassword().execute();
                        progressDialog(progress, "Loading", "Please wait...");
                    }
                }
            }
        });


    }

    public class UpdatePassword extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {//http://202.88.154.118/GroceryWebAPI/api/Home/InsertRegistrationDetailsOfMerchant
                String url = baseUrl + "ChangePwdMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", mid);

                jsonObject.accumulate("Password", pin);

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
                    result = result.replace("]", "");//  {    "Status": "success"  }
                    JSONObject mainObject = new JSONObject(result);
                    /// String areaID = mainObject.getString("$id");
                    responceStatus = mainObject.getString("Status");
                    //  responseUserId = mainObject.getString("UserId");
                } else
                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(ChangePasswordActivity.this, "1 ChangePasswordActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progress.isShowing()) {
                progress.dismiss();
            }

            if (responceStatus.equals("success")) {
             //   progress.dismiss();
                Toast.makeText(getApplicationContext(), "Password successfully changed", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            } else {
              //  progress.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to change password", Toast.LENGTH_LONG).show();
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
