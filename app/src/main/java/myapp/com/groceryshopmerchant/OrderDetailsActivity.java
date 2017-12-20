package myapp.com.groceryshopmerchant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import myapp.com.groceryshopmerchant.Adapters.OrderDetailsListAdapter;
import myapp.com.groceryshopmerchant.DBHandlers.CustomerOrderDetails;
import myapp.com.groceryshopmerchant.DBHandlers.DatabaseHandler;
import myapp.com.groceryshopmerchant.DBHandlers.Merchant;

import static myapp.com.groceryshopmerchant.Adapters.MainActivityOrderListAdapter.ProductIdKey;
import static myapp.com.groceryshopmerchant.Adapters.MainActivityOrderListAdapter.ProductOrderAddressIdKey;
import static myapp.com.groceryshopmerchant.Adapters.MainActivityOrderListAdapter.ProductOrderStatusKey;
import static myapp.com.groceryshopmerchant.Adapters.MainActivityOrderListAdapter.ProductOrderTypeKey;
import static myapp.com.groceryshopmerchant.Adapters.MainActivityOrderListAdapter.mypreference;
import static myapp.com.groceryshopmerchant.Constants.Constants.appendLog;
import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;
import static myapp.com.groceryshopmerchant.Constants.Constants.isNetworkAvailable;
import static myapp.com.groceryshopmerchant.Constants.Constants.progressDialog;


public class OrderDetailsActivity extends AppCompatActivity {
    CustomerOrderDetails ordertopacked;
    ListView ListViewOrderDetails;
    ArrayList<CustomerOrderDetails> CustomerOrderList = new ArrayList<CustomerOrderDetails>();
    ArrayList<CustomerOrderDetails> DispatchOrderList = new ArrayList<CustomerOrderDetails>();
    public static OrderDetailsListAdapter orderdetailslistadapter;
    String OrderId, ProductUnit, OrderType;
    TextView TvTotalItems, TvTotalAmmount;
    Double total = 0.00;
    SharedPreferences sharedpreferences;
    List<Merchant> merchant;
    int status;
    int tempstatus;
    InputStream inputStream = null;
    String result = null;
    String responceStatus;
    int count, MerchantId, AddressId;
    String ShopName, Manufacturer, EmailId, Unit;
    DatabaseHandler db = new DatabaseHandler(this);
    ProgressDialog progress;
    Button BtnDispatch;
    String name = "";
    String OrderStatus, TotalAmmount, CustomerName, MobileNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        progress = new ProgressDialog(OrderDetailsActivity.this);

        TvTotalItems = (TextView) findViewById(R.id.tv_total_items);
        TvTotalAmmount = (TextView) findViewById(R.id.tv_total_ammount);
        ListViewOrderDetails = (ListView) findViewById(R.id.list_order_details);
        BtnDispatch = (Button) findViewById(R.id.btn_dispatch);
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

        if (sharedpreferences.contains(ProductOrderAddressIdKey)) {
            AddressId = sharedpreferences.getInt(ProductOrderAddressIdKey, 0);
        }

        if (sharedpreferences.contains(ProductOrderTypeKey)) {
            OrderType = sharedpreferences.getString(ProductOrderTypeKey, "0");
        }


