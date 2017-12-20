package myapp.com.groceryshopmerchant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import myapp.com.groceryshopmerchant.DBHandlers.Address;
import myapp.com.groceryshopmerchant.DBHandlers.DatabaseHandler;
import myapp.com.groceryshopmerchant.DBHandlers.Merchant;

import static myapp.com.groceryshopmerchant.Adapters.MainActivityOrderListAdapter.ProductIdKey;
import static myapp.com.groceryshopmerchant.Adapters.MainActivityOrderListAdapter.ProductOrderAddressIdKey;
import static myapp.com.groceryshopmerchant.Adapters.MainActivityOrderListAdapter.ProductOrderStatusKey;
import static myapp.com.groceryshopmerchant.Adapters.MainActivityOrderListAdapter.ProductOrderTypeKey;
import static myapp.com.groceryshopmerchant.Adapters.MainActivityOrderListAdapter.ProductTotalPriceKey;
import static myapp.com.groceryshopmerchant.Adapters.MainActivityOrderListAdapter.mypreference;
import static myapp.com.groceryshopmerchant.Constants.Constants.appendLog;
import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;

public class DeliveryAddressActivity extends AppCompatActivity {


    InputStream inputStream = null;
    String result = null;
    String responceStatus;
    int status;
    int tempstatus;
    int addressId;
    String strStatus;
    Address addressObj;
    SharedPreferences sharedpreferences;
    String OrderId, OrderType, OrderStatus, TotalAmmount;
    int count, MerchantId, AddressId;
    String ShopName, Manufacturer, EmailId, Unit;
    List<Merchant> merchant;
    DatabaseHandler db = new DatabaseHandler(this);
    TextView tvTotal, txtName, txtAddress, txtMobileNumber, txtAlternetMobileNumber, txtxTitle;
    Button btnDeliverOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);

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

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains(ProductIdKey)) {
            OrderId = sharedpreferences.getString(ProductIdKey, "0");
        }
        if (sharedpreferences.contains(ProductOrderStatusKey)) {
            OrderStatus = sharedpreferences.getString(ProductOrderStatusKey, "0");
        }
        if (sharedpreferences.contains(ProductTotalPriceKey)) {
            TotalAmmount = sharedpreferences.getString(ProductTotalPriceKey, "0");
        }
        if (sharedpreferences.contains(ProductOrderAddressIdKey)) {
            AddressId = sharedpreferences.getInt(ProductOrderAddressIdKey, 0);
        }

        if (sharedpreferences.contains(ProductOrderTypeKey)) {
            OrderType = sharedpreferences.getString(ProductOrderTypeKey, "0");
        }


        tvTotal = (TextView) findViewById(R.id.tv_total_place_order);
        tvTotal.setText(TotalAmmount);
        txtName = (TextView) findViewById(R.id.tv_address_namePO);
        txtAddress = (TextView) findViewById(R.id.tv_addressPO);
        txtMobileNumber = (TextView) findViewById(R.id.tv_address_mobile_numberPO);
        txtAlternetMobileNumber = (TextView) findViewById(R.id.tv_address_alternate_mobile_numberPO);
        txtxTitle = (TextView) findViewById(R.id.tv_title);
        btnDeliverOrder = (Button) findViewById(R.id.btn_confirm_order);

        if (OrderStatus.equals("Delivered")) {
            btnDeliverOrder.setText("Order Delivered");
            btnDeliverOrder.setClickable(false);
            btnDeliverOrder.setEnabled(false);
        }

        btnDeliverOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OrderStatus = "Delivered";
                new UpdateOrderStatus().execute();

            }
        });

        if (AddressId == 0) {
            txtName.setText("Customer selected Pickup Option");
        } else {
            new JSONAsyncTaskToGetDeliveryAddress().execute();
        }
    }

    public class JSONAsyncTaskToGetDeliveryAddress extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                // code to consume wcf service which sends the details to the server
                String url = baseUrl + "getDeliveryAddressById";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("AddressId", AddressId);
                // jsonObject.accumulate("ProductId", productId);
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

                HttpResponse response = httpclient.execute(httpPost);
                // 9. receive response as inputStream
                // inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string

                // StatusLine stat = response.getStatusLine();
                status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONArray mainObject = new JSONArray(data);
                    JSONObject othposjsonobj = mainObject.getJSONObject(0);
                    strStatus = othposjsonobj.getString("Status");
                    JSONArray productArray = othposjsonobj.getJSONArray("data");

                    JSONObject object = productArray.getJSONObject(0);
                    addressObj = new Address();
                    addressObj.setAddressId(object.getInt("AddressId"));
                    addressObj.setAddressType(object.getString("AddressType"));
                    addressObj.setCity(object.getString("City"));
                    addressObj.setArea(object.getString("Area"));
                    addressObj.setBuilding(object.getString("BuildingName"));
                    addressObj.setPincode(object.getString("Pincode"));
                    addressObj.setState(object.getString("State"));
                    addressObj.setLandmark(object.getString("Landmark"));
                    addressObj.setName(object.getString("Name"));
                    addressObj.setPhonenumber(object.getString("PhoneNo"));
                    addressObj.setAlernatenumber(object.getString("AlternatePhoneNo"));
                    addressObj.setStatus("no");


                } else {
                    // result = "Did not work!";
                }
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(DeliveryAddressActivity.this, "1 DeliveryAddressActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (status == 200) {
                if (strStatus.equals("success")) {

                    if (!addressObj.getName().equals("null")) {
                        txtName.setText(addressObj.getName());
                    }

                    if (!addressObj.getLandmark().equals("null")) {
                        txtAddress.setText(addressObj.getBuilding() + ", " + addressObj.getArea() + ", " + addressObj.getLandmark() + ", " + addressObj.getCity() + ", " + addressObj.getState() + ", " + addressObj.getPincode());

                    } else {
                        txtAddress.setText(addressObj.getBuilding() + ", " + addressObj.getArea() + ", " + addressObj.getCity() + ", " + addressObj.getState() + ", " + addressObj.getPincode());

                    }
                    txtMobileNumber.setText(addressObj.getPhonenumber());
                    if (!addressObj.getAlernatenumber().equals("null")) {
                        txtAlternetMobileNumber.setText(addressObj.getAlernatenumber());
                    }
                    //    txtName.setText(addressObj.getName());
//                    txtAddress.setText(addressObj.getBuilding() + ", " + addressObj.getArea() + ", " + addressObj.getLandmark() + ", " + addressObj.getCity() + ", " + addressObj.getState() + ", " + addressObj.getPincode());
//                    txtMobileNumber.setText(addressObj.getPhonenumber());
//                    txtAlternetMobileNumber.setText(addressObj.getAlernatenumber());
                }
            }
        }
    }

    public class UpdateOrderStatus extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String url = baseUrl + "UpdateOrderStatusMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
                jsonObject.accumulate("OrderId", OrderId);
                jsonObject.accumulate("OrderStatus", OrderStatus);

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
                appendLog(DeliveryAddressActivity.this, "2 DeliveryAddressActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {
                Toast.makeText(getApplicationContext(), "Order delivered successfully...", Toast.LENGTH_LONG).show();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);


            } else {
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
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

    }
}
