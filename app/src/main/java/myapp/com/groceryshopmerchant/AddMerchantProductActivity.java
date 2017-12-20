package myapp.com.groceryshopmerchant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
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

import myapp.com.groceryshopmerchant.Adapters.MoreUnitPriceMrpAdapter;
import myapp.com.groceryshopmerchant.DBHandlers.DatabaseHandler;
import myapp.com.groceryshopmerchant.DBHandlers.Merchant;
import myapp.com.groceryshopmerchant.DBHandlers.UnitAndPrice;

import static myapp.com.groceryshopmerchant.Constants.Constants.appendLog;
import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;
import static myapp.com.groceryshopmerchant.Constants.Constants.isNetworkAvailable;
import static myapp.com.groceryshopmerchant.Constants.Constants.progressDialog;

public class AddMerchantProductActivity extends AppCompatActivity {
    AutoCompleteTextView AutoProductName;
    Spinner SpinnerUnitType, AutoProductCategory;
    Button BtnAddMoreFields, BtnAddProduct;
    public List<String> addedProductsCategory = new ArrayList<String>();
    public List<String> addedProductsNames = new ArrayList<String>();
    String CategoryName, ProductName, UnitType;
    ProgressDialog progress;
    int status;
    int tempstatus;
    InputStream inputStream = null;
    String result = null;
    String responceStatus;
    private EditText EdtPrice, EdtMrp;
    private EditText EdtUnit;
    private ArrayList<EditText> ListEdtUnit = new ArrayList<EditText>();
    private ArrayList<EditText> ListEdtPrice = new ArrayList<EditText>();
    public static ArrayList<UnitAndPrice> productList;
    String[] country = {"Kg", "gm", "liter", "piece", "Other",};
    List<Merchant> merchant;
    DatabaseHandler db = new DatabaseHandler(this);
    int count;
    static int MerchantId;
    String ShopName, Manufacturer, EmailId, Unit;
    float Price, MRP;
    UnitAndPrice newproduct;
    public static ListView lview;
    public static MoreUnitPriceMrpAdapter adapter;
    EditText EdtManufacturer;

    int ProductId;
    SharedPreferences sharedpreferences;
    public static final String mypreferenceaddproductimage = "mypreferenceaddproductimage";
    public static final String AddProductImage_ProductCatKey = "AddProductImage_ProductCatKey";
    public static final String AddProductImage_ProductNameKey = "AddProductImage_ProductNameKey";
    public static final String AddProductImage_ProductIdKey = "AddProductImage_ProductIdKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        getSupportActionBar().setHomeButtonEnabled(true);


        //Declerations
        progress = new ProgressDialog(AddMerchantProductActivity.this);
        AutoProductCategory = (Spinner) findViewById(R.id.autocomplete_product_category);
        AutoProductName = (AutoCompleteTextView) findViewById(R.id.autocomplete_product_name);
        EdtManufacturer = (EditText) findViewById(R.id.edt_manufacturer);
        BtnAddMoreFields = (Button) findViewById(R.id.btn_add_more_fields);

        BtnAddProduct = (Button) findViewById(R.id.btn_add_product);
        EdtPrice = (EditText) findViewById(R.id.edt_price);
        EdtMrp = (EditText) findViewById(R.id.edt_mrp);
        EdtUnit = (EditText) findViewById(R.id.edt_unit);
        lview = (ListView) findViewById(R.id.listview);


        productList = new ArrayList<UnitAndPrice>();
        Integer count = productList.size();
        ViewGroup.LayoutParams params = lview.getLayoutParams();
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        params.height = (int) (count * 40 * scale + 0.5f);
        lview.setLayoutParams(params);
        adapter = new MoreUnitPriceMrpAdapter(this, productList);
        lview.setAdapter(adapter);
        // autoCompleteView.setText("");
        adapter.notifyDataSetChanged();


        //Spinner Decleration and data set
        SpinnerUnitType = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerUnitType.setAdapter(aa);

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

        //Buttons on clicks
        BtnAddMoreFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (EdtUnit.length() <= 0) {
                    EdtUnit.setError("Enter Unit");
                    EdtUnit.requestFocus();
                } else if (EdtPrice.length() <= 0) {
                    EdtPrice.setError("Enter Price");
                    EdtPrice.requestFocus();
                } else if (EdtMrp.length() <= 0) {
                    EdtMrp.setError("Enter MRP");
                    EdtMrp.requestFocus();
                } else {
                    Unit = EdtUnit.getText().toString();
                    Price = Float.parseFloat(EdtPrice.getText().toString());
                    MRP = Float.parseFloat(EdtMrp.getText().toString());
                    UnitType = SpinnerUnitType.getSelectedItem().toString();

                    newproduct = new UnitAndPrice();
                    newproduct.set_unit(Unit + UnitType);
                    newproduct.set_price(Price);
                    newproduct.set_mrp(MRP);


                    productList.add(newproduct);
                    Integer count = productList.size();
                    ViewGroup.LayoutParams params = lview.getLayoutParams();
                    final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                    params.height = (int) (count * 40 * scale + 0.5f);
                    lview.setLayoutParams(params);
                    //adapter = new MoreUnitPriceMrpAdapter(NewBillActivity.this, productList);
                    lview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    EdtUnit.setText("");
                    EdtPrice.setText("");
                    EdtMrp.setText("");
                }
            }
        });

        AutoProductCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                CategoryName = AutoProductCategory.getSelectedItem().toString();

