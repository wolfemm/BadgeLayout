package au.com.dardle.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.View;


public class BadgeItem extends View {
    final Drawable mIcon;
    final CharSequence mText;

    public BadgeItem(Context context) {
        this(context, null);
    }

    public BadgeItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable
                .BadgeItem);
        mIcon = tintTypedArray.getDrawable(R.styleable.BadgeItem_android_icon);
        mText = tintTypedArray.getText(R.styleable.BadgeItem_android_text);
        setEnabled(tintTypedArray.getBoolean(R.styleable.BadgeItem_android_enabled, true));
        setSelected(tintTypedArray.getBoolean(R.styleable.BadgeItem_selected, false));
        tintTypedArray.recycle();
    }
}