        if (OrderStatus.equals("Cancelled")) {
            BtnDispatch.setText("Order Cancelled");
            BtnDispatch.setClickable(false);
            BtnDispatch.setEnabled(false);
        }
        if (OrderStatus.equals("Dispatch")) {
            BtnDispatch.setText("View Address");
        }
        if (OrderStatus.equals("Delivered")) {
            BtnDispatch.setText("View Address");
        }
        if (isNetworkAvailable(OrderDetailsActivity.this)) {
            progressDialog(progress, "Loading", "Please wait...");
            new GetOrderListDetailsInMerchant().execute();
        } else {
            Toast.makeText(OrderDetailsActivity.this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
        }

        BtnDispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (OrderStatus.equals("Dispatch"))

                {
                    Intent i = new Intent(getApplicationContext(), DeliveryAddressActivity.class);
                    startActivity(i);
                } else if (OrderStatus.equals("Delivered")) {
                    Intent i = new Intent(getApplicationContext(), DeliveryAddressActivity.class);
                    startActivity(i);
                } else {
                    name = "";
                    DispatchOrderList.clear();
                    for (CustomerOrderDetails wp : CustomerOrderList) {
                        if (wp.getPacked_flag().equals("unpacked")) {

                            CustomerOrderDetails customerorderDetails = new CustomerOrderDetails();
                            customerorderDetails.set_productId(wp.get_productId());
                            customerorderDetails.set_productName(wp.get_productName());
                            customerorderDetails.set_productImage(wp.get_productImage());
                            customerorderDetails.set_manufacturer(wp.get_manufacturer());
                            customerorderDetails.set_categoryName(wp.get_categoryName());
                            customerorderDetails.set_unit(wp.get_unit());
                            customerorderDetails.set_totalRatings(wp.get_totalRatings());
                            customerorderDetails.set_price(wp.get_price());
                            customerorderDetails.set_mrp(wp.get_mrp());
                            customerorderDetails.setPacked_flag(wp.getPacked_flag());

                            DispatchOrderList.add(customerorderDetails);

                            name = name + wp.get_productName() + ", ";
                        }

                    }

                    if (OrderStatus.equals("Placed")) {

                        Toast.makeText(getApplicationContext(), "Accept order first", Toast.LENGTH_LONG).show();

                    } else {
                        if (DispatchOrderList.size() == CustomerOrderList.size()) {
                            Toast.makeText(getApplicationContext(), "Select product to dispatch", Toast.LENGTH_LONG).show();

                        } else if (DispatchOrderList.isEmpty()) {
                            OrderStatus = "Dispatch";
                            new SendOrderStatus().execute();

                        }
                        else {
                       //     DispatchOrder();
                            Toast.makeText(getApplicationContext(), "Need to Pack all product ", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });


    }

//    public void DispatchOrder() {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setTitle("Pack all product");
//        alertDialogBuilder.setMessage(name);
//        alertDialogBuilder.setPositiveButton("yes",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        OrderStatus = "Dispatch";
//                        new SendOrderStatus().execute();
//
//
//                    }
//                });
//
//        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // finish();
//            }
//        });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }


    public class GetOrderListDetailsInMerchant extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //   http://192.168.10.128/GroceryWebAPI/api/Home/getOrderListInMerchant
            try {//http://202.88.154.118/GroceryWebAPI/api/Home/getOrderDetailsInMerchant
                String url = baseUrl + "getOrderDetailsInMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);//
                jsonObject.accumulate("OrderId", OrderId);//77  MerchantId = 40  OrderId = 77

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
//
                if (status == 200) {
                    CustomerOrderList.clear();
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONArray mainObject = new JSONArray(data);
                    JSONObject othposjsonobj = mainObject.getJSONObject(0);
                    JSONArray cityArray = othposjsonobj.getJSONArray("data");
                    for (int n = 0; n < cityArray.length(); n++) {
                        JSONObject object = cityArray.getJSONObject(n);

                        CustomerOrderDetails customerorderDetails = new CustomerOrderDetails();
                        customerorderDetails.set_productId(object.getInt("ProductId"));
                        customerorderDetails.set_productName(object.getString("ProductName"));
                        customerorderDetails.set_productImage(object.getString("ProductImage"));
                        customerorderDetails.set_manufacturer(object.getString("Manufacturer"));
                        customerorderDetails.set_categoryName(object.getString("CategoryName"));
                        customerorderDetails.set_unit(object.getString("Unit"));
                        customerorderDetails.set_quantity(object.getInt("Qty"));
                        customerorderDetails.set_price(object.getLong("Price"));
                        // customerorderDetails.set_mrp(object.getLong("MRP"));
                        customerorderDetails.setPacked_flag("unpacked");
                        customerorderDetails.set_orderStatus(OrderStatus);

                        CustomerOrderList.add(customerorderDetails);

                        total = total + object.getLong("Price") * object.getLong("Qty");
                    }
                } else
                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(OrderDetailsActivity.this, "1 OrderDetailsActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (tempstatus == 200) {
                //  if (responceStatus.equals("success")) {
                progress.dismiss();
                TvTotalItems.setText("Total Items : " + String.valueOf(CustomerOrderList.size()));
                TvTotalAmmount.setText("Total Ammount : ₹ " + String.valueOf(total));
                orderdetailslistadapter = new OrderDetailsListAdapter(OrderDetailsActivity.this,
                        R.layout.order_details__list, CustomerOrderList);
                ListViewOrderDetails.setAdapter(orderdetailslistadapter);
                orderdetailslistadapter.notifyDataSetChanged();
            } else {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void ProductPacked(final View v) {
        ordertopacked = (CustomerOrderDetails) v.getTag();
        ProductUnit = ordertopacked.get_unit();

        for (CustomerOrderDetails wp : CustomerOrderList) {
            if (wp.get_unit() == ProductUnit) {
                if (wp.getPacked_flag().equals("packed")) {
                    wp.setPacked_flag("unpacked");
                } else
                    wp.setPacked_flag("packed");
            }
        }
        orderdetailslistadapter.notifyDataSetChanged();

    }


    public class SendOrderStatus extends AsyncTask<String, Void, String> {
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
                appendLog(OrderDetailsActivity.this, "2 OrderDetailsActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {
                Toast.makeText(getApplicationContext(), "Order dispatch successfully...", Toast.LENGTH_LONG).show();

                Intent i = new Intent(getApplicationContext(), DeliveryAddressActivity.class);
                startActivity(i);


            } else {
                Toast.makeText(getApplicationContext(), responceStatus, Toast.LENGTH_LONG).show();
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
