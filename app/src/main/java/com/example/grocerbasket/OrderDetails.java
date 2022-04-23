package com.example.grocerbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.grocerbasket.Adapters.OrderProductAdapter;
import com.example.grocerbasket.Constructors.CartProdHelperClass;
import com.example.grocerbasket.Constructors.OrderHelperClass;
import com.example.grocerbasket.Session.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderDetails extends AppCompatActivity {

    String orderby,orderid,orderto;
    RecyclerView orderRecycler;
    TextView cost,delCost,toPay,payOption,userLocation,txt2;
    ImageView img;
    RelativeLayout btn_Rl;
    ArrayList<CartProdHelperClass> productHelperClass1;
    RecyclerView.Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        orderby=getIntent().getStringExtra("orderby");
        orderto=getIntent().getStringExtra("orderto");
        orderid=getIntent().getStringExtra("orderid");
        orderRecycler=findViewById(R.id.OrderRecyclerView);
        cost=findViewById(R.id.totalcost);
        delCost=findViewById(R.id.delcost);
        toPay=findViewById(R.id.ToPay);
        payOption=findViewById(R.id.PayOption);
        userLocation=findViewById(R.id.UserLocation);
        btn_Rl=findViewById(R.id.btn_barRl);
        txt2=findViewById(R.id.txt2);
        img=findViewById(R.id.img);


        //getData setData
        productHelperClass1= new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Orders").orderByChild("orderby").equalTo(orderby).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    productHelperClass1.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        OrderHelperClass orderHelperClass = ds.getValue(OrderHelperClass.class);
                        String delOp=orderHelperClass.getDeliveryOption();
                        String payOp=orderHelperClass.getPayOption();
                        String totalCost=orderHelperClass.getOrdercost();
                        String orderTo=orderHelperClass.getOrderto();

                        toPay.setText("\u20b9 "+totalCost);

                        if(payOp.equals("Cash On Delivery"))
                        {
                            payOption.setText("COD");
                            payOption.setTextColor(Color.parseColor("#ec1c24"));
                        }
                        else{
                            payOption.setText("Paid");
                            payOption.setTextColor(Color.parseColor("#04942d"));
                        }


                        if(delOp.equalsIgnoreCase("Delivery")){
                            FirebaseDatabase.getInstance().getReference("Sellers").child(orderTo).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String delFee=snapshot.child("delfee").getValue(String.class);
                                        delCost.setText("\u20b9 "+delFee);
                                        int dF=Integer.parseInt(delFee);
                                        int tC=Integer.parseInt(totalCost);
                                        String orgCost=String.valueOf(tC-dF);
                                        cost.setText("\u20b9 "+orgCost);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else{
                            delCost.setText("\u20b9 0");
                            cost.setText("\u20b9 "+totalCost);
                        }
                    }

                    FirebaseDatabase.getInstance().getReference("Orders").child(orderby).child("Items").orderByChild("prodid").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                CartProdHelperClass productHelperClass = ds.getValue(CartProdHelperClass.class);
                                productHelperClass1.add(productHelperClass);
                            }
                            adapter = new OrderProductAdapter(OrderDetails.this, productHelperClass1);
                            orderRecycler.setAdapter(adapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneno").equalTo(orderby).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String address=snapshot.child(orderby).child("useradd").getValue(String.class);

                                userLocation.setText(address);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Session
        SessionManager sessionManager = new SessionManager(OrderDetails.this, SessionManager.SESSION_FORWHO);
        HashMap<String, String> forWhoDetails = sessionManager.getForWhomDetailFromSession();
        String forWhom = forWhoDetails.get(SessionManager.KEY_FORWHO);

        if("forSeller".equals(forWhom)) {

            FirebaseDatabase.getInstance().getReference("Orders").orderByChild("orderby").equalTo(orderby).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Integer progress=snapshot.child(orderby).child("progress").getValue(Integer.class);
                        if(progress==50){
                            btn_Rl.setBackgroundTintList(ContextCompat.getColorStateList(OrderDetails.this, R.color.dark_green));
                            txt2.setText("Order is Ready");
                            img.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
                        }
                        else{
                            btn_Rl.setBackgroundTintList(ContextCompat.getColorStateList(OrderDetails.this, R.color.orange));
                            txt2.setText("Track Order");
                            img.setImageResource(R.drawable.ic_baseline_my_location_24);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //Listener
            btn_Rl.setEnabled(true);
            btn_Rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if("Order is Ready".equals(txt2.getText().toString())) {
                        FirebaseDatabase.getInstance().getReference("Orders").orderByChild("orderby").equalTo(orderby).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    FirebaseDatabase.getInstance().getReference("Orders").child(orderby).child("progress").setValue(100);
                                    prepareNotification(orderid,orderby,orderto);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        btn_Rl.setBackgroundTintList(ContextCompat.getColorStateList(OrderDetails.this, R.color.orange));
                        txt2.setText("Track Order");
                        img.setImageResource(R.drawable.ic_baseline_my_location_24);
                    }

                }
            });
        }
        else if("forAdmin".equals(forWhom)){
            btn_Rl.setEnabled(false);
            btn_Rl.setBackgroundTintList(ContextCompat.getColorStateList(OrderDetails.this, R.color.light_grey));
            txt2.setTextColor(Color.BLACK);
            img.setImageTintList(ContextCompat.getColorStateList(OrderDetails.this, R.color.black));
        }
    }

    void prepareNotification(String orderId,String orderby,String orderto){
        String TOPIC="/topics/Push_Notification";
        String TITLE="Order "+orderId;
        String MESSAGE="Your Order is Ready";
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
            Toast.makeText(OrderDetails.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
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

        Volley.newRequestQueue(OrderDetails.this).add(jsonObjectRequest);
    }
}