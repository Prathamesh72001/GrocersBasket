package com.example.grocerbasket.Constructors;

public class ReviewHelperClass {

    String reviewby,review,timestamp,pid;
    float rating;

    public ReviewHelperClass() {
    }

    public ReviewHelperClass(String reviewby, float rating, String review, String timestamp,String pid) {
        this.reviewby = reviewby;
        this.rating = rating;
        this.review = review;
        this.timestamp = timestamp;
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getReviewby() {
        return reviewby;
    }

    public void setReviewby(String reviewby) {
        this.reviewby = reviewby;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
