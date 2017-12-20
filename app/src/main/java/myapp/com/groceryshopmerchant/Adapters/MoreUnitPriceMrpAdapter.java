package myapp.com.groceryshopmerchant.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import myapp.com.groceryshopmerchant.R;
import myapp.com.groceryshopmerchant.DBHandlers.UnitAndPrice;

import static myapp.com.groceryshopmerchant.AddMerchantProductActivity.adapter;

/**
 * @author anfer
 */
public class MoreUnitPriceMrpAdapter extends BaseAdapter {
    public ArrayList<UnitAndPrice> productList;
    Activity activity;
    Context context;

    public MoreUnitPriceMrpAdapter(Activity activity, ArrayList<UnitAndPrice> productList) {
        super();
        this.activity = activity;
        this.productList = productList;
    }

    public MoreUnitPriceMrpAdapter(ArrayList<UnitAndPrice> productList) {

    }

    @Override
    public int getCount() {
        if (productList != null)
            return productList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView mUnit;
        TextView mPrice;
        TextView mMrp;
        TextView btnClear;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        Typeface fontAwesomeFont = Typeface.createFromAsset(activity.getAssets(), "fontawesome-webfont.ttf");

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.unit_price_listview_row, null);
            holder = new ViewHolder();
            holder.mUnit = (TextView) convertView.findViewById(R.id.unit);
            holder.mPrice = (TextView) convertView.findViewById(R.id.price);
            holder.mMrp = (TextView) convertView.findViewById(R.id.mrp);

            holder.btnClear = (TextView) convertView.findViewById(R.id.img_clear);
            holder.btnClear.setTypeface(fontAwesomeFont);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UnitAndPrice item = productList.get(position);

        holder.mUnit.setText(item.get_unit().toString());
        holder.mPrice.setText(String.valueOf(item.get_price()));
        holder.mMrp.setText(String.valueOf(item.get_mrp()));

        //holder.btnClear.setImageResource(R.drawable.ic_close_black_24dp);

        holder.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productList.remove(position);


                adapter.notifyDataSetChanged();
            }
        });
        return convertView;
    }

}