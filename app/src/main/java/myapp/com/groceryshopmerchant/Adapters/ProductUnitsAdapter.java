package myapp.com.groceryshopmerchant.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import myapp.com.groceryshopmerchant.DBHandlers.UnitAndPrice;
import myapp.com.groceryshopmerchant.R;


/**
 * Created by apple on 8/22/17.
 */

public class ProductUnitsAdapter extends ArrayAdapter<UnitAndPrice> {

    protected static final String LOG_TAG = DemoAddProductListAdapter.class.getSimpleName();
    private ArrayList<UnitAndPrice> arraylist;
    private List<UnitAndPrice> items;
    private int layoutResourceId;
    private Context context;

    public ProductUnitsAdapter(Context context, int layoutResourceId, List<UnitAndPrice> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
        this.arraylist = new ArrayList<UnitAndPrice>();
        this.arraylist.addAll(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AtomPaymentHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        Typeface fontAwesomeFont = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");

        holder = new AtomPaymentHolder();
        holder.atomPayment = items.get(position);

        //  holder.BtnEdtProduct = (Button) row.findViewById(R.id.btn_edit);
        //    holder.BtnEdtProduct.setTag(holder.atomPayment);

        holder.TvEditProduct = (TextView) row.findViewById(R.id.tv_edit_product);
        holder.TvEditProduct.setTag(holder.atomPayment);
        holder.TvEditProduct.setTypeface(fontAwesomeFont);

        holder.TvDeleteProduct = (TextView) row.findViewById(R.id.tv_delete_product);
        holder.TvDeleteProduct.setTag(holder.atomPayment);
        holder.TvDeleteProduct.setTypeface(fontAwesomeFont);

        holder.TvUnit = (TextView) row.findViewById(R.id.tvv_unit);
        setProductUnitTextChangeListener(holder);

        holder.TvPrice = (TextView) row.findViewById(R.id.tvv_price);
        setProductPriceTextListeners(holder);

        holder.TvMRP = (TextView) row.findViewById(R.id.tvv_mrp);
        setProductMRPTextListeners(holder);

        row.setTag(holder);

        setupItem(holder);
        return row;
    }

    private void setupItem(AtomPaymentHolder holder) {

        holder.TvUnit.setText(holder.atomPayment.get_unit());
        holder.TvPrice.setText("Price : " + String.valueOf(holder.atomPayment.get_price()));
        holder.TvMRP.setText("MRP : " + String.valueOf(holder.atomPayment.get_mrp()));
    }

    public static class AtomPaymentHolder {
        UnitAndPrice atomPayment;
        TextView TvUnit;
        TextView TvPrice;
        TextView TvMRP;
        TextView TvEditProduct;
        TextView TvDeleteProduct;
    }

    private void setProductUnitTextChangeListener(final ProductUnitsAdapter.AtomPaymentHolder holder) {
        holder.TvUnit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                holder.atomPayment.set_unit(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void setProductPriceTextListeners(final ProductUnitsAdapter.AtomPaymentHolder holder) {
        holder.TvPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    holder.atomPayment.set_price(Double.parseDouble(s.toString()));
                } catch (NumberFormatException e) {
//                    Log.e(LOG_TAG, "error reading double value: " + s.toString());
//                    DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
//                    String date = dff.format(Calendar.getInstance().getTime());
//                    Log.d("InputStream", e.getLocalizedMessage());
//                    e.printStackTrace();
//                    appendLog(context, "1 ProductUnitsAdapter " + e.toString() + date);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //  holder.TvPrice.setCursorVisible(true);
                // holder.TvPrice.setC
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setProductMRPTextListeners(final ProductUnitsAdapter.AtomPaymentHolder holder) {
        holder.TvMRP.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    holder.atomPayment.set_mrp(Double.parseDouble(s.toString()));
                } catch (NumberFormatException e) {
//                    Log.e(LOG_TAG, "error reading double value: " + s.toString());
//                    Log.e(LOG_TAG, "error reading double value: " + s.toString());
//                    DateFormat dff = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
//                    String date = dff.format(Calendar.getInstance().getTime());
//                    Log.d("InputStream", e.getLocalizedMessage());
//                    e.printStackTrace();
//                    appendLog(context, "2 ProductUnitsAdapter " + e.toString() + date);
                }
            }
//java.lang.NumberFormatException: For input string: "MRP : 10.0"
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