//                addedProductsNames.clear();
//
//                if (isNetworkAvailable(AddMerchantProductActivity.this)) {
//                    new SendProductCategory().execute();
//                    progressDialog(progress, "Loading", "Please wait...");
//                } else {
//                    Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        BtnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  CategoryName = AutoProductCategory.getText().toString();
                ProductName = AutoProductName.getText().toString();
                Manufacturer = EdtManufacturer.getText().toString();

                if (CategoryName == null || CategoryName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter category name", Toast.LENGTH_LONG).show();

                } else if (ProductName == null || ProductName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter product name", Toast.LENGTH_LONG).show();

                } else if (Manufacturer == null || Manufacturer.equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter Manufacturer", Toast.LENGTH_LONG).show();

                } else if (adapter.getCount() == 0) {

                    Toast.makeText(AddMerchantProductActivity.this, "Add Unit And Prices", Toast.LENGTH_LONG).show();
                    TextView emptyText = (TextView) findViewById(android.R.id.empty);
                    lview.setEmptyView(emptyText);

                } else {

                    AddProductPopup();

                }

            }
        });

        if (isNetworkAvailable(AddMerchantProductActivity.this)) {
            new GetAllCategories().execute();
            progressDialog(progress, "Loading", "Please wait...");
        } else {
            Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();

        }

    }

    class GetAllCategories extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                //------------------>>
                HttpGet httpget = new HttpGet(baseUrl + "getAllCategories");
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpget);

                // StatusLine stat = response.getStatusLine();
                status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONArray mainObject = new JSONArray(data);
                    JSONObject othposjsonobj = mainObject.getJSONObject(0);
                    JSONArray cityArray = othposjsonobj.getJSONArray("data");
                    for (int n = 0; n < cityArray.length(); n++) {
                        JSONObject object = cityArray.getJSONObject(n);

                        addedProductsCategory.add(object.getString("CategoryName"));
                    }

                    return true;
                }

            } catch (IOException e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(AddMerchantProductActivity.this, "1 AddMerchantProductActivity " + e.toString() + date);
            } catch (JSONException e) {

                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(AddMerchantProductActivity.this, "2 AddMerchantProductActivity " + e.toString() + date);
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            if (progress.isShowing()) {
                progress.dismiss();
            }

            if (status == 200) {
                ArrayAdapter<String> ProductCategoryAdapter = new ArrayAdapter<String>(AddMerchantProductActivity.this, android.R.layout.simple_spinner_item, addedProductsCategory);
                ProductCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                AutoProductCategory.setAdapter(ProductCategoryAdapter);
                ProductCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            }
        }
    }


    public class SendProductCategory extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                String url = baseUrl + "getAllProducts";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("CategoryName", CategoryName);

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
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONArray mainObject = new JSONArray(data);
                    JSONObject othposjsonobj = mainObject.getJSONObject(0);
                    JSONArray cityArray = othposjsonobj.getJSONArray("data");
                    for (int n = 0; n < cityArray.length(); n++) {
                        JSONObject object = cityArray.getJSONObject(n);

                        addedProductsNames.add(object.getString("ProductName"));
                    }
                    //  responseUserId = mainObject.getString("UserId");
                } else
                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(AddMerchantProductActivity.this, "3 AddMerchantProductActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (tempstatus == 200) {
                //  if (responceStatus.equals("success")) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }

                ArrayAdapter<String> ProductNameAdapter = new ArrayAdapter<String>(AddMerchantProductActivity.this, android.R.layout.simple_spinner_item, addedProductsNames);
                ProductNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                AutoProductName.setAdapter(ProductNameAdapter);
                ProductNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            } else {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
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

    public class AddProductDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
//http://192.168.10.143/GroceryWebAPI/api/Home/AddProduct
            try {
                String url = baseUrl + "AddProduct";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("CategoryName", CategoryName);
                jsonObject.accumulate("ProductName", ProductName);
                jsonObject.accumulate("MerchantId", MerchantId);
                jsonObject.accumulate("Manufacturer", Manufacturer);

                if (productList.size() >= 0) {
                    JSONArray productArr = new JSONArray();
                    for (int i = 0; i < productList.size(); i++) {
                        JSONObject itemObj = new JSONObject();
                        UnitAndPrice item = productList.get(i);
                        itemObj.put("Unit", item.get_unit());
                        itemObj.put("Price", item.get_price());
                        itemObj.put("MRP", item.get_mrp());
                        productArr.put(itemObj);
                    }
//{"ProductImage":"sdfghn","ProductName":"apple","CategoryName":"fruits","MerchantId":12,"UnitList":[{"Unit":"5Kg","Price":6}]}
                    jsonObject.accumulate("UnitListPriceMrp", productArr);
                }
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
                    ProductId = mainObject.getInt("ProductId");
                    //ProductId=123;

                } else
                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(AddMerchantProductActivity.this, "4 AddMerchantProductActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }

                sharedpreferences = getApplicationContext().getSharedPreferences(mypreferenceaddproductimage,
                        Context.MODE_PRIVATE);


                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(AddProductImage_ProductCatKey, CategoryName);
                editor.putString(AddProductImage_ProductNameKey, ProductName);
                editor.putInt(AddProductImage_ProductIdKey, ProductId);

                editor.commit();


                Intent mainIntent = new Intent(AddMerchantProductActivity.this, AddMerchantProductImageActivity.class);
                startActivity(mainIntent);
                Toast.makeText(getApplicationContext(), "Product added successfully.", Toast.LENGTH_LONG).show();
                //    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            } else if (responceStatus.equals("Product already added")) {
                // Toast.makeText(getApplicationContext(), "Product already added", Toast.LENGTH_LONG).show();

                open();
            } else {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        MenuItem miSearch = menu.findItem(R.id.action_search);
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.home:
//                onBackPressed();
//                break;
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void open() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Product already exists Do you want to replace?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Toast.makeText(AddMerchantProductActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();


                        if (isNetworkAvailable(AddMerchantProductActivity.this)) {
                            new UpdateProductDetails().execute();
                            progressDialog(progress, "Loading", "Please wait...");
                        } else {
                            Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();
                        }


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

    public class UpdateProductDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
//http://192.168.10.143/GroceryWebAPI/api/Home/AddProduct
            try {
                //http://192.168.10.128/GroceryWebAPI/api/Home/ReplaceProduct
                String url = baseUrl + "ReplaceProduct";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("CategoryName", CategoryName);
                jsonObject.accumulate("ProductName", ProductName);
                jsonObject.accumulate("MerchantId", MerchantId);
                jsonObject.accumulate("ProductImage", "sdfghn");
                jsonObject.accumulate("Manufacturer", Manufacturer);

                if (productList.size() >= 0) {
                    JSONArray productArr = new JSONArray();
                    for (int i = 0; i < productList.size(); i++) {
                        JSONObject itemObj = new JSONObject();
                        UnitAndPrice item = productList.get(i);
                        itemObj.put("Unit", item.get_unit());
                        itemObj.put("Price", item.get_price());
                        itemObj.put("MRP", item.get_mrp());
                        productArr.put(itemObj);
                    }
//{"ProductImage":"sdfghn","ProductName":"apple","CategoryName":"fruits","MerchantId":12,"UnitList":[{"Unit":"5Kg","Price":6}]}
                    jsonObject.accumulate("UnitList", productArr);
                }
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
                    //    responseUserId = mainObject.getString("UserId");
                } else
                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(AddMerchantProductActivity.this, "5 AddMerchantProductActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            if (responceStatus.equals("success")) {

                Intent mainIntent = new Intent(AddMerchantProductActivity.this, AddMerchantProductImageActivity.class);
                startActivity(mainIntent);
                //   Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void AddProductPopup() {

        final Context context = this;
        // get popup view to edit price
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.add_product_popup, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView PCat = (TextView) promptsView.findViewById(R.id.tvpCat);
        final TextView PName = (TextView) promptsView.findViewById(R.id.tvpName);
        final TextView PManuf = (TextView) promptsView.findViewById(R.id.tvpManuf);
        final TextView PUnit = (TextView) promptsView.findViewById(R.id.tvpUnit);

        PCat.setText("Product Category : " + CategoryName);
        PName.setText("Product Name : " + ProductName);
        PManuf.setText("Product Manufacturer : " + Manufacturer);

        String temp = "";
        if (productList.size() >= 0) {

            for (int i = 0; i < productList.size(); i++) {
                UnitAndPrice item = productList.get(i);
                temp = temp + (" Unit : " + item.get_unit() + " Price : " + String.valueOf(item.get_price()) + " MRP : " + String.valueOf(item.get_mrp() + "\n"));
            }
        }

        PUnit.setText(temp);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (isNetworkAvailable(AddMerchantProductActivity.this)) {
                                    progressDialog(progress, "Loading", "Please wait...");
                                    new AddProductDetails().execute();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }


}
