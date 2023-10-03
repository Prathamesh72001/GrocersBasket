package com.example.grocerbasket.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerbasket.Constructors.CartProdHelperClass;
import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Constructors.ReviewHelperClass;
import com.example.grocerbasket.Constructors.SellerHelperClass;
import com.example.grocerbasket.Constructors.UserHelperClass;
import com.example.grocerbasket.Product_Details;
import com.example.grocerbasket.R;
import com.example.grocerbasket.Session.SessionManager;
import com.example.grocerbasket.Welcome_To_Login_Signup;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter< ReviewAdapter. ReviewAdapterViewHolder>{

    Context context;
    public ArrayList<ReviewHelperClass> reviewHelperClasses;


    public ReviewAdapter(Context context, ArrayList<ReviewHelperClass> reviewHelperClasses) {
        this.context = context;
        this.reviewHelperClasses = reviewHelperClasses;

    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_layout, parent, false);
        ReviewAdapter.ReviewAdapterViewHolder orderProductAdapterViewHolder = new ReviewAdapter.ReviewAdapterViewHolder(view);

        return orderProductAdapterViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewAdapterViewHolder holder, int position) {
        //Get Data
        ReviewHelperClass productHelperClass = reviewHelperClasses.get(position);

        String reviewBy=productHelperClass.getReviewby();
        String review=productHelperClass.getReview();
        String timeStamp=productHelperClass.getTimestamp();
        float rating = productHelperClass.getRating();


        //Set Data
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateForm= DateFormat.format("dd/MM/yyyy HH:mm A",calendar).toString();
        holder.date.setText(dateForm);

        holder.review.setText(review);

        holder.prodRating.setRating(rating);

        FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneno").equalTo(reviewBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        UserHelperClass userHelperClass=ds.getValue(UserHelperClass.class);
                        String firstname=userHelperClass.getFirstname();
                        String lastname=userHelperClass.getLastname();
                        holder.username.setText(firstname+" "+lastname);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseStorage.getInstance().getReference().child("User Profiles/").child(Objects.requireNonNull(reviewBy + "/" + "profile.jpg")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Picasso.get().load(uri).centerCrop().resize(holder.userimage.getMeasuredWidth(),holder.userimage.getMeasuredHeight()).placeholder(R.drawable.default_image).into(holder.userimage);;
                }catch(Exception e){
                    holder.userimage.setImageResource(R.drawable.default_image);
                }
            }
        });

    }



    @Override
    public int getItemCount() {
        return reviewHelperClasses.size();
    }

    public static class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        RatingBar prodRating;
        CircleImageView userimage;
        TextView review,username,date;

        public ReviewAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            //hooks
            userimage = itemView.findViewById(R.id.userImage);
            prodRating = itemView.findViewById(R.id.prodRating);
            username = itemView.findViewById(R.id.userName);
            date = itemView.findViewById(R.id.revDate);
            review = itemView.findViewById(R.id.review);

        }
    }
}
