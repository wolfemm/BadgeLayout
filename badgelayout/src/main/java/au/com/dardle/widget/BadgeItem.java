package au.com.dardle.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.View;


public class BadgeItem extends View {
    final CharSequence mText;
    final Drawable mIcon;

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
        mText = tintTypedArray.getText(R.styleable.BadgeItem_android_text);
        mIcon = tintTypedArray.getDrawable(R.styleable.BadgeItem_android_icon);
        boolean enabled = tintTypedArray.getBoolean(R.styleable.BadgeItem_android_enabled, true);
        boolean selected = tintTypedArray.getBoolean(R.styleable.BadgeItem_selected, false);
        tintTypedArray.recycle();

        setEnabled(enabled);
        setSelected(selected);
    }
}
