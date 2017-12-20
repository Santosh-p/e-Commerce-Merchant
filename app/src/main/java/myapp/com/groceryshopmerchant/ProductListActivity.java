package myapp.com.groceryshopmerchant;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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

import myapp.com.groceryshopmerchant.Adapters.ProductDetailsListAdapter;
import myapp.com.groceryshopmerchant.DBHandlers.DatabaseHandler;
import myapp.com.groceryshopmerchant.DBHandlers.Merchant;
import myapp.com.groceryshopmerchant.DBHandlers.ProductDetails;

import static myapp.com.groceryshopmerchant.Constants.Constants.appendLog;
import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;
import static myapp.com.groceryshopmerchant.Constants.Constants.isNetworkAvailable;
import static myapp.com.groceryshopmerchant.Constants.Constants.progressDialog;

public class ProductListActivity extends AppCompatActivity {
    ProductDetails itemtoedit;
    ListView productlist;
    int status;
    int tempstatus;
    InputStream inputStream = null;
    String result = null;
    String responceStatus;
    int count, MerchantId;
    String ShopName, Manufacturer, EmailId, Unit;
    List<Merchant> merchant;
    ArrayList<ProductDetails> ProductDetailsList = new ArrayList<ProductDetails>();
    ArrayList<ProductDetails> SearchedProductDetailsList = new ArrayList<ProductDetails>();
    DatabaseHandler db = new DatabaseHandler(this);
    public static ProductDetailsListAdapter imageItemAdapter;
    String CategoryId;
    int ProductId;
    public static SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String MerchantIdKey = "MerchantIdKey";
    public static final String ProductIdKey = "ProductIdKey";
    public static final String ProductOrderStatusKey = "ProductOrderStatusKey";
    boolean doubleBackToExitPressedOnce = true;
    SearchView searchView;
    ProgressDialog progress;
    String catName;
    int catId;
    String stat;
    String filter="no";

    //  private ProductDetailsListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        progress = new ProgressDialog(ProductListActivity.this);

       // getSupportActionBar().setHomeButtonEnabled(true);
        productlist = (ListView) findViewById(R.id.list_product);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(getApplicationContext());
//        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
//    }
//
//
//    @Override public void onRefresh() {
//        new Handler().postDelayed(new Runnable() {
//            @Override public void run() {
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        }, 5000);


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


        if (isNetworkAvailable(ProductListActivity.this)) {
            new GetAllProducts().execute();
            progressDialog(progress, "Loading", "Please wait...");
        } else {
            Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();

        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isNetworkAvailable(ProductListActivity.this)) {
                    ProductDetailsList.clear();
                    swipeRefreshLayout.setRefreshing(true);
                    new GetAllProducts().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);

                }


            }
        });
    }

    public class GetAllProducts extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {//http://192.168.10.143/GroceryWebAPI/api/Home/getAllProductsOfMerchant
                String url = baseUrl + "getAllProductsOfMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
//{"MerchantId":"1"}
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
                    ProductDetailsList.clear();
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONArray mainObject = new JSONArray(data);
                    JSONObject othposjsonobj = mainObject.getJSONObject(0);
                    JSONArray cityArray = othposjsonobj.getJSONArray("data");
                    for (int n = 0; n < cityArray.length(); n++) {
                        JSONObject object = cityArray.getJSONObject(n);

                        JSONArray productUnitArr = object.getJSONArray("UnitArr");

                        ProductDetails productdetails = new ProductDetails();
                        productdetails.set_productId(object.getInt("ProductId"));
                        productdetails.set_productName(object.getString("ProductName"));
                        productdetails.set_productImage(object.getString("ProductImage"));
                        productdetails.set_manufacturer(object.getString("Manufacturer"));
                        productdetails.set_categoryName(object.getString("CategoryName"));

                        int abc = productUnitArr.length();
                        productdetails.setUnit_count(abc);

                        if (abc == 1) {
                            JSONObject uObject = productUnitArr.getJSONObject(0);
                            productdetails.set_unit(uObject.getString("Unit"));
                            productdetails.set_price(uObject.getDouble("Price"));
                            productdetails.set_mrp(uObject.getDouble("MRP"));

                        }

                        ProductDetailsList.add(productdetails);

                        //           productdetails = null;
                    }
                    //  responseUserId = mainObject.getString("UserId");
                } else
                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(ProductListActivity.this, "1 ProductListActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            swipeRefreshLayout.setRefreshing(false);
            if (tempstatus == 200) {

                imageItemAdapter = new ProductDetailsListAdapter(ProductListActivity.this,
                        R.layout.product_details_list, ProductDetailsList);
                productlist.setAdapter(imageItemAdapter);
                imageItemAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.productlistactivity, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!newText.equals("")) {
                    doubleBackToExitPressedOnce = false;
                    imageItemAdapter.filter(newText);
                }
                return false;


            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();//16908332
        if (id == android.R.id.home) {

            onBackPressed();

        }

        if (id == R.id.action_vegitables) {
            catId = 1;
            catName = "Vegetables";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_fruits) {
            catId = 2;
            catName = "Fruits";
            filter="yes";

            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_cloths) {
            catId = 3;
            catName = "Cloths";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_grocery) {
            catId = 4;
            catName = "Grocery";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_personal_care) {
            catId = 5;
            catName = "Personal Care";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_dairy_beverages) {
            catId = 6;
            catName = "Dairy & Beverages";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_household_needs) {
            catId = 7;
            catName = "Household Needs";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_baby_kids) {
            catId = 8;
            catName = "Baby & Kids";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_packaged_food) {
            catId = 10;
            catName = "Packaged Food";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_others) {
            catId = 11;
            catName = "Others";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            if (filter.equals("yes"))
            {
                if (isNetworkAvailable(ProductListActivity.this)) {
                    new GetAllProducts().execute();
                    progressDialog(progress, "Loading", "Please wait...");
                    filter="no";
                } else {
                    Toast.makeText(ProductListActivity.this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                super.onBackPressed();
            }
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        searchView.setIconified(true);

        searchView.onActionViewCollapsed();
        searchView.setQuery("", false);
        searchView.clearFocus();
        imageItemAdapter.filter("");

    }

    public void RemoveProductMerchant(final View v) {

        //   Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_LONG).show();
        itemtoedit = (ProductDetails) v.getTag();

        ProductId = itemtoedit.get_productId();
        new RemoveProduct().execute();
    }


    public class RemoveProduct extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                String url = baseUrl + "RemoveProductMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
                jsonObject.accumulate("ProductId", ProductId);

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
                appendLog(ProductListActivity.this, "2 ProductListActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {
                Toast.makeText(getApplicationContext(), "Product removed", Toast.LENGTH_LONG).show();
                new GetAllProducts().execute();
            } else {

                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
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

    public class JSONAsyncTaskToGetProductsListByCategory extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                // code to consume wcf service which sends the details to the server
                String url = baseUrl + "searchInProListInMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
                jsonObject.accumulate("CategoryId", catId);

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
                    ProductDetailsList.clear();
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONArray mainObject = new JSONArray(data);
                    JSONObject othposjsonobj = mainObject.getJSONObject(0);

                    stat = othposjsonobj.getString("Status");
                    JSONArray cityArray = othposjsonobj.getJSONArray("data");
                    for (int n = 0; n < cityArray.length(); n++) {
                        JSONObject object = cityArray.getJSONObject(n);

                        JSONArray productUnitArr = object.getJSONArray("UnitArr");

                        ProductDetails productdetails = new ProductDetails();
                        productdetails.set_productId(object.getInt("ProductId"));
                        productdetails.set_productName(object.getString("ProductName"));
                        productdetails.set_productImage(object.getString("ProductImage"));
                        productdetails.set_manufacturer(object.getString("Manufacturer"));
                        productdetails.set_categoryName(object.getString("CategoryName"));

                        int abc = productUnitArr.length();
                        productdetails.setUnit_count(abc);

                        if (abc == 1) {
                            JSONObject uObject = productUnitArr.getJSONObject(0);
                            productdetails.set_unit(uObject.getString("Unit"));
                            productdetails.set_price(uObject.getDouble("Price"));
                          //  productdetails.set_mrp(uObject.getDouble("MRP"));

                        }

                        ProductDetailsList.add(productdetails);
                    }
                } else {
                    // result = "Did not work!";
                }


            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(ProductListActivity.this, "3 ProductListActivity " + e.toString() + date);

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (status == 200) {

                if (stat.equals("fail")) {
                    Toast.makeText(getApplicationContext(), "No products found", Toast.LENGTH_LONG).show();

                } else {
                    imageItemAdapter = new ProductDetailsListAdapter(ProductListActivity.this,
                            R.layout.product_details_list, ProductDetailsList);
                    productlist.setAdapter(imageItemAdapter);
                    imageItemAdapter.notifyDataSetChanged();
                }
            }
        }
    }

}
