package myapp.com.groceryshopmerchant.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import myapp.com.groceryshopmerchant.DBHandlers.CustomerOrderDetails;
import myapp.com.groceryshopmerchant.OrderDetailsActivity;
import myapp.com.groceryshopmerchant.R;

import static myapp.com.groceryshopmerchant.MainActivity.swipeRefreshLayout;

/**
 * Created by SSPL on 29-08-2017.
 */

public class MainActivityOrderListAdapter extends BaseAdapter implements View.OnClickListener {
    TextView TvCustomerName, TvDate, TvTime, Tvnextbtn, TvCancel, TvOrderId, TvTotalPrice, TvOrderStatus;
    Button BtnAcceptOrder;
    private Activity myContext;
    private ArrayList<CustomerOrderDetails> datas;
    private ArrayList<CustomerOrderDetails> items;

    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String ProductIdKey = "ProductIdKey";
    public static final String ProductOrderStatusKey = "ProductOrderStatusKey";
    public static final String ProductTotalPriceKey = "ProductTotalPriceKey";

    public static final String ProductOrderAddressIdKey = "ProductOrderAddressIdKey";
    public static final String ProductOrderTypeKey = "ProductOrderTypeKey";

    private LayoutInflater v;

    CustomerOrderDetails post;

    public MainActivityOrderListAdapter(Context context, int textViewResourceId,
                                        ArrayList<CustomerOrderDetails> objects) {
        // TODO Auto-generated constructor stub
        myContext = (Activity) context;
        this.datas = objects;
        this.items = new ArrayList<CustomerOrderDetails>();
        this.items.addAll(datas);
        v = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public void onClick(View v) {
    }

    static class ViewHolder {
        TextView postTitleView;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final MainActivityOrderListAdapter.ViewHolder holder;
        holder = new MainActivityOrderListAdapter.ViewHolder();
        post = (CustomerOrderDetails) datas.get(position);
        CustomerOrderDetails ei = (CustomerOrderDetails) post;
        vi = v.inflate(R.layout.customer_order__list, null);
        Typeface fontAwesomeFont = Typeface.createFromAsset(myContext.getAssets(), "fontawesome-webfont.ttf");
        // CustomerOrderDetails atomPayment = null;
        TvCustomerName = (TextView) vi.findViewById(R.id.tv_customername);
        TvDate = (TextView) vi.findViewById(R.id.tv_date);
        TvTime = (TextView) vi.findViewById(R.id.tv_time);
        TvOrderId = (TextView) vi.findViewById(R.id.tv_orderId);
        TvTotalPrice = (TextView) vi.findViewById(R.id.tv_total_price);
        TvOrderStatus = (TextView) vi.findViewById(R.id.tv_order_status);

        BtnAcceptOrder = (Button) vi.findViewById(R.id.btn_accept);
        BtnAcceptOrder.setTag(post);

        TvCancel = (TextView) vi.findViewById(R.id.tv_cancel);
        TvCancel.setTag(post);
        Tvnextbtn = (TextView) vi.findViewById(R.id.tvGoAhed1);
        Tvnextbtn.setTypeface(fontAwesomeFont);

        TvCustomerName.setText(post.get_customerName());
        TvDate.setText(post.get_date());
        TvTime.setText(post.get_time());
        TvOrderId.setText("Order Id - " + post.get_order_id());
        TvTotalPrice.setText("Total ₹ - " + post.get_total_price());
        TvOrderStatus.setText("Order Status - " + post.get_orderStatus());

        if (!post.get_orderStatus().equals("Placed")) {
            TvCancel.setVisibility(View.GONE);
            BtnAcceptOrder.setVisibility(View.GONE);

        }

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!swipeRefreshLayout.isRefreshing()) {
                    sharedpreferences = myContext.getSharedPreferences(mypreference,
                            Context.MODE_PRIVATE);

                    CustomerOrderDetails product = datas.get(position);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(ProductIdKey, product.get_order_id());
                    editor.putString(ProductOrderStatusKey, product.get_orderStatus());
                    editor.putString(ProductTotalPriceKey, product.get_total_price());
                    editor.putInt(ProductOrderAddressIdKey, product.get_address_id());
                    editor.putString(ProductOrderTypeKey, product.get_order_type());
                    editor.commit();

                    Intent selectCategoryIntent = new Intent(myContext, OrderDetailsActivity.class);
                    myContext.startActivity(selectCategoryIntent);
                }

            }
        });
        vi.setTag(holder);
        return vi;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        datas.clear();

        if (charText.length() == 0) {
            datas.addAll(items);
        } else {
            for (CustomerOrderDetails wp : items) {
                if (wp.get_order_id().toLowerCase(Locale.getDefault()).contains(charText) || wp.get_customerName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    datas.add(wp);
                }
            }
        }
        this.notifyDataSetChanged();
    }
}