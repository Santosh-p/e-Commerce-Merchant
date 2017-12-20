package myapp.com.groceryshopmerchant.Adapters;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import myapp.com.groceryshopmerchant.DBHandlers.ProductDetails;
import myapp.com.groceryshopmerchant.R;

import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;

public class DemoAddProductListAdapter extends ArrayAdapter<ProductDetails> {

    protected static final String LOG_TAG = DemoAddProductListAdapter.class.getSimpleName();
    private ArrayList<ProductDetails> arraylist;
    private List<ProductDetails> items;
    private int layoutResourceId;
    private Context context;
    double EdtPrice;
    String unit;
    int pid;
    ProductDetails atomPayment, atomPayment1;

    TextView TvProductName, TvManufacturer, TvProductCategory, TvUnit;
    TextView TvPrice, TvMRP, TvEdtPrice;
    Button AddProductButton;
    ImageView ImageViewProductImage;

    public DemoAddProductListAdapter(Context context, int layoutResourceId, List<ProductDetails> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
        this.arraylist = new ArrayList<ProductDetails>();
        this.arraylist.addAll(items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AtomPaymentHolder holder = null;
        Typeface fontAwesomeFont = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new AtomPaymentHolder();
        atomPayment = items.get(position);
        AddProductButton = (Button) row.findViewById(R.id.atomPay_removePay);

        AddProductButton.setTag(atomPayment);

        ImageViewProductImage = (ImageView) row.findViewById(R.id.imgv_prodect_image);
        // setProductImageChangeListener(holder);
        //  Picasso.with(context).load("http://202.88.154.118/GroceryWebAPI/api/Home/GetProductImage?filename=" + holder.atomPayment.get_productImage()).fit().into(holder.ImageViewProductImage);

        TvProductName = (TextView) row.findViewById(R.id.tvv_product_name);
        // setProductNameTextChangeListener(holder);

        TvManufacturer = (TextView) row.findViewById(R.id.tv_product_manufacturer);
        //  setProductManufacturerChangeListener(holder);

        TvProductCategory = (TextView) row.findViewById(R.id.tv_product_category);
        // setProductCategoryTextChangeListener(holder);

        TvUnit = (TextView) row.findViewById(R.id.tv_Unit);
        //  setProductUnitTextChangeListener(holder);

        TvPrice = (TextView) row.findViewById(R.id.tv_add_price);
        TvMRP = (TextView) row.findViewById(R.id.tv_add_mrp);
        TvEdtPrice = (TextView) row.findViewById(R.id.tv_edit_price);
        TvEdtPrice.setTypeface(fontAwesomeFont);

        TvEdtPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                atomPayment = items.get(position);
                unit = atomPayment.get_unit();
                pid = atomPayment.get_productId();

                //  atomPayment1 = arraylist.get(position);

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
                userInputPrice.setText(String.valueOf(atomPayment.get_price()));
                userInputPrice.setSelection(userInputPrice.getText().length());

                userInputMRP.setText(String.valueOf(atomPayment.get_mrp()));
              //  userInputMRP.setSelection(userInputMRP.getText().length());


                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        String pri = userInputPrice.getText().toString();
                                        String mrp = userInputMRP.getText().toString();
                                        if (pri.equals("") || pri == null || pri.isEmpty()||mrp.equals("") || mrp == null || mrp.isEmpty()) {

                                            // Toast.makeText(context, "not change", Toast.LENGTH_LONG).show();

                                        } else {
                                            for (ProductDetails wp : arraylist) {
                                                if (wp.get_productId() == pid) {
                                                    if (wp.get_unit().equals(unit)) {
                                                        wp.set_price(Double.parseDouble(userInputPrice.getText().toString()));
                                                        wp.set_mrp(Double.parseDouble(userInputMRP.getText().toString()));
                                                    }
                                                }
                                            }

                                            for (ProductDetails wp : items) {
                                                if (wp.get_productId() == pid) {
                                                    if (wp.get_unit().equals(unit)) {
                                                        wp.set_price(Double.parseDouble(userInputPrice.getText().toString()));
                                                        wp.set_mrp(Double.parseDouble(userInputMRP.getText().toString()));
                                                    }
                                                }
                                            }
                                        }
                                        refreshlayout();
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
        });


