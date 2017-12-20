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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import myapp.com.groceryshopmerchant.DBHandlers.ProductDetails;
import myapp.com.groceryshopmerchant.DisplayProductDetailsActivity;
import myapp.com.groceryshopmerchant.R;

import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;
import static myapp.com.groceryshopmerchant.ProductListActivity.swipeRefreshLayout;


/**
 * Created by pramod on 11/17/16..
 */
public class ProductDetailsListAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity myContext;
    private ArrayList<ProductDetails> datas;
    private ArrayList<ProductDetails> items;

    private LayoutInflater v;

    ProductDetails post;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String MerchantIdKey = "MerchantIdKey";
    public static final String ProductIdKey = "ProductIdKey";

    public ProductDetailsListAdapter(Context context, int textViewResourceId,
                                     ArrayList<ProductDetails> objects) {
        // TODO Auto-generated constructor stub
        myContext = (Activity) context;
        this.datas = objects;

        this.items = new ArrayList<ProductDetails>();
        this.items.addAll(datas);
        v = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public void onClick(View v) {
    }

    static class ViewHolder {
        ProductDetails atomPayment;
        TextView postTitleView;
        TextView txtCategory, txtproductname, txtprice, txtquantity, imgeditproduct, tvRemove;
        ImageView imageproduct;
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
        final ViewHolder holder;
        if (!swipeRefreshLayout.isRefreshing()) {
            holder = new ViewHolder();
            post = (ProductDetails) datas.get(position);
            holder.atomPayment = post;
            ProductDetails ei = (ProductDetails) post;
            vi = v.inflate(R.layout.product_details_list, null);
            Typeface fontAwesomeFont = Typeface.createFromAsset(myContext.getAssets(), "fontawesome-webfont.ttf");

            holder.imageproduct = (ImageView) vi.findViewById(R.id.img_productimage);
            holder.tvRemove = (TextView) vi.findViewById(R.id.tv_remove);
            holder.tvRemove.setTag(holder.atomPayment);

            holder.txtCategory = (TextView) vi.findViewById(R.id.tv_category);
            holder.txtproductname = (TextView) vi.findViewById(R.id.tv_product_name);
            holder.txtprice = (TextView) vi.findViewById(R.id.tv_price);
          //  holder.txtmrp = (TextView) vi.findViewById(R.id.tv_mrp);
            holder.txtquantity = (TextView) vi.findViewById(R.id.tv_quantity);
            holder.imgeditproduct = (TextView) vi.findViewById(R.id.tv_next);
            holder.imgeditproduct.setTypeface(fontAwesomeFont);


            vi.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    if (!swipeRefreshLayout.isRefreshing()) {
                        sharedpreferences = myContext.getSharedPreferences(mypreference,
                                Context.MODE_PRIVATE);

                        ProductDetails product = datas.get(position);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt(ProductIdKey, product.get_productId());
                        editor.commit();

                        Intent selectCategoryIntent = new Intent(myContext, DisplayProductDetailsActivity.class);
                        myContext.startActivity(selectCategoryIntent);
                    }


                }
            });

            Picasso.with(myContext).load(baseUrl+"GetProductImage?filename=" + post.get_productImage()).fit().into(holder.imageproduct);


            holder.txtCategory.setText(post.get_categoryName());
            // imageproduct.setImageResource(imgid[position]);
            holder.txtproductname.setText(post.get_productName());


            if (post.getUnit_count() == 1) {
                holder.txtprice.setText("Price : " + String.valueOf(post.get_price()));
           //     holder.txtmrp.setText("MRP : " + String.valueOf(post.get_mrp()));
                holder.txtquantity.setText(post.get_unit());
            } else {
                holder.txtprice.setText("");
            //    holder.txtmrp.setText("");
                holder.txtquantity.setText("");
            }
            vi.setTag(holder);
        }
        return vi;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        datas.clear();

        if (charText.length() == 0) {
            datas.addAll(items);
        } else {
            for (ProductDetails wp : items) {
                if (wp.get_productName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    datas.add(wp);
                }
            }
        }
        this.notifyDataSetChanged();
    }


}