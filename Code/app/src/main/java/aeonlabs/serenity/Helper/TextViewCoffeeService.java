package aeonlabs.serenity.Helper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class TextViewCoffeeService extends androidx.appcompat.widget.AppCompatTextView {

    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "font-diner-coffee-service.otf";

    public TextViewCoffeeService(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewCoffeeService(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewCoffeeService(Context context) {
        super(context);
        init();
    }

    private void init() {

        //Font name should not contain "/".
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), FONTAWESOME);
        setTypeface(tf);
    }

}