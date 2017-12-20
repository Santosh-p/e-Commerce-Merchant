package myapp.com.groceryshopmerchant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
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

import myapp.com.groceryshopmerchant.Adapters.DemoAddProductListAdapter;
import myapp.com.groceryshopmerchant.DBHandlers.DatabaseHandler;
import myapp.com.groceryshopmerchant.DBHandlers.Merchant;
import myapp.com.groceryshopmerchant.DBHandlers.ProductDetails;
import myapp.com.groceryshopmerchant.DBHandlers.UnitAndPrice;

import static myapp.com.groceryshopmerchant.Constants.Constants.appendLog;
import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;
import static myapp.com.groceryshopmerchant.Constants.Constants.isNetworkAvailable;
import static myapp.com.groceryshopmerchant.Constants.Constants.progressDialog;

public class AddMasterProductActivity extends AppCompatActivity {

    private DemoAddProductListAdapter adapter;ArrayList<ProductDetails> ProductDetailsList = new ArrayList<ProductDetails>();
    String CategoryName, ProductImage, ProductName, UnitType;
    ProgressDialog progress;
    int status;
    int tempstatus;
    InputStream inputStream = null;
    String result = null;
    String responceStatus;
    List<Merchant> merchant;
    DatabaseHandler db = new DatabaseHandler(this);
    int count, MerchantId;
    String ShopName, Manufacturer, EmailId, Unit;
    double Price,MRP;
    ProductDetails itemToRemove;
    public static ArrayList<UnitAndPrice> productList;
    ListView LV_ProductDetails;
    String[] country, population;
   // String catName;
    int catId;
    boolean doubleBackToExitPressedOnce = true;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    String stat;
    String filter="no";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_add_product);
        progress = new ProgressDialog(AddMasterProductActivity.this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_allitems);

        LV_ProductDetails = (ListView) findViewById(R.id.listview_product_details);

        // android.support.v7.app.ActionBar ab = getSupportActionBar();
        //  ab.setTitle("Add Products In Shop");


        LV_ProductDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(), "selected", Toast.LENGTH_LONG).show();

            }
        });


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

        //setupAddPaymentButton();
        setupListViewAdapter();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable(AddMasterProductActivity.this)) {
                    ProductDetailsList.clear();
                    swipeRefreshLayout.setRefreshing(true);
                    new GetAllProduct().execute();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    public void addItem(View v) {
        itemToRemove = (ProductDetails) v.getTag();
//		adapter.remove(itemToRemove);
        //    Toast.makeText(getApplicationContext(), "" + itemToRemove.get_productName() + itemToRemove.get_price(), Toast.LENGTH_LONG).show();
        CategoryName = itemToRemove.get_categoryName();
        ProductName = itemToRemove.get_productName();
        ProductImage = itemToRemove.get_productImage();
        Manufacturer = itemToRemove.get_manufacturer();
        Unit = itemToRemove.get_unit();
        Price = itemToRemove.get_price();
        MRP = itemToRemove.get_mrp();

        progressDialog(progress, "Loading", "Please wait...");

        new AddProductDetails().execute();


    }

    private void setupListViewAdapter() {

        if (isNetworkAvailable(AddMasterProductActivity.this)) {
            new GetAllProduct().execute();
            progressDialog(progress, "Loading", "Please wait...");
        } else {
            Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();

        }


    }

    public class AddProductDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                String url = baseUrl + "AddProductMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
                jsonObject.accumulate("ProductImage", ProductImage);
                jsonObject.accumulate("ProductName", ProductName);
                jsonObject.accumulate("Manufacturer", Manufacturer);
                jsonObject.accumulate("CategoryName", CategoryName);
                jsonObject.accumulate("Unit", Unit);
                jsonObject.accumulate("Prize", Price);
                jsonObject.accumulate("MRP", MRP);

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
                    //  responseUserId = mainObject.getString("UserId");
                    if (responceStatus.equals(null)) {
                        responceStatus = "Did not work!";
                    }
                } else

                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(AddMasterProductActivity.this, "1 AddMasterProductActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {


            if (responceStatus.equals("success")) {
                progress.dismiss();
                //  Intent mainIntent = new Intent(AddNewPerson.this, ProductListActivity.class);
                // startActivity(mainIntent);
                //   Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                adapter.remove(itemToRemove);

            } else if (responceStatus.equals("Product already added")) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Product already added", Toast.LENGTH_LONG).show();

                //   open();
            } else {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Error,Please try letter", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void open() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Product already exists Do you want to replace?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Toast.makeText(AddMerchantProductActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                        //	new UpdateProductDetails().execute();

                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_product, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_serch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Double back press exit

                if (!newText.equals("")) {
                    doubleBackToExitPressedOnce = false;
                    adapter.filter(newText);
                }
                return false;
            }

        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();


        if (id == R.id.action_vegitables) {
            catId = 1;
           // catName = "Vegetables";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_fruits) {
            catId = 2;
         //   catName = "Fruits";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_cloths) {
            catId = 3;
         //   catName = "Cloths";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_grocery) {
            catId = 4;
        //    catName = "Grocery";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_personal_care) {
            catId = 5;
          //  catName = "Personal Care";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_dairy_beverages) {
            catId = 6;
           // catName = "Dairy & Beverages";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_household_needs) {
            catId = 7;
          //  catName = "Household Needs";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_baby_kids) {
            catId = 8;
         //   catName = "Baby & Kids";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_packaged_food) {
            catId = 10;
         //   catName = "Packaged Food";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        } else if (id == R.id.action_others) {
            catId = 11;
           // catName = "Others";
            filter="yes";
            new JSONAsyncTaskToGetProductsListByCategory().execute();
        }

        return super.onOptionsItemSelected(item);
    }


    public class JSONAsyncTaskToGetProductsListByCategory extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                // code to consume wcf service which sends the details to the server
                String url = baseUrl + "searchInAddProductInMerchant";
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
                    JSONArray productArray = othposjsonobj.getJSONArray("data");

                    for (int n = 0; n < productArray.length(); n++) {
                        JSONObject object = productArray.getJSONObject(n);

                        ProductDetails proObj = new ProductDetails();
                        proObj.set_productId(object.getInt("ProductId"));
                        proObj.set_productName(object.getString("ProductName"));
                        proObj.set_manufacturer(object.getString("Manufacturer"));
                        proObj.set_productImage(object.getString("ProductImage"));
                        proObj.set_categoryName(object.getString("CategoryName"));
                        proObj.set_unit(object.getString("Unit"));
                        proObj.set_price(object.getDouble("Price"));
                        proObj.set_mrp(object.getDouble("Price"));

                        ProductDetailsList.add(proObj);
                    }
                } else {
                    // result = "Did not work!";
                }


            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(AddMasterProductActivity.this, "2 AddMasterProductActivity " + e.toString() + date);

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (status == 200) {


                if (stat.equals("fail")) {
                    Toast.makeText(getApplicationContext(), "No products found", Toast.LENGTH_LONG).show();

                } else {
                    adapter = new DemoAddProductListAdapter(AddMasterProductActivity.this, R.layout.atom_pay_list_item, ProductDetailsList);
                    LV_ProductDetails.setAdapter(adapter);
                }

            }
        }
    }


    public class GetAllProduct extends AsyncTask<String, Void, String> {

        // http://192.168.10.128/GroceryWebAPI/api/Home/getAllProductsWithDetails
        // MerchantId:1

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                //http://202.88.154.118/GroceryWebAPI/api/Home/
                // code to consume wcf service which sends the details to the server
                String url = baseUrl + "getAllProductsWithDetails";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
                // jsonObject.accumulate("CategoryId", catId);

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

                    JSONArray productArray = othposjsonobj.getJSONArray("data");

                    for (int n = 0; n < productArray.length(); n++) {
                        JSONObject object = productArray.getJSONObject(n);
//{"ProductId":4,"ProductName":"Britannia Cake - Fruity Fun","ProductImage":"britanniacakefruityfun.jpg","Manufacturer":"Britannia","CategoryName":"Bakery,Cakes & Dairy","Unit":"250gm","MRP":44}
                        ProductDetails proObj = new ProductDetails();
                        proObj.set_productId(object.getInt("ProductId"));
                        proObj.set_productName(object.getString("ProductName"));
                        proObj.set_manufacturer(object.getString("Manufacturer"));
                        proObj.set_productImage(object.getString("ProductImage"));
                        proObj.set_categoryName(object.getString("CategoryName"));
                        proObj.set_unit(object.getString("Unit"));
                        proObj.set_price(object.getDouble("MRP"));
                        proObj.set_mrp(object.getDouble("MRP"));

                        ProductDetailsList.add(proObj);

                    }
                } else {
                    // result = "Did not work!";
                }


            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(AddMasterProductActivity.this, "3 AddMasterProductActivity " + e.toString() + date);

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            swipeRefreshLayout.setRefreshing(false);
            if (progress.isShowing()) {
                progress.dismiss();
            }
            if (status == 200) {

                adapter = new DemoAddProductListAdapter(AddMasterProductActivity.this, R.layout.atom_pay_list_item, ProductDetailsList);
                LV_ProductDetails.setAdapter(adapter);

            } else {
                Toast.makeText(getApplicationContext(), "Error,Please try letter", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {


            if (filter.equals("yes"))
            {
                if (isNetworkAvailable(AddMasterProductActivity.this)) {
                    new GetAllProduct().execute();
                    progressDialog(progress, "Loading", "Please wait...");
                    filter="no";
                } else {
                    Toast.makeText(AddMasterProductActivity.this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
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
        adapter.filter("");


    }


}



