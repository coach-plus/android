package com.mathandoro.coachplus.views.layout;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;


public class FontAwesomeView extends AppCompatTextView {

    private Context context;

    public FontAwesomeView(Context context) {
        super(context);
        this.context = context;
        createView();
    }

    public FontAwesomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        createView();
    }

    private void createView(){
        setGravity(Gravity.CENTER);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fa_solid_900.ttf");
        setTypeface(typeface);
    }
}