        // setProductPriceTextListeners(holder);

        row.setTag(holder);

        setupItem();
        return row;
    }

    private void setupItem() {

        TvProductName.setText(atomPayment.get_productName());
        TvManufacturer.setText(atomPayment.get_manufacturer());
        TvProductCategory.setText(atomPayment.get_categoryName());
        TvUnit.setText(atomPayment.get_unit());
        TvPrice.setText("Price : " + String.valueOf(atomPayment.get_price()));
        TvMRP.setText("MRP : " + String.valueOf(atomPayment.get_mrp()));
        Picasso.with(context).load(baseUrl+"GetProductImage?filename=" + atomPayment.get_productImage()).fit().into(ImageViewProductImage);
//http://202.88.154.118/GroceryWebAPI/api/Home/GetProductImage?filename=britanniacakefruityfun.jpg
    }

    public static class AtomPaymentHolder {

    }


//    private void setProductNameTextChangeListener(final AtomPaymentHolder holder) {
//        holder.TvProductName.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                holder.atomPayment.set_productName(s.toString());
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//    }
//
//    private void setProductManufacturerChangeListener(final AtomPaymentHolder holder) {
//        holder.TvManufacturer.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                holder.atomPayment.set_manufacturer(s.toString());
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//    }
//
//    private void setProductCategoryTextChangeListener(final AtomPaymentHolder holder) {
//        holder.TvProductCategory.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                holder.atomPayment.set_categoryName(s.toString());
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//    }
//
//    private void setProductUnitTextChangeListener(final AtomPaymentHolder holder) {
//        holder.TvUnit.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                holder.atomPayment.set_unit(s.toString());
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//    }
//
//    private void setProductPriceTextListeners(final AtomPaymentHolder holder) {
//        holder.TvPrice.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                try {
//                    holder.atomPayment.set_price(Double.parseDouble(s.toString()));
//                } catch (NumberFormatException e) {
//                    Log.e(LOG_TAG, "error reading double value: " + s.toString());
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                //  holder.TvPrice.setCursorVisible(true);
//                // holder.TvPrice.setC
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        items.clear();

        if (charText.length() == 0) {
            items.addAll(arraylist);
        } else {
            for (ProductDetails wp : arraylist) {
                if (wp.get_productName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    items.add(wp);
                }
            }
        }
        this.notifyDataSetChanged();
    }


    public void refreshlayout() {
        this.notifyDataSetChanged();
    }
//    public void EditPrice() {
//
//        //  itemtoeditprice = (UnitAndPrice) v.getTag();
//
//        Toast.makeText(context, "Edit Price", Toast.LENGTH_LONG).show();
//
//      //  final Context context = this;
//        // get popup view to edit price
//        // get prompts.xml view
//        LayoutInflater li = LayoutInflater.from(context);
//        View promptsView = li.inflate(R.layout.edit_price_popup, null);
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                context);
//
//        // set prompts.xml to alertdialog builder
//        alertDialogBuilder.setView(promptsView);
//
//        final EditText userInput = (EditText) promptsView
//                .findViewById(R.id.editTextDialogUserInput);
//
//        // set dialog message
//        alertDialogBuilder
//                .setCancelable(false)
//                .setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                // get user input and set it to result
//                                // edit text
//                                //    result.setText(userInput.getText());
//
//                            userInput.setText(String.valueOf(holder.atomPayment.get_price()));
//
//                                //  CategoryName = itemToRemove.get_categoryName();
//                                //  ProductName = itemToRemove.get_productName();
//
//                                EdtPrice = Double.parseDouble(userInput.getText().toString());
//
//
//                                //     new DisplayProductDetailsActivity.SendEditProductDetails().execute();
//                            }
//                        })
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//        // create alert dialog
//        AlertDialog alertDialog = alertDialogBuilder.create();
//
//        // show it
//        alertDialog.show();
//
//    }


}
