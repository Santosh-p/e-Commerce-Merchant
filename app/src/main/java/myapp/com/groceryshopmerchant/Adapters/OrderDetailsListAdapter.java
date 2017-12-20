package myapp.com.groceryshopmerchant.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import myapp.com.groceryshopmerchant.DBHandlers.CustomerOrderDetails;
import myapp.com.groceryshopmerchant.R;

import static myapp.com.groceryshopmerchant.Constants.Constants.baseUrl;

/**
 * Created by SSPL on 29-08-2017.
 */

public class OrderDetailsListAdapter extends BaseAdapter implements View.OnClickListener {
    TextView txtCategory, txtproductname, txtprice, txtmrp, txtunit, txtquantity;
    ImageView imageproduct;
    Button BtnPacked;
    private Activity myContext;
    private ArrayList<CustomerOrderDetails> datas;

    private LayoutInflater v;

    CustomerOrderDetails post;

    public OrderDetailsListAdapter(Context context, int textViewResourceId,
                                   ArrayList<CustomerOrderDetails> objects) {
        // TODO Auto-generated constructor stub
        myContext = (Activity) context;
        this.datas = objects;
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
        vi = v.inflate(R.layout.order_details__list, null);
        Typeface fontAwesomeFont = Typeface.createFromAsset(myContext.getAssets(), "fontawesome-webfont.ttf");

        imageproduct = (ImageView) vi.findViewById(R.id.img_productimage);
        txtCategory = (TextView) vi.findViewById(R.id.tv_category);
        txtproductname = (TextView) vi.findViewById(R.id.tv_product_name);
        txtprice = (TextView) vi.findViewById(R.id.tv_price);
       // txtmrp = (TextView) vi.findViewById(R.id.tv_mrp);
        txtunit = (TextView) vi.findViewById(R.id.tv_unit);
        txtquantity = (TextView) vi.findViewById(R.id.tv_quantity);
        BtnPacked = (Button) vi.findViewById(R.id.btn_packed);
        BtnPacked.setTag(post);

        if (post.get_orderStatus().equals("Cancelled")) {
            BtnPacked.setClickable(false);
            BtnPacked.setEnabled(false);
        }
        if (post.get_orderStatus().equals("Dispatch")) {
            BtnPacked.setClickable(false);
            BtnPacked.setEnabled(false);
        }
        if (post.getPacked_flag().equals("packed")) {
            BtnPacked.setBackgroundResource(R.color.gereen);
            BtnPacked.setText("Packed");
        }
        if (post.get_orderStatus().equals("Delivered")) {
            BtnPacked.setClickable(false);
            BtnPacked.setEnabled(false);
        }

        Picasso.with(myContext).load(baseUrl+"GetProductImage?filename=" + post.get_productImage()).fit().into(imageproduct);

        txtCategory.setText(post.get_categoryName());
        // imageproduct.setImageResource(imgid[position]);
        txtproductname.setText(post.get_productName());
        txtprice.setText("Price " + String.valueOf(post.get_price()));
       // txtmrp.setText("MRP " + String.valueOf(post.get_mrp()));
        txtunit.setText("Unit " + post.get_unit());
        txtquantity.setText("Quantity " + String.valueOf(post.get_quantity()));
        vi.setTag(holder);
        return vi;
    }

}
