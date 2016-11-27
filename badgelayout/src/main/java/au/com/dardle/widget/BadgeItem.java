/*
 * Copyright (C) 2016 Dardle Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.dardle.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.View;


/**
 * BadgeItem is a special 'view' which allows you to declare badge items for a {@link BadgeLayout}
 * within a layout. This view is not actually added to BadgeLayout, it is just a dummy which allows
 * setting of a badge items's text, icon. See BadgeLayout for more information on how
 * to use it.
 *
 * @see BadgeLayout
 */
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
