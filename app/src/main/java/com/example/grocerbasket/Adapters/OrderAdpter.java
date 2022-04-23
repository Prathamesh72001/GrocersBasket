package com.example.grocerbasket.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.grocerbasket.Constructors.OrderHelperClass;
import com.example.grocerbasket.Constructors.SellerHelperClass;
import com.example.grocerbasket.OrderDetails;
import com.example.grocerbasket.R;
import com.example.grocerbasket.Session.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderAdpter extends RecyclerView.Adapter<OrderAdpter.OrderAdpterViewHolder> {

    Context context;
    public ArrayList<OrderHelperClass> orderHelperClasses;

    public OrderAdpter(Context context, ArrayList<OrderHelperClass> orderHelperClasses) {
        this.context = context;
        this.orderHelperClasses = orderHelperClasses;
    }

    @NonNull
    @Override
    public OrderAdpterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout_admin, parent, false);
        OrderAdpter.OrderAdpterViewHolder orderAdapterViewHolder = new OrderAdpter.OrderAdpterViewHolder(view);

        return orderAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdpterViewHolder holder, int position) {
        //Get Data
        OrderHelperClass orderHelperClass = orderHelperClasses.get(position);

        String orderid = orderHelperClass.getOrderid();
        String ordertime = orderHelperClass.getOrdertime();
        String orderstatus = orderHelperClass.getOrderstatus();
        String ordercost = orderHelperClass.getOrdercost();
        String orderby = orderHelperClass.getOrderby();
        String orderto = orderHelperClass.getOrderto();
        String payOp = orderHelperClass.getPayOption();
        String delOp = orderHelperClass.getDeliveryOption();

        //Set Data
        holder.orderid.setText(orderid);
        holder.orderprice.setText(ordercost);
        holder.delop.setText(delOp);

        if (payOp.equals("Cash On Delivery")) {
            holder.payop.setText("COD");
            holder.payop.setTextColor(Color.parseColor("#ec1c24"));
        } else {
            holder.payop.setText("Paid");
            holder.payop.setTextColor(Color.parseColor("#04942d"));
        }

        if (orderstatus.equalsIgnoreCase("In Progress")) {
            holder.orderstatus.setText(orderstatus);
            holder.orderstatus.setTextColor(Color.parseColor("#e3781a"));
        } else if (orderstatus.equalsIgnoreCase("Completed")) {
            holder.orderstatus.setText(orderstatus);
            holder.orderstatus.setTextColor(Color.parseColor("#04942d"));
        } else {
            holder.orderstatus.setText(orderstatus);
            holder.orderstatus.setTextColor(Color.parseColor("#ec1c24"));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(ordertime));
        String dateForm = DateFormat.format("dd/MM/yyyy", calendar).toString();
        holder.ordertime.setText(dateForm);

        FirebaseStorage.getInstance().getReference().child("User Profiles/").child(Objects.requireNonNull(orderby + "/" + "profile.jpg")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Picasso.get().load(uri).centerCrop().resize(holder.userImage.getMeasuredWidth(), holder.userImage.getMeasuredHeight()).placeholder(R.drawable.default_image).into(holder.userImage);
                } catch (Exception e) {
                    holder.userImage.setImageResource(R.drawable.default_image);
                }
            }
        });

        FirebaseDatabase.getInstance().getReference("Sellers").orderByChild("phoneno").equalTo(orderto).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        SellerHelperClass sellerHelperClass = ds.getValue(SellerHelperClass.class);
                        String shopname = sellerHelperClass.getShopname();
                        holder.orderto.setText("From " + shopname);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Orders").orderByChild("orderby").equalTo(orderby).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Session
                        SessionManager sessionManager = new SessionManager(context, SessionManager.SESSION_FORWHO);
                        HashMap<String, String> forWhoDetails = sessionManager.getForWhomDetailFromSession();
                        String forWhom = forWhoDetails.get(SessionManager.KEY_FORWHO);

                        if ("forSeller".equals(forWhom)) {
                            if (snapshot.exists()) {

                                Integer progress = snapshot.child(orderby).child("progress").getValue(Integer.class);

                                if (progress == 0) {
                                    FirebaseDatabase.getInstance().getReference("Orders").child(orderby).child("progress").setValue(50);
                                    prepareNotification(orderid,orderby,orderto);
                                }
                            }

                            Intent intent = new Intent(context, OrderDetails.class);
                            intent.putExtra("orderby", orderby);
                            intent.putExtra("orderid", orderid);
                            context.startActivity(intent);
                        } else if ("forAdmin".equals(forWhom)) {
                            Intent intent = new Intent(context, OrderDetails.class);
                            intent.putExtra("orderby", orderby);
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderHelperClasses.size();
    }

    public static class OrderAdpterViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView orderto, orderid, orderprice, orderstatus, ordertime, payop, delop;


        public OrderAdpterViewHolder(@NonNull View itemView) {
            super(itemView);
            //hooks
            userImage = itemView.findViewById(R.id.userImage);
            orderto = itemView.findViewById(R.id.OrderTo);
            orderid = itemView.findViewById(R.id.OrderId);
            orderprice = itemView.findViewById(R.id.productPrice);
            orderstatus = itemView.findViewById(R.id.OrderStatus);
            ordertime = itemView.findViewById(R.id.OrderTime);
            payop = itemView.findViewById(R.id.PayOption);
            delop = itemView.findViewById(R.id.DelOption);
        }
    }

    void prepareNotification(String orderId,String orderby,String orderto){
        String TOPIC="/topics/Push_Notification";
        String TITLE="Order "+orderId;
        String MESSAGE="Your Order is in Progress";
        String TYPE="OrderStatus";

        JSONObject jsonObject=new JSONObject();
        JSONObject jsonObjectBody=new JSONObject();
        try{
            jsonObjectBody.put("type",TYPE);
            jsonObjectBody.put("order", orderId);
            jsonObjectBody.put("orderby", orderby);
            jsonObjectBody.put("orderto", orderto);
            jsonObjectBody.put("title", TITLE);
            jsonObjectBody.put("message", MESSAGE);

            jsonObject.put("to",TOPIC);
            jsonObject.put("data",jsonObjectBody);

        }catch (Exception e){
            Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_LONG).show();
        }

        sendNotification(jsonObject);
    }

    private void sendNotification(JSONObject jsonObject) {
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","key=AAAA8uRayzA:APA91bEykbsck74MlrrSTo07wd84IXg2kHSFua6IAYrZbCK9LKgjDbaRse9O36zrReUVy6qAGv9W-RxmS8N9-xl9lY75c9UYI1sh8zih6Clq5Jdj9rjBGNFpcUYzOKN3IKEVq2nKVHbP");

                return headers;
            }
        };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
}
