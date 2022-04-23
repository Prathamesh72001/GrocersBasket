package com.example.grocerbasket.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerbasket.Constructors.ReviewHelperClass;
import com.example.grocerbasket.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class UserReviewadapter extends RecyclerView.Adapter<UserReviewadapter.UserReviewadapterViewHolder> {
    Context context;
    public ArrayList<ReviewHelperClass> reviewHelperClasses;


    public UserReviewadapter(Context context, ArrayList<ReviewHelperClass> reviewHelperClasses) {
        this.context = context;
        this.reviewHelperClasses = reviewHelperClasses;

    }

    @NonNull
    @Override
    public UserReviewadapter.UserReviewadapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review_layout, parent, false);
        UserReviewadapter.UserReviewadapterViewHolder orderProductAdapterViewHolder = new UserReviewadapter.UserReviewadapterViewHolder(view);

        return orderProductAdapterViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull UserReviewadapter.UserReviewadapterViewHolder holder, int position) {
        //Get Data
        ReviewHelperClass productHelperClass = reviewHelperClasses.get(position);

        String review = productHelperClass.getReview();
        String timeStamp = productHelperClass.getTimestamp();
        float rating = productHelperClass.getRating();
        String pid = productHelperClass.getPid();
        String reviewby = productHelperClass.getReviewby();


        //Set Data
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateForm = DateFormat.format("dd/MM/yyyy", calendar).toString();
        holder.date.setText(dateForm);

        holder.review.setText(review);

        holder.prodRating.setRating(rating);

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.suggestion_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.copy:
                                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clipData = ClipData.newPlainText("Data", review);
                                clipboardManager.setPrimaryClip(clipData);
                                Toast.makeText(context, "Review copied successfully", Toast.LENGTH_LONG).show();

                                return true;

                            case R.id.delete:
                                FirebaseDatabase.getInstance().getReference("Product Reviews").child(pid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        long i = snapshot.getChildrenCount();
                                        Toast.makeText(context, String.valueOf(i), Toast.LENGTH_LONG).show();
                                        FirebaseDatabase.getInstance().getReference("Product Reviews").child(pid).orderByChild("timestamp").equalTo(timeStamp).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (i != 1) {
                                                    FirebaseDatabase.getInstance().getReference("Products").child("All Products").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                float pRating = snapshot.child("rating").getValue(Float.class);
                                                                float new_rating = (pRating * 2) - rating;
                                                                //Toast.makeText(context, String.valueOf(new_rating), Toast.LENGTH_LONG).show();

                                                                DecimalFormat df = new DecimalFormat("#.#");
                                                                String d = df.format(new_rating);

                                                                FirebaseDatabase.getInstance().getReference("Products").child("All Products").child(pid).child("rating").setValue(Float.parseFloat(d));

                                                                FirebaseDatabase.getInstance().getReference("Products").child("Top Offers").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()) {
                                                                            FirebaseDatabase.getInstance().getReference("Products").child("Top Offers").child(pid).child("rating").setValue(Float.parseFloat(d));
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });

                                                                FirebaseDatabase.getInstance().getReference("Products").child("Trending").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()) {
                                                                            FirebaseDatabase.getInstance().getReference("Products").child("Trending").child(pid).child("rating").setValue(Float.parseFloat(d));
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });

                                                                FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(reviewby).child("cartProdHelperClass").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()) {
                                                                            FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(reviewby).child("cartProdHelperClass").child(pid).child("rating").setValue(Float.parseFloat(d));
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });

                                                                FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(reviewby).child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()) {
                                                                            FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(reviewby).child(pid).child("rating").setValue(Float.parseFloat(d));
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });

                                                                FirebaseDatabase.getInstance().getReference("Orders").child(reviewby).child("Items").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()) {
                                                                            FirebaseDatabase.getInstance().getReference("Orders").child(reviewby).child("Items").child(pid).child("rating").setValue(Float.parseFloat(d));
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
                                                } else {
                                                    String d = "0.0";
                                                    FirebaseDatabase.getInstance().getReference("Products").child("All Products").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                FirebaseDatabase.getInstance().getReference("Products").child("All Products").child(pid).child("rating").setValue(Float.parseFloat(d));
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    FirebaseDatabase.getInstance().getReference("Products").child("Top Offers").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                FirebaseDatabase.getInstance().getReference("Products").child("Top Offers").child(pid).child("rating").setValue(Float.parseFloat(d));
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    FirebaseDatabase.getInstance().getReference("Products").child("Trending").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                FirebaseDatabase.getInstance().getReference("Products").child("Trending").child(pid).child("rating").setValue(Float.parseFloat(d));
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(reviewby).child("cartProdHelperClass").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(reviewby).child("cartProdHelperClass").child(pid).child("rating").setValue(Float.parseFloat(d));
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(reviewby).child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(reviewby).child(pid).child("rating").setValue(Float.parseFloat(d));
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    FirebaseDatabase.getInstance().getReference("Orders").child(reviewby).child("Items").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                FirebaseDatabase.getInstance().getReference("Orders").child(reviewby).child("Items").child(pid).child("rating").setValue(Float.parseFloat(d));
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                }
                                                FirebaseDatabase.getInstance().getReference("User Reviews").child(reviewby).child(timeStamp).removeValue();
                                                FirebaseDatabase.getInstance().getReference("Product Reviews").child(pid).child(timeStamp).removeValue();
                                                Toast.makeText(context, "Review removed successfully", Toast.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        FirebaseDatabase.getInstance().getReference("Products").child("All Products").orderByChild("prodid").equalTo(pid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child(pid).child("prodname").getValue(String.class);
                    String Image = snapshot.child(pid).child("prodimg").getValue(String.class);

                    holder.prodname.setText(name);

                    try {
                        Picasso.get().load(Image).placeholder(R.drawable.default_image).into(holder.prodImage);
                    } catch (Exception e) {
                        holder.prodImage.setImageResource(R.drawable.default_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return reviewHelperClasses.size();
    }

    public static class UserReviewadapterViewHolder extends RecyclerView.ViewHolder {

        RatingBar prodRating;
        ImageView prodImage, more;
        TextView review, prodname, date;

        public UserReviewadapterViewHolder(@NonNull View itemView) {
            super(itemView);
            //hooks
            prodImage = itemView.findViewById(R.id.productImage_Dashboard);
            prodRating = itemView.findViewById(R.id.prodRating);
            prodname = itemView.findViewById(R.id.prodName);
            date = itemView.findViewById(R.id.date);
            review = itemView.findViewById(R.id.review);
            more = itemView.findViewById(R.id.more);

        }
    }
}
