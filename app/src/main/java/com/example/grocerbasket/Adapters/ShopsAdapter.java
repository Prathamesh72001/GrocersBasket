package com.example.grocerbasket.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.grocerbasket.Constructors.CartProdHelperClass;
import com.example.grocerbasket.Constructors.OrderHelperClass;
import com.example.grocerbasket.Constructors.SellerHelperClass;
import com.example.grocerbasket.Dashboard;
import com.example.grocerbasket.FavouriteActivity;
import com.example.grocerbasket.R;
import com.example.grocerbasket.Session.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.ShopsAdapterViewHolder> {
    Context context;
    public ArrayList<SellerHelperClass> sellerHelperClasses;
    double lat1, lon1, lat2, lon2;
    String address, phoneno,email, finalcost, payOp, delOp;
    boolean isDelOptionSelected = false;
    boolean isPayOptionSelected = false;


    public ShopsAdapter(Context context, ArrayList<SellerHelperClass> sellerHelperClasses) {
        this.context = context;
        this.sellerHelperClasses = sellerHelperClasses;
    }

    @NonNull
    @Override
    public ShopsAdapter.ShopsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_layout, parent, false);

        return new ShopsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopsAdapter.ShopsAdapterViewHolder holder, int position) {
        //Get Data
        SellerHelperClass sellerHelperClass = sellerHelperClasses.get(position);

        String shopname = sellerHelperClass.getShopname();
        String shopownername = sellerHelperClass.getFirstname();
        String shopadd = sellerHelperClass.getShopadd();
        String shopemail = sellerHelperClass.getEmail();
        String shopphone = sellerHelperClass.getPhoneno();
        String shopdelfee = sellerHelperClass.getDelfee();

        FirebaseStorage.getInstance().getReference().child("Seller Shop Images/").child(Objects.requireNonNull(shopphone + "/" + "profile.jpg")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Picasso.get().load(uri).centerCrop().resize(holder.shopimage.getMeasuredWidth(), holder.shopimage.getMeasuredHeight()).placeholder(R.drawable.default_image).into(holder.shopimage);
                    ;
                } catch (Exception e) {
                    holder.shopimage.setImageResource(R.drawable.default_image);
                }
            }
        });

        holder.shopname.setText("from " + shopname);
        holder.shopadd.setText(shopadd);
        holder.Delfee.setText("\u20b9" + shopdelfee);
        holder.Phone.setText(shopphone);
        holder.Email.setText(shopemail);

        FirebaseDatabase.getInstance().getReference("Sellers").orderByChild("phoneno").equalTo(shopphone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean isOpen = snapshot.child(shopphone).child("isOpen").getValue(boolean.class);
                    if (isOpen) {
                        holder.Status.setText("OPEN");
                        holder.Status.setTextColor(Color.parseColor("#04942d"));
                    } else {
                        holder.Status.setText("CLOSE");
                        holder.Status.setTextColor(Color.parseColor("#ec1c24"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //User Lat & Long
        SessionManager sessionManager1 = new SessionManager(context, SessionManager.SESSION_ADDRESS);
        HashMap<String, String> addDetails = sessionManager1.getAddressDetailFromSession();
        address = addDetails.get(SessionManager.KEY_ADD);
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List addList = geocoder.getFromLocationName(address, 1);
            if (addList != null && addList.size() > 0) {
                Address Maddress = (Address) addList.get(0);
                lat1 = Maddress.getLatitude();
                lon1 = Maddress.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Seller Lat & Long
        Geocoder geocoder1 = new Geocoder(context, Locale.getDefault());
        try {
            List addList1 = geocoder1.getFromLocationName(shopadd, 1);
            if (addList1 != null && addList1.size() > 0) {
                Address Maddress = (Address) addList1.get(0);
                lat2 = Maddress.getLatitude();
                lon2 = Maddress.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Distance
        //Time
        float results[] = new float[10];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        DecimalFormat df = new DecimalFormat("#.#");
        String d = df.format(results[0] / 1000);
        holder.distance.setText(d + " km");

        String t = df.format((results[0] / 1000) * 1.96);
        holder.time.setText(t + " mins");

        //Listener
        holder.expandbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Sellers").orderByChild("phoneno").equalTo(shopphone).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            boolean isOpen = snapshot.child(shopphone).child("isOpen").getValue(boolean.class);
                            if (isOpen) {
                                detailsBottomSheet(shopphone,shopdelfee);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    private void detailsBottomSheet(String shopphone, String shopdelfee) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bs_select_delivery_options, null);
        bottomSheetDialog.setContentView(view);

        //hooks
        RelativeLayout takeAway = view.findViewById(R.id.takeAway);
        RelativeLayout delivery = view.findViewById(R.id.delivery);
        TextView deliveryTxt = view.findViewById(R.id.deliveryText);
        TextView takeawayTxt = view.findViewById(R.id.takeawayText);
        TextView payTxt = view.findViewById(R.id.pay_text);
        TextView cost = view.findViewById(R.id.cost);
        RelativeLayout relativeLayout = view.findViewById(R.id.relativeLayout4);
        RelativeLayout nextBtn = view.findViewById(R.id.Nextbtn);
        ImageView take = view.findViewById(R.id.bookimage1);
        ImageView del = view.findViewById(R.id.bookimage2);

        //showDialogue
        bottomSheetDialog.show();


        SessionManager UsersessionManager = new SessionManager(context, SessionManager.SESSION_USER);
        HashMap<String, String> userDetails = UsersessionManager.getUserDetailFromSession();
        phoneno = userDetails.get(SessionManager.KEY_PHONENO);
        email = userDetails.get(SessionManager.KEY_EMAIL);
        //finalcost
        FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").orderByChild("userphoneno").equalTo(phoneno).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String finalcost = snapshot.child(phoneno).child("cartfinalcost").getValue(String.class);
                    cost.setText(finalcost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //listener
        takeAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAway.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.orange));
                takeawayTxt.setTextColor(Color.parseColor("#FFFFFFFF"));
                take.setImageTintList(ContextCompat.getColorStateList(context, R.color.white));

                delivery.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_grey));
                deliveryTxt.setTextColor(Color.parseColor("#FF000000"));
                del.setImageTintList(ContextCompat.getColorStateList(context, R.color.black));

                isDelOptionSelected = true;

                delOp = takeawayTxt.getText().toString();

                FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").orderByChild("userphoneno").equalTo(phoneno).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            finalcost = snapshot.child(phoneno).child("cartfinalcost").getValue(String.class);
                            cost.setText(finalcost);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delivery.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.orange));
                deliveryTxt.setTextColor(Color.parseColor("#FFFFFFFF"));
                del.setImageTintList(ContextCompat.getColorStateList(context, R.color.white));

                takeAway.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_grey));
                takeawayTxt.setTextColor(Color.parseColor("#FF000000"));
                take.setImageTintList(ContextCompat.getColorStateList(context, R.color.black


                ));

                isDelOptionSelected = true;

                delOp = deliveryTxt.getText().toString();

                FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").orderByChild("userphoneno").equalTo(phoneno).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String c = snapshot.child(phoneno).child("cartfinalcost").getValue(String.class);
                            Integer cos = Integer.parseInt(c) + Integer.parseInt(shopdelfee);
                            finalcost = String.valueOf(cos);
                            cost.setText(finalcost);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.payment_option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.COD:

                            case R.id.Online:
                                payTxt.setText(item.getTitle().toString());
                                int amount = Math.round(Float.parseFloat(finalcost) * 100);
                                payOp = payTxt.getText().toString();
                                isPayOptionSelected = true;
                                Checkout checkout=new Checkout();
                                checkout.setKeyID("rzp_test_ngxCkqNNDrbuYw");
                                JSONObject jsonObject=new JSONObject();
                                try{
                                    jsonObject.put("amount",amount);
                                    jsonObject.put("name","Grocer'sBascket");
                                    jsonObject.put("description","Order Payment");
                                    jsonObject.put("prefill.contact",phoneno);
                                    jsonObject.put("prefill.email",email);
                                    checkout.open((Activity) context, jsonObject);
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                                break;

                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (isDelOptionSelected && isPayOptionSelected) {

                            bottomSheetDialog.dismiss();
                            submitOrder(shopphone, finalcost, delOp, payOp);

                    } else if (!isDelOptionSelected) {
                        Toast.makeText(context, "Please Select Delivery Option", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Please Select Payment Option", Toast.LENGTH_SHORT).show();
                    }

            }
        });

    }

    private void submitOrder(String shopphone, String finalcost, String delOp, String payOp) {
        String timeStamp = "" + System.currentTimeMillis();

        SessionManager UsersessionManager = new SessionManager(context, SessionManager.SESSION_USER);
        HashMap<String, String> userDetails = UsersessionManager.getUserDetailFromSession();
        phoneno = userDetails.get(SessionManager.KEY_PHONENO);


        FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").orderByChild("userphoneno").equalTo(phoneno).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    OrderHelperClass orderHelperClass = new OrderHelperClass(timeStamp, timeStamp, "In Progress", finalcost, phoneno, shopphone, payOp, delOp,0);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Orders");
                    ref.child(phoneno).setValue(orderHelperClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(phoneno).child("cartProdHelperClass").orderByChild("prodid").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            CartProdHelperClass cartProdHelperClass = ds.getValue(CartProdHelperClass.class);
                                            String prodid = cartProdHelperClass.getProdid();
                                            ref.child(phoneno).child("Items").child(prodid).setValue(cartProdHelperClass);
                                        }
                                        Toast.makeText(context, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                                        prepareNotification(timeStamp,phoneno,shopphone);
                                        FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(phoneno).removeValue();
                                        context.startActivity(new Intent(context, Dashboard.class));
                                        ((Activity) context).finishAffinity();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return sellerHelperClasses.size();
    }



    public static class ShopsAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView shopname, distance, time, shopadd, Status, Email, Phone, Delfee;
        ImageView expandbtn, shopimage;

        public ShopsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            //hooks
            shopimage = itemView.findViewById(R.id.shopImage);
            expandbtn = itemView.findViewById(R.id.expand);
            Delfee = itemView.findViewById(R.id.Delfee);
            Phone = itemView.findViewById(R.id.Phone);
            Email = itemView.findViewById(R.id.Email);
            Status = itemView.findViewById(R.id.Status);
            shopadd = itemView.findViewById(R.id.ShopAdd);
            time = itemView.findViewById(R.id.time);
            distance = itemView.findViewById(R.id.distance);
            shopname = itemView.findViewById(R.id.shopname);

        }
    }

    void prepareNotification(String orderId,String orderby,String orderto){
        String TOPIC="/topics/PUSH_NOTIFICATION";
        String TITLE="Order "+orderId;
        String MESSAGE="Your Order is Placed Successfully";
        String TYPE="NewOrder";

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

        sendNotification(jsonObject,orderId);
    }

    private void sendNotification(JSONObject jsonObject, String orderId) {
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> headers=new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","key=AAAA8uRayzA:APA91bEykbsck74MlrrSTo07wd84IXg2kHSFua6IAYrZbCK9LKgjDbaRse9O36zrReUVy6qAGv9W-RxmS8N9-xl9lY75c9UYI1sh8zih6Clq5Jdj9rjBGNFpcUYzOKN3IKEVq2nKVHbP");

                return headers;
            }
        };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
}
