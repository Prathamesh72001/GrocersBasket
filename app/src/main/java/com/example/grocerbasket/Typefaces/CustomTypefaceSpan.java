package com.example.grocerbasket.Typefaces;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomTypefaceSpan extends TypefaceSpan {
    final Typeface typeface;

    public CustomTypefaceSpan (Typeface typeface){
        super("");
        this.typeface=typeface;
    }

    public CustomTypefaceSpan (String family,Typeface typeface){
        super(family);
        this.typeface=typeface;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        applyCustomTypeFace(ds,typeface);
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint paint) {
        applyCustomTypeFace(paint,typeface);
    }

    private void applyCustomTypeFace(TextPaint paint, Typeface typeface) {
        int oldstyle;
        Typeface old=paint.getTypeface();
        if(old==null){
            oldstyle=0;
        }
        else{
            oldstyle=old.getStyle();
        }

        int fake=oldstyle & ~typeface.getStyle();
        if((fake & Typeface.BOLD)!=0){
            paint.setFakeBoldText(true);
        }

        if((fake & Typeface.ITALIC)!=0){
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(typeface);
    }


}
