package myapp.com.groceryshopmerchant;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Collections;
import java.util.List;

import myapp.com.groceryshopmerchant.Adapters.MainActivityOrderListAdapter;
import myapp.com.groceryshopmerchant.DBHandlers.CustomerOrderDetails;
import myapp.com.groceryshopmerchant.DBHandlers.DatabaseHandler;
import myapp.com.groceryshopmerchant.DBHandlers.Merchant;

import static myapp.com.groceryshopmerchant.Constants.Constants.appendLog;
import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;
import static myapp.com.groceryshopmerchant.Constants.Constants.isNetworkAvailable;
import static myapp.com.groceryshopmerchant.Constants.Constants.progressDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    CustomerOrderDetails ordertoaccept;
    public static SwipeRefreshLayout swipeRefreshLayout;
    ListView ListViewOrderList;
    ArrayList<CustomerOrderDetails> CustomerOrderList = new ArrayList<CustomerOrderDetails>();
    public static MainActivityOrderListAdapter orderlistadapter;

    int status;
    int tempstatus;
    String responceStatus;
    InputStream inputStream = null;
    String result = null;
    List<Merchant> merchant;
    int count, MerchantId;
    String ShopName, Manufacturer, EmailId, Unit;
    DatabaseHandler db = new DatabaseHandler(this);
    ArrayList<CustomerOrderDetails> SearchedOrderList = new ArrayList<CustomerOrderDetails>();
    SearchView searchView;
    String OrderId;
    String OrderStatus;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(MainActivity.this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainview_swipe_refresh_layout);
        ListViewOrderList = (ListView) findViewById(R.id.list_customer_order);


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

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle(ShopName);
      //  ab.setSubtitle("Merchant Id " + MerchantId);

        // MerchantId = 1;

  /*      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent i = new Intent(getApplicationContext(), AddMasterProductActivity.class);
                startActivity(i);
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        TextView Tv_shop_name = (TextView) hView.findViewById(R.id.tv_drawer_title);
        Tv_shop_name.setText(ShopName);
      //  ImageView ImgVw_shop_img = (ImageView) hView.findViewById(R.id.imageView);
      //  Picasso.with(getApplicationContext()).load("http://202.88.154.118/GroceryWebAPI/api/Home/GetShopImage?filename=45KYCShopImage.jpg").fit().into(ImgVw_shop_img);
        TextView Tv_shop_address = (TextView) hView.findViewById(R.id.tv_drawer_subtitle);
        Tv_shop_address.setText(EmailId);


        if (isNetworkAvailable(MainActivity.this)) {
            new getOrderListInMerchant().execute();
            progressDialog(progress, "Loading", "Please wait...");
        } else {
            Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();

        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isNetworkAvailable(MainActivity.this)) {
                    CustomerOrderList.clear();
                    swipeRefreshLayout.setRefreshing(true);
                    new getOrderListInMerchant().execute();
                } else {
                    swipeRefreshLayout.setRefreshing(false);

                    Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                SearchedOrderList.clear();
                if (!newText.equals("")) {
                    doubleBackToExitPressedOnce = false;
                    orderlistadapter.filter(newText);
                }
                return false;
            }
        });
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drawer_menu_addProduct) {
            Intent i = new Intent(getApplicationContext(), AddMasterProductActivity.class);
            startActivity(i);

            // Handle the camera action
        } else if (id == R.id.drawer_menu_productList) {
            Intent i = new Intent(getApplicationContext(), ProductListActivity.class);
            startActivity(i);

        } else if (id == R.id.drawer_menu_settings) {
            Intent i = new Intent(getApplicationContext(), ProfileSettingActivity.class);
            startActivity(i);

        } else if (id == R.id.drawer_menu_addnewproduct) {
             Intent i = new Intent(getApplicationContext(), AddMerchantProductActivity.class);
             startActivity(i);

        }
        /* else if (id == R.id.nav_share) {

            Intent i = new Intent(getApplicationContext(), UploadShopDetailsActivity.class);
            startActivity(i);


        }*/ else if (id == R.id.drawer_menu_logout) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class getOrderListInMerchant extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //   http://192.168.10.128/GroceryWebAPI/api/Home/getOrderListInMerchant
            try {
                String url = baseUrl + "getOrderListInMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
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
                        customerorderDetails.set_date(object.getString("Date"));
                        customerorderDetails.set_time(object.getString("Time"));
                        customerorderDetails.set_customerName(object.getString("CustomerName"));
                        customerorderDetails.set_order_id(object.getString("OrderId"));
                        customerorderDetails.set_total_price(object.getString("TotalPrice"));
                        customerorderDetails.set_orderStatus(object.getString("OrderStatus"));
                        customerorderDetails.set_address_id(object.getInt("DeliveryAddressId"));
                        customerorderDetails.set_order_type(object.getString("OrderType"));
                        CustomerOrderList.add(customerorderDetails);

                    }
                } else
                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(MainActivity.this, "1 MainActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            swipeRefreshLayout.setRefreshing(false);
            if (tempstatus == 200) {
                //  if (responceStatus.equals("success")) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                Collections.reverse(CustomerOrderList);

                swipeRefreshLayout.setRefreshing(false);
                orderlistadapter = new MainActivityOrderListAdapter(MainActivity.this,
                        R.layout.customer_order__list, CustomerOrderList);
                ListViewOrderList.setAdapter(orderlistadapter);
                orderlistadapter.notifyDataSetChanged();
            } else {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Double back press exit
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
            searchView.setIconified(true);

            searchView.onActionViewCollapsed();
            searchView.setQuery("", false);
            searchView.clearFocus();
            orderlistadapter.filter("");

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public void BtnAcceptOrder(final View v) {
        //http://192.168.10.128/GroceryWebAPI/api/Home/UpdateOrderStatusMerchant
        //MerchantId:34
        //OrderId:2
        //OrderStatus:Cancelled/Accepted

        //   Toast.makeText(getApplicationContext(), "AcceptOrder pending", Toast.LENGTH_LONG).show();
        ordertoaccept = (CustomerOrderDetails) v.getTag();

        // ProductId = ordertoaccept.get_productId();
        // new ProductListActivity.RemoveProduct().execute();

        OrderId = ordertoaccept.get_order_id();
        OrderStatus = "Accepted";
        new SendOrderStatus().execute();

    }

    public void TVCancelOrder(final View v) {
        //http://192.168.10.128/GroceryWebAPI/api/Home/UpdateOrderStatusMerchant
        //MerchantId:34
        //OrderId:2
        //OrderStatus:Cancelled/Accepted
        //Toast.makeText(getApplicationContext(), "CancelOrder pending", Toast.LENGTH_LONG).show();

        ordertoaccept = (CustomerOrderDetails) v.getTag();
        OrderId = ordertoaccept.get_order_id();
        OrderStatus = "Cancelled";
        CancelOrder();
    }

    public void CancelOrder() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Cancel order");
        alertDialogBuilder.setMessage("Are you sure you want to cancel this order ? ");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        new SendOrderStatus().execute();


                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
                jsonObject.accumulate("MerchantId", MerchantId);//40
                jsonObject.accumulate("OrderId", OrderId);//142
                jsonObject.accumulate("OrderStatus", OrderStatus);//Accepted

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
                appendLog(MainActivity.this, "2 MainActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {
//                Intent i = new Intent(getApplicationContext(), UploadShopDetailsActivity.class);
//                startActivity(i);
                if (OrderStatus.equals("Accepted")) {
                    Toast.makeText(getApplicationContext(), "Order Accepted", Toast.LENGTH_LONG).show();
                }
                if (OrderStatus.equals("Cancelled")) {
                    Toast.makeText(getApplicationContext(), "Order Cancelled", Toast.LENGTH_LONG).show();
                }
                new getOrderListInMerchant().execute();
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
}
