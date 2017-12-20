package myapp.com.groceryshopmerchant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import myapp.com.groceryshopmerchant.DBHandlers.DatabaseHandler;
import myapp.com.groceryshopmerchant.DBHandlers.Merchant;

import static myapp.com.groceryshopmerchant.Constants.Constants.appendLog;
import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;
import static myapp.com.groceryshopmerchant.Constants.Constants.isNetworkAvailable;
import static myapp.com.groceryshopmerchant.Constants.Constants.progressDialog;

public class UploadShopDetailsActivity extends AppCompatActivity {
    Pattern PanCardPattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

    EditText EdtAdharNo, EdtShopNo, EdtPanNo;
    ImageView ImgAdharcard;
    ImageView ImgShopCertificate;
    ImageView ImgPanCard;
    static ImageView ImgShopImg;
    String AadharNo, ShopRegistrationNo, PanCardNo;
    ProgressDialog progress;
    int count;
    static int MerchantId;
    String ShopName, EmailId;
    List<Merchant> merchant;
    DatabaseHandler db = new DatabaseHandler(this);
    ProgressDialog dialog;
    InputStream inputStream = null;
    String result = null;
    String responceStatus, responseUserId;
    int tempstatus;
    Button Submit;
    static String Status, UploadedFileName;
    String picturePath;
    private static int RESULT_LOAD_IMAGE = 1;
    String KYCStatus = "fail", SettingStatus = "fail";

    String ImageName;
    static String filenamee;
    boolean AdharCardImageFlag = false;
    boolean CertificateImageFlag = false;
    boolean PanCardImageFlag = false;
    boolean ShopImageFlag = false;

    public static String CertificateImage_Url_Flag = "Off";
    public static String ShopImage_Url_Flag = "Off";
    public static String AdharImage_Url_Flag = "Off";
    public static String PancardImage_Url_Flag = "Off";

    public static String CertificateImage_SERVER_URL = "http://202.88.154.118/GroceryWebAPI/api/Home/UploadShopCertificateImage/";
    public static String ShopImage_SERVER_URL = "http://202.88.154.118/GroceryWebAPI/api/Home/UploadShopImage/";
    public static String AadharImage_SERVER_URL = "http://202.88.154.118/GroceryWebAPI/api/Home/UploadAadharImage/";
    public static String PanCardImage_SERVER_URL = "http://202.88.154.118/GroceryWebAPI/api/Home/UploadPanCardImage/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_shop_details);
        //Get Merchant details from local database
        merchant = db.getAllMerchants();
        count = 0;
        for (Merchant cn : merchant) {
            count++;
        }
        if ((count > 0)) {
            Merchant merchant = db.getMerchantDetails();
            MerchantId = merchant.get_user_id();//46
            ShopName = merchant.get_shop_name();
            EmailId = merchant.get_email_id();
        }
        progress = new ProgressDialog(UploadShopDetailsActivity.this);
        EdtAdharNo = (EditText) findViewById(R.id.edt_adharNo);
        EdtShopNo = (EditText) findViewById(R.id.edt_shop_registration_number);
        EdtPanNo = (EditText) findViewById(R.id.edt_pan_card_number);

        ImgAdharcard = (ImageView) findViewById(R.id.img_adharcard);
        ImgShopCertificate = (ImageView) findViewById(R.id.img_shopcertificate);
        ImgPanCard = (ImageView) findViewById(R.id.img_pancard);
        ImgShopImg = (ImageView) findViewById(R.id.img_shopimg);
        //  ImgShopImg.setImageResource(R.drawable.cast_abc_scrubber_control_off_mtrl_alpha);

        ImgAdharcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Adhar Card Image", Toast.LENGTH_LONG).show();

                AdharImage_Url_Flag = "On";
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        ImgShopCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CertificateImage_Url_Flag = "On";

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        ImgPanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PancardImage_Url_Flag = "On";
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        ImgShopImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopImage_Url_Flag = "On";
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        Submit = (Button) findViewById(R.id.btn_submit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AadharNo = EdtAdharNo.getText().toString();
                ShopRegistrationNo = EdtShopNo.getText().toString();
                PanCardNo = EdtPanNo.getText().toString();

                Matcher matcher = PanCardPattern.matcher(PanCardNo);
                // Check if pattern matches


                if (isNetworkAvailable(UploadShopDetailsActivity.this)) {
                    if (AadharNo == null || AadharNo.equals("") || ShopRegistrationNo == null || ShopRegistrationNo.equals("") || PanCardNo == null || PanCardNo.equals("")) {
                        Toast.makeText(getApplicationContext(), "Enter all valid details", Toast.LENGTH_LONG).show();

                    } else if (AadharNo.length() < 12) {
                        EdtAdharNo.setError("Invalid Aadhar Card No");
                        EdtAdharNo.requestFocus();
                    } else if (ShopRegistrationNo == null || ShopRegistrationNo.equals("")) {
                        EdtShopNo.setError("Enter Shop Registration No");
                        EdtShopNo.requestFocus();
                    } else if (!matcher.matches()) {
                        EdtPanNo.setError("Invalid Pan Card No");
                        EdtPanNo.requestFocus();

                    } else {
                        if (!AdharCardImageFlag) {
                            Toast.makeText(getApplicationContext(), "Upload Adhar Card Image", Toast.LENGTH_LONG).show();
                        } else if (!CertificateImageFlag) {
                            Toast.makeText(getApplicationContext(), "Upload Shop Certificate Image", Toast.LENGTH_LONG).show();
                        } else if (!PanCardImageFlag) {
                            Toast.makeText(getApplicationContext(), "Upload Pan Card Image", Toast.LENGTH_LONG).show();
                        } else if (!ShopImageFlag) {
                            Toast.makeText(getApplicationContext(), "Upload Shop Image", Toast.LENGTH_LONG).show();
                        } else {
                            new SendShopDetails().execute();
                            progressDialog(progress, "Loading", "Please wait...");
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);

            if (isNetworkAvailable(UploadShopDetailsActivity.this)) {
                if (picturePath != null) {
                    dialog = ProgressDialog.show(UploadShopDetailsActivity.this, "", "Uploading File...", true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                compressImage(picturePath);
                                picturePath = filenamee;
                                new SendImageFileToServer().execute();

                            } catch (OutOfMemoryError e) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(UploadShopDetailsActivity.this, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.dismiss();
                            }

                        }
                    }).start();
                } else {
                    Toast.makeText(UploadShopDetailsActivity.this, "Please choose a File First", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(UploadShopDetailsActivity.this, "Please check network connection", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }
    }

    public class SendShopDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
//http://202.88.154.118/GroceryWebAPI/api/Home/AddKYCDetailsOfMerchant
            try {
                String url = baseUrl + "AddKYCDetailsOfMerchant";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("MerchantId", MerchantId);
                jsonObject.accumulate("AadharNo", AadharNo);
                jsonObject.accumulate("ShopRegistrationNo", ShopRegistrationNo);
                jsonObject.accumulate("PanCardNo", PanCardNo);
//{"MerchantId":26,"AadharNo":"11233344","ShopRegistrationNo":"33444","PanCardNo":"33333"}

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
                appendLog(UploadShopDetailsActivity.this, "1 UploadShopDetailsActivity " + e.toString() + date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {
                progress.dismiss();
                ////////Code for store loc in database
                KYCStatus = "success";
                SettingStatus = "fail";

                db.UpdateRegistrationStatus(MerchantId, KYCStatus, SettingStatus);

                Intent i = new Intent(getApplicationContext(), ProvideSettingActivity.class);
                startActivity(i);


            } else {
                progress.dismiss();
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

    public static int uploadFilee(String selectedFilePath) {
        int serverResponseCode = 0;
        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);
        String[] parts = selectedFilePath.split("/");

        String[] partss = filenamee.split("/");
        filenamee = parts[parts.length - 1];

        try {///storage/emulated/0/MyFolder/Images/1505726332718.jpg
            FileInputStream fileInputStream = new FileInputStream(selectedFile);
            URL url = null;
            if (CertificateImage_Url_Flag.equals("On")) {
                url = new URL(CertificateImage_SERVER_URL + MerchantId);
            }
            if (ShopImage_Url_Flag.equals("On")) {
                url = new URL(ShopImage_SERVER_URL + MerchantId);
            }
            if (AdharImage_Url_Flag.equals("On")) {
                url = new URL(AadharImage_SERVER_URL + MerchantId);
            }
            if (PancardImage_Url_Flag.equals("On")) {
                url = new URL(PanCardImage_SERVER_URL + MerchantId);
            }
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);//Allow Inputs
            connection.setDoOutput(true);//Allow Outputs
            connection.setUseCaches(false);//Don't use a cached Copy
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("UploadedImage", selectedFilePath);
            //connection.setRequestProperty("TrackerId", responceVal1);
            //creating new dataoutputstream
            dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            // fileName = fileName.replaceAll(" ", "x");
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"UploadedImage\";filename=\""
                    + filenamee + "\"" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);
            //returns no. of bytes present in fileInputStream
            bytesAvailable = fileInputStream.available();
            //selecting the buffer size as minimum of available bytes or 1 MB
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            //setting the buffer as byte array of size of bufferSize
            buffer = new byte[bufferSize];
            //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            //loop repeats till bytesRead = -1, i.e., no bytes are left to read
            while (bytesRead > 0) {
                try {
                    dataOutputStream.write(buffer, 0, bufferSize);
                } catch (OutOfMemoryError e) {

//                    DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
//                    String date = dff.format(Calendar.getInstance().getTime());
//                    Log.d("InputStream", e.getLocalizedMessage());
//                    e.printStackTrace();
//                    appendLog(getc, "1 UploadShopDetailsActivity " + e.toString() + date);
                }
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            try {
                serverResponseCode = connection.getResponseCode();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

            String serverResponseMessage = connection.getResponseMessage();

            InputStream is = connection.getInputStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            is.close();
            try {
                JSONObject job = new JSONObject(responseStrBuilder.toString());
                Status = job.getString("Key");
                UploadedFileName = job.getString("Value");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Log.i("exception", "File Size Is Too Large");
                    // Toast.makeText(MyFirebaseMessagingService.this, "File Size Is Too Large", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //dialog.dismiss();
        return serverResponseCode;
        //}
    }

    public class SendImageFileToServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {


                uploadFilee(picturePath);
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (Status.equals("success")) {

                if (CertificateImage_Url_Flag.equals("On")) {
                    CertificateImageFlag = true;
                }
                if (ShopImage_Url_Flag.equals("On")) {
                    ShopImageFlag = true;
                }
                if (AdharImage_Url_Flag.equals("On")) {
                    AdharCardImageFlag = true;
                }
                if (PancardImage_Url_Flag.equals("On")) {
                    PanCardImageFlag = true;
                }


                dialog.dismiss();
                Toast.makeText(getApplicationContext(), UploadedFileName + " File uploaded successfully", Toast.LENGTH_LONG).show();

                if (CertificateImage_Url_Flag.equals("On")) {
                    Picasso.with(getApplicationContext()).load("http://202.88.154.118/GroceryWebAPI/api/Home/GetKYCImage?filename=" + UploadedFileName).fit().into(ImgShopCertificate);
                }
                if (ShopImage_Url_Flag.equals("On")) {//http://202.88.154.118/GroceryWebAPI/api/Home/GetKYCImage?filename=44KYCShopImage.jpg
                    Picasso.with(getApplicationContext()).load("http://202.88.154.118/GroceryWebAPI/api/Home/GetShopImage?filename=" + UploadedFileName).fit().into(ImgShopImg);
                }
                if (AdharImage_Url_Flag.equals("On")) {
                    Picasso.with(getApplicationContext()).load("http://202.88.154.118/GroceryWebAPI/api/Home/GetKYCImage?filename=" + UploadedFileName).fit().into(ImgAdharcard);
                }
                if (PancardImage_Url_Flag.equals("On")) {
                    Picasso.with(getApplicationContext()).load("http://202.88.154.118/GroceryWebAPI/api/Home/GetKYCImage?filename=" + UploadedFileName).fit().into(ImgPanCard);
                }

                CertificateImage_Url_Flag = "Off";
                ShopImage_Url_Flag = "Off";
                AdharImage_Url_Flag = "Off";
                PancardImage_Url_Flag = "Off";


            } else {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error while file upload ", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        filenamee = getFilename();
        try {
            out = new FileOutputStream(filenamee);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
///storage/emulated/0/MyFolder/Images/1505725675706.jpg
        return filenamee;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "GroceryShopMerchant/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}
