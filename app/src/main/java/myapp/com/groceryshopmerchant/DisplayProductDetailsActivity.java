package myapp.com.groceryshopmerchant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

import myapp.com.groceryshopmerchant.Adapters.ProductUnitsAdapter;
import myapp.com.groceryshopmerchant.DBHandlers.DatabaseHandler;
import myapp.com.groceryshopmerchant.DBHandlers.Merchant;
import myapp.com.groceryshopmerchant.DBHandlers.ProductDetails;
import myapp.com.groceryshopmerchant.DBHandlers.UnitAndPrice;

import static myapp.com.groceryshopmerchant.Constants.Constants.appendLog;
import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;
import static myapp.com.groceryshopmerchant.Constants.Constants.progressDialog;
import static myapp.com.groceryshopmerchant.ProductListActivity.ProductIdKey;
import static myapp.com.groceryshopmerchant.ProductListActivity.mypreference;

public class DisplayProductDetailsActivity extends AppCompatActivity {
    ImageView imgProduct;
    ListView listListOfWeight;
    TextView tvProductName, tvProductManufactutrar;
    UnitAndPrice itemtoedit;
    int ProductId;
    int status;
    int tempstatus;
    String responceStatus, responseUserId;
    InputStream inputStream = null;
    String result = null;
    ArrayList<ProductDetails> ProductDetailsList = new ArrayList<ProductDetails>();
    ArrayList<UnitAndPrice> unitsList = new ArrayList<UnitAndPrice>();
    public static ProductUnitsAdapter imageItemAdapter;
    String ProductName, ProductManufacturer, ImageName;
    SharedPreferences sharedpreferences;
    String EdtUnit;
    double EdtPrice, EdtMRP;
    boolean doubleBackToExitPressedOnce = true;
    int count, MerchantId;
    String ShopName, Manufacturer, EmailId, Unit;
    DatabaseHandler db = new DatabaseHandler(this);
    List<Merchant> merchant;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_product_details);
        progress = new ProgressDialog(DisplayProductDetailsActivity.this);
        getSupportActionBar().setHomeButtonEnabled(true);
        imgProduct = (ImageView) findViewById(R.id.img_productimage);
        listListOfWeight = (ListView) findViewById(R.id.list_product_weight);
        tvProductName = (TextView) findViewById(R.id.tv_product_name);
        tvProductManufactutrar = (TextView) findViewById(R.id.tv_product_manufacturar);

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
            ProductId = sharedpreferences.getInt(ProductIdKey, 0);
        }
        new GetSelectedProductDetails().execute();
        progressDialog(progress, "Loading", "Please wait...");
    }

    public class GetSelectedProductDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try { //http://192.168.10.128/GroceryWebAPI/api/Home/getProductDetail
                String url = baseUrl + "getProductDetail";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
                // jsonObject.accumulate("CategoryId", "2");
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
                HttpResponse response = httpclient.execute(httpPost);

                // StatusLine stat = response.getStatusLine();
                status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    unitsList.clear();
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONArray mainObject = new JSONArray(data);
                    JSONObject othposjsonobj = mainObject.getJSONObject(0);
                    JSONArray cityArray = othposjsonobj.getJSONArray("data");
                    for (int n = 0; n < cityArray.length(); n++) {
                        JSONObject object = cityArray.getJSONObject(n);


                        ProductName = object.getString("ProductName");
                        ProductManufacturer = object.getString("Manufacturer");
                        ImageName = object.getString("ProductImage");

                        JSONArray unitArray = object.getJSONArray("UnitArr");
                        //[{"Unit":"200gm","Price":30},{"Unit":"100gm","Price":17},{"Unit":"600gm","Price":85}]
                        for (int unit = 0; unit < unitArray.length(); unit++) {
                            JSONObject unitObject = unitArray.getJSONObject(unit);
                            UnitAndPrice projectUnitObj = new UnitAndPrice();
                            projectUnitObj.set_unit(unitObject.getString("Unit"));
                            projectUnitObj.set_price(unitObject.getDouble("Price"));
                            projectUnitObj.set_mrp(unitObject.getDouble("MRP"));
                            //projectUnitObj.setQuantity(unitObject.getInt("Qty"));
                            unitsList.add(projectUnitObj);
                            projectUnitObj = null;
                        }
                    }
                } else {
                    // result = "Did not work!";
                }
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(DisplayProductDetailsActivity.this, "1 DisplayProductDetailsActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            if (status == 200) {
                Picasso.with(DisplayProductDetailsActivity.this).load(baseUrl+"GetProductImage?filename=" + ImageName).fit().into(imgProduct);

                tvProductName.setText(ProductName);
                tvProductManufactutrar.setText(ProductManufacturer);

                imageItemAdapter = new ProductUnitsAdapter(DisplayProductDetailsActivity.this,
                        R.layout.main_view_list_row, unitsList);
                listListOfWeight.setAdapter(imageItemAdapter);
                imageItemAdapter.notifyDataSetChanged();
            }
        }
    }


    public void EditProduct(final View v) {
        itemtoedit = (UnitAndPrice) v.getTag();
        final Context context = this;
        // get popup view to edit price
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.edit_price_popup, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInputPrice = (EditText) promptsView
                .findViewById(R.id.popup_edt_price);
        final EditText userInputMRP = (EditText) promptsView
                .findViewById(R.id.popup_edt_mrp);

        userInputPrice.setText(String.valueOf(itemtoedit.get_price()));
        //   userInputPrice.setSelection(userInputPrice.getText().length());

        userInputMRP.setText(String.valueOf(itemtoedit.get_mrp()));
        //  userInputMRP.setSelection(userInputMRP.getText().length());

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String pri = userInputPrice.getText().toString();
                                String mrp = userInputMRP.getText().toString();
                                if (pri.equals("") || pri == null || pri.isEmpty() || mrp.equals("") || mrp == null || mrp.isEmpty()) {

                                    // Toast.makeText(context, "not change", Toast.LENGTH_LONG).show();

                                } else {
                                    itemtoedit = (UnitAndPrice) v.getTag();

                                    EdtUnit = itemtoedit.get_unit();
                                    EdtPrice = Double.parseDouble(userInputPrice.getText().toString());
                                    EdtMRP = Double.parseDouble(userInputMRP.getText().toString());


                                    new SendEditProductDetails().execute();
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

    public void RemoveProduct(final View v) {

        itemtoedit = (UnitAndPrice) v.getTag();

        EdtUnit = itemtoedit.get_unit();

        new DeleteProduct().execute();


    }

    public class DeleteProduct extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                String url = baseUrl + "RemoveProductUnitMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
                jsonObject.accumulate("ProductId", ProductId);
                jsonObject.accumulate("Unit", EdtUnit);


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
                appendLog(DisplayProductDetailsActivity.this, "2 DisplayProductDetailsActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {
                Toast.makeText(getApplicationContext(), "Product remove", Toast.LENGTH_LONG).show();
                new GetSelectedProductDetails1().execute();
            } else {

                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            }
        }
    }


    public class SendEditProductDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try { //   http://192.168.10.128/GroceryWebAPI/api/Home/EditProductMerchant
                String url = baseUrl + "EditProductMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject


                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
                jsonObject.accumulate("ProductId", ProductId);
                jsonObject.accumulate("Unit", EdtUnit);
                jsonObject.accumulate("Prize", EdtPrice);
                jsonObject.accumulate("MRP", EdtMRP);

                // 4. convert JSONObject to JSON to String
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
                appendLog(DisplayProductDetailsActivity.this, "3 DisplayProductDetailsActivity " + e.toString() + date);
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {

                // Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                new EditGetSelectedProductDetails().execute();

            } else {
                //         if (progress.isShowing()) {
                //         progress.dismiss();
                //  }
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

    public class EditGetSelectedProductDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try { //http://192.168.10.128/GroceryWebAPI/api/Home/getProductDetail
                String url = baseUrl + "getProductDetail";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
                // jsonObject.accumulate("CategoryId", "2");
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
                HttpResponse response = httpclient.execute(httpPost);

                // StatusLine stat = response.getStatusLine();
                status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    unitsList.clear();
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONArray mainObject = new JSONArray(data);
                    JSONObject othposjsonobj = mainObject.getJSONObject(0);
                    JSONArray cityArray = othposjsonobj.getJSONArray("data");
                    for (int n = 0; n < cityArray.length(); n++) {
                        JSONObject object = cityArray.getJSONObject(n);


                        ProductName = object.getString("ProductName");
                        ProductManufacturer = object.getString("Manufacturer");
//[{"Unit":"1kg","Price":10,"MRP":10},{"Unit":"5kg","Price":50,"MRP":50}]
                        JSONArray unitArray = object.getJSONArray("UnitArr");
                        //[{"Unit":"200gm","Price":30},{"Unit":"100gm","Price":17},{"Unit":"600gm","Price":85}]
                        for (int unit = 0; unit < unitArray.length(); unit++) {
                            JSONObject unitObject = unitArray.getJSONObject(unit);
                            UnitAndPrice projectUnitObj = new UnitAndPrice();
                            projectUnitObj.set_unit(unitObject.getString("Unit"));
                            projectUnitObj.set_price(unitObject.getDouble("Price"));
                            projectUnitObj.set_mrp(unitObject.getDouble("MRP"));
                            //projectUnitObj.setQuantity(unitObject.getInt("Qty"));
                            unitsList.add(projectUnitObj);
                            projectUnitObj = null;
                        }
                    }
                } else {
                    // result = "Did not work!";
                }
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(DisplayProductDetailsActivity.this, "4 DisplayProductDetailsActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (status == 200) {

                imageItemAdapter = new ProductUnitsAdapter(DisplayProductDetailsActivity.this,
                        R.layout.main_view_list_row, unitsList);
                listListOfWeight.setAdapter(imageItemAdapter);
                imageItemAdapter.notifyDataSetChanged();
            }
        }
    }


    public class GetSelectedProductDetails1 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try { //http://192.168.10.128/GroceryWebAPI/api/Home/getProductDetail
                String url = baseUrl + "getProductDetail";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
                // jsonObject.accumulate("CategoryId", "2");
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
                HttpResponse response = httpclient.execute(httpPost);

                // StatusLine stat = response.getStatusLine();
                status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    unitsList.clear();
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONArray mainObject = new JSONArray(data);
                    JSONObject othposjsonobj = mainObject.getJSONObject(0);
                    JSONArray cityArray = othposjsonobj.getJSONArray("data");
                    for (int n = 0; n < cityArray.length(); n++) {
                        JSONObject object = cityArray.getJSONObject(n);


                        ProductName = object.getString("ProductName");
                        ProductManufacturer = object.getString("Manufacturer");
                        ImageName = object.getString("ProductImage");

                        JSONArray unitArray = object.getJSONArray("UnitArr");
                        //[{"Unit":"200gm","Price":30},{"Unit":"100gm","Price":17},{"Unit":"600gm","Price":85}]
                        for (int unit = 0; unit < unitArray.length(); unit++) {
                            JSONObject unitObject = unitArray.getJSONObject(unit);
                            UnitAndPrice projectUnitObj = new UnitAndPrice();
                            projectUnitObj.set_unit(unitObject.getString("Unit"));
                            projectUnitObj.set_price(unitObject.getDouble("Price"));
                            projectUnitObj.set_mrp(unitObject.getDouble("MRP"));
                            //projectUnitObj.setQuantity(unitObject.getInt("Qty"));
                            unitsList.add(projectUnitObj);
                            projectUnitObj = null;
                        }
                    }
                } else {
                    // result = "Did not work!";
                }
            } catch (Exception e) {
                DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = dff.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(DisplayProductDetailsActivity.this, "5 DisplayProductDetailsActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (status == 200) {

                if (unitsList.isEmpty()) {
                    Intent mainIntent = new Intent(DisplayProductDetailsActivity.this, ProductListActivity.class);
                    startActivity(mainIntent);
                } else {
                    Picasso.with(DisplayProductDetailsActivity.this).load("http://202.88.154.118/GroceryWebAPI/api/Home/GetProductImage?filename=" + ImageName).fit().into(imgProduct);

                    tvProductName.setText(ProductName);
                    tvProductManufactutrar.setText(ProductManufacturer);

                    imageItemAdapter = new ProductUnitsAdapter(DisplayProductDetailsActivity.this,
                            R.layout.main_view_list_row, unitsList);
                    listListOfWeight.setAdapter(imageItemAdapter);
                    imageItemAdapter.notifyDataSetChanged();
                }
            }
        }
    }


}
