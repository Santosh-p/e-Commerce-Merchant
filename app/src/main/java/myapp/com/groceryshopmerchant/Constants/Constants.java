package myapp.com.groceryshopmerchant.Constants;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by SSPL on 31-07-2017.
 */

public class Constants {
    public static int mid = 1;
  // public static String baseUrl ="http://202.88.154.118/GroceryWebAPI/api/Home/";
     public static String baseUrl ="http://202.88.154.118/GroceryWebAPINew/api/Home/";
 //  public static String baseUrl ="http://192.168.10.128/GroceryWebAPI/api/Home/";

     public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
     }
     public static void progressDialog(ProgressDialog progress, String title, String message) {
        progress.setTitle(title);
        progress.setMessage(message);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //  showAlertDialog("Oops!", "Network connection is poor!");
            }
        });
    }

    //Generate log file and print exception in it.
    public static void appendLog(Context context, String text) {
        String logFileName = "GroceryShopCustomerApp.txt";
        File logFile = new File(Environment.getExternalStorageDirectory()
                + "/" + logFileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
            //   sendEmail(context);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        showAlertDialogforException(context,"Oops","Something went wrong.");
    }

    public static void sendEmail(Context context) {

        String pathname = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";//"/ICareTracker/";
        String filename = "GroceryShopMerchantApp.txt";
        File file = new File(pathname, filename);

        Log.i("Send email", "");
        String[] TO = {""};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Grocery Shop Merchant Application Log File");
        //  emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send ERP Log File."));
            //  finish();
            //  Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void showAlertDialogforException(final Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(true)
                .setMessage(message)
                .setPositiveButton("Send Report", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        sendEmail(context);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
