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
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pools;
import android.support.v7.widget.TintTypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * BadgeLayout provides a horizontal layout to display badges.
 * <p>
 * <p>Population of the badges to display is
 * done through {@link Badge} instances. You create badges via {@link #newBadge()}. From there you can
 * change the badge's label or icon via {@link Badge#setText(CharSequence)} and {@link Badge#setIcon(Drawable)}
 * respectively. To display the badge, you need to add it to the layout via one of the
 * {@link #addBadge(Badge)} methods. For example:
 * <pre>
 * BadgeLayout badgeLayout = ...;
 * badgeLayout.addBadge(badgeLayout.newBadge().setText("Badge 1"));
 * badgeLayout.addBadge(badgeLayout.newBadge().setText("Badge 2"));
 * badgeLayout.addBadge(badgeLayout.newBadge().setText("Badge 3"));
 * </pre>
 * You should set a listener via {@link #addOnBadgeClickedListener(OnBadgeClickedListener)} to be
 * notified when any badge is clicked.
 * <p>
 * <p>You can also add items to BadgeLayout in your layout through the use of {@link BadgeItem}.
 * An example usage is like so:</p>
 * <p>
 * <pre>
 * &lt;au.com.dardle.widget.BadgeLayout
 *         android:layout_height=&quot;wrap_content&quot;
 *         android:layout_width=&quot;match_parent&quot;&gt;
 *
 *     &lt;au.com.dardle.widget.BadgeItem
 *             android:text=&quot;@string/badge_text&quot;/&gt;
 *
 *     &lt;au.com.dardle.widget.BadgeItem
 *             android:icon=&quot;@drawable/ic_android&quot;/&gt;
 *
 * &lt;/au.com.dardle.widget.BadgeLayout&gt;
 * </pre>
 */

public class BadgeLayout extends HorizontalScrollView {
    private static final Pools.Pool<Badge> sBadgePool = new Pools.SynchronizedPool<>(16);

    /**
     * Callback interface invoked when a badge is clicked.
     */
    public interface OnBadgeClickedListener {
        void onBadgeClicked(Badge badge);
    }

    private final LinearLayout mContentContainer;

    private final ArrayList<Badge> mBadges = new ArrayList<>();

    private int mSpacing;   // pixel

    private int mBadgeBackgroundResId;
    private int mBadgeContentSpacing;   // pixel
    private BadgeTextPosition mBadgeTextPosition;
    private ColorStateList mBadgeTextColors;
    private int mBadgeTextSize; // pixel

    private final ArrayList<OnBadgeClickedListener> mOnBadgeClickedListeners = new ArrayList<>();
    private final OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view instanceof BadgeView) {
                BadgeView badgeView = (BadgeView) view;
                Badge badge = badgeView.mBadge;

                for (int i = mOnBadgeClickedListeners.size() - 1; i >= 0; i--) {
                    mOnBadgeClickedListeners.get(i).onBadgeClicked(badge);
                }
            }
        }
    };


    // Pool we use as a simple RecyclerBin
    private final Pools.Pool<BadgeView> mBadgeViewPool = new Pools.SimplePool<>(12);


    public BadgeLayout(Context context) {
        this(context, null);
    }

    public BadgeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Add the LinearLayout as the content container
        mContentContainer = new LinearLayout(context);
        mContentContainer.setOrientation(LinearLayout.HORIZONTAL);
        mContentContainer.setGravity(Gravity.CENTER_VERTICAL);
        super.addView(mContentContainer, 0, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                .WRAP_CONTENT));

        final TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable
                .BadgeLayout);
        mSpacing = tintTypedArray.getDimensionPixelSize(R.styleable.BadgeLayout_spacing, 8);
        mBadgeContentSpacing = tintTypedArray.getDimensionPixelSize(R.styleable.BadgeLayout_badgeContentSpacing, 0);
        mBadgeBackgroundResId = tintTypedArray.getResourceId(R.styleable.BadgeLayout_badgeBackground, 0);
        mBadgeTextPosition = BadgeTextPosition.values()[tintTypedArray.getInt(R.styleable.BadgeLayout_badgeTextPosition, BadgeTextPosition.BOTTOM.ordinal())];

        // Badge text color
        if (tintTypedArray.hasValue(R.styleable.BadgeLayout_badgeTextColor)) {
            // If we have an explicit text color set, use it
            mBadgeTextColors = tintTypedArray.getColorStateList(R.styleable.BadgeLayout_badgeTextColor);
        } else {
            mBadgeTextColors = new TextView(context).getTextColors();
        }

        // Badge text size
        mBadgeTextSize = tintTypedArray.getDimensionPixelSize(R.styleable.BadgeLayout_badgeTextSize, 14);

        tintTypedArray.recycle();
    }

    @Override
    public void addView(View child) {
        addViewInternal(child);
    }

    @Override
    public void addView(View child, int index) {
        addViewInternal(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        addViewInternal(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        addViewInternal(child);
    }

    /**
     * Create and return a new {@link Badge}. You need to manually add this using
     * {@link #addBadge(Badge)} or a related method.
     *
     * @return A new Badge
     * @see #addBadge(Badge)
     */
    @NonNull
    public Badge newBadge() {
        Badge badge = sBadgePool.acquire();
        if (badge == null) {
            badge = new Badge();
        }

        badge.mParent = this;
        badge.mView = createBadgeView(badge);

        return badge;
    }

    /**
     * Add a badge to this layout. The badge will be added at the end of the list.
     *
     * @param badge Badge to add
     */
    public void addBadge(@NonNull Badge badge) {
        if (badge.mParent != this) {
            throw new IllegalArgumentException("Badge belongs to a different BadgeLayout");
        }

        addBadgeView(badge);
        configureBadge(badge);
    }

    /**
     * Remove all badges from the layout.
     */
    public void removeAllBadges() {
        // Remove all the views
        for (int i = mContentContainer.getChildCount() - 1; i >= 0; i--) {
            removeBadgeViewAt(i);
        }

        for (final Iterator<Badge> iterator = mBadges.iterator(); iterator.hasNext(); ) {
            final Badge badge = iterator.next();
            iterator.remove();
            sBadgePool.release(badge);
        }
    }

    /**
     * Set the spacing between badge items
     *
     * @param spacing Spacing between badge items
     */
    public void setSpacing(int spacing) {
        mSpacing = spacing;
        for (int i = 1; i < mBadges.size(); i++) {
            final Badge badge = mBadges.get(i);
            final View view = badge.mView;
            if (view != null) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = mSpacing;
            }
        }
    }

    /**
     * Set the spacing between text and image
     *
     * @param badgeContentSpacing Spacing between text and image
     */
    public void setBadgeContentSpacing(int badgeContentSpacing) {
        mBadgeContentSpacing = badgeContentSpacing;

        updateBadges();
    }

    /**
     * Set badge's background
     *
     * @param badgeBackgroundResId Badge's background resource id
     */
    public void setBadgeBackground(int badgeBackgroundResId) {
        mBadgeBackgroundResId = badgeBackgroundResId;

        updateBadges();
    }

    /**
     * Set badge's text position
     *
     * @param badgeTextPosition Badge's text position
     * @see BadgeTextPosition
     */
    public void setBadgeTextPosition(BadgeTextPosition badgeTextPosition) {
        mBadgeTextPosition = badgeTextPosition;

        updateBadges();
    }

    public ColorStateList getBadgeTextColor() {
        return mBadgeTextColors;
    }

    /**
     * Set badge text color
     *
     * @param badgeTextColor Badge's text color
     */
    public void setBadgeTextColor(ColorStateList badgeTextColor) {
        mBadgeTextColors = badgeTextColor;

        updateBadges();
    }

    /**
     * Get badge text size
     *
     * @return Badge's text size
     */
    public int getBadgeTextSize() {
        return mBadgeTextSize;
    }

    /**
     * Set badge text size
     *
     * @param textSize Badge's text size
     */
    public void setBadgeTextSize(int textSize) {
        mBadgeTextSize = textSize;

        updateBadges();
    }

    /**
     * Add a {@link BadgeLayout.OnBadgeClickedListener} that will be invoked when badge is clicked.
     * <p>
     * <p>Components that add a listener should take care to remove it when finished via
     * {@link #removeOnBadgeClickedListener(OnBadgeClickedListener)}.</p>
     *
     * @param onBadgeClickedListener listener to add
     */
    public void addOnBadgeClickedListener(@NonNull OnBadgeClickedListener onBadgeClickedListener) {
        if (!mOnBadgeClickedListeners.contains(onBadgeClickedListener)) {
            mOnBadgeClickedListeners.add(onBadgeClickedListener);
        }
    }

    /**
     * Remove the given {@link BadgeLayout.OnBadgeClickedListener} that was previously added via
     * {@link #addOnBadgeClickedListener(OnBadgeClickedListener)}.
     *
     * @param onBadgeClickedListener listener to remove
     */
    public void removeOnBadgeClickedListener(@NonNull OnBadgeClickedListener onBadgeClickedListener) {
        mOnBadgeClickedListeners.remove(onBadgeClickedListener);
    }


    private void addViewInternal(final View child) {
        if (child instanceof BadgeItem) {
            addBadgeFromItemView((BadgeItem) child);
        } else {
            throw new IllegalArgumentException("Only BadgeItem instances can be added to BadgeLayout");
        }
    }

    private void addBadgeFromItemView(@NonNull BadgeItem badgeItem) {
        final Badge badge = newBadge();

        badge.setText(badgeItem.mText);

        badge.setIcon(badgeItem.mIcon);

        badge.setEnabled(badgeItem.isEnabled());

        badge.setSelected(badgeItem.isSelected());

        addBadge(badge);
    }

    private BadgeView createBadgeView(@NonNull final Badge badge) {
        BadgeView badgeView = mBadgeViewPool.acquire();
        if (badgeView == null) {
            badgeView = new BadgeView(getContext());
        }
        badgeView.setBadge(badge);
        return badgeView;
    }

    private void addBadgeView(Badge badge) {
        final BadgeView badgeView = badge.mView;
        if (badgeView != null) {
            if (badgeView.getParent() != null) {
                // Remove from parent if it is already added
                mContentContainer.removeView(badgeView);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (mContentContainer.getChildCount() > 0) {
                // Add spacing if required
                layoutParams.leftMargin = mSpacing;
            }
            badgeView.setLayoutParams(layoutParams);

            mContentContainer.addView(badgeView);
        }
    }

    private void configureBadge(Badge badge) {
        mBadges.add(badge);

        if (badge.mView != null) {
            badge.mView.setOnClickListener(mClickListener);
        }
    }

    private void updateBadges() {
        for (Badge badge : mBadges) {
            badge.updateView();
        }
    }

    private void removeBadgeViewAt(int position) {
        final BadgeView badgeView = (BadgeView) mContentContainer.getChildAt(position);
        mContentContainer.removeViewAt(position);
        if (badgeView != null) {
            mBadgeViewPool.release(badgeView);
        }
        requestLayout();
    }


    /**
     * A badge in this layout. Instances can be created via {@link #newBadge()}.
     */
    public static final class Badge {
        private Drawable mIcon;
        private CharSequence mText;
        private boolean mSelected = false;
        private boolean mEnabled = true;

        @Nullable
        private BadgeLayout mParent;
        @Nullable
        private BadgeView mView;

        private Badge() {
            // Private constructor
        }

        /**
         * Set the text displayed on this badge. Text may be truncated if there is not room to display
         * the entire string.
         *
         * @param text The text to display
         * @return The current instance for call chaining
         */
        @NonNull
        public Badge setText(@Nullable CharSequence text) {
            mText = text;
            updateView();
            return this;
        }


        /**
         * Set the icon displayed on this badge.
         *
         * @param icon The drawable to use as an icon
         * @return The current instance for call chaining
         */
        @NonNull
        public Badge setIcon(@Nullable Drawable icon) {
            mIcon = icon;
            updateView();
            return this;
        }

        /**
         * Set the selection state of this badge
         *
         * @param selected The selection state
         * @return The current instance for call chaining
         */
        @NonNull
        public Badge setSelected(boolean selected) {
            mSelected = selected;
            updateView();
            return this;
        }

        /**
         * Set the enabled state of this badge
         *
         * @param enabled The enabled state
         * @return The current instance for call chaining
         */
        @NonNull
        public Badge setEnabled(boolean enabled) {
            mEnabled = enabled;
            updateView();
            return this;
        }

        /**
         * Return the text of this badge.
         *
         * @return The badge's text
         */
        @Nullable
        public CharSequence getText() {
            return mText;
        }

        private void updateView() {
            if (mView != null) {
                mView.update();
            }
        }
    }

    public enum BadgeTextPosition {
        LEFT, TOP, RIGHT, BOTTOM
    }

    /**
     * The default badge view
     */
    private class BadgeView extends LinearLayout {
        private Badge mBadge;
        private final ImageView mImageView;
        private final TextView mTextView;

        public BadgeView(Context context) {
            super(context);

            mImageView = new ImageView(context);

            mTextView = new TextView(context);
            mTextView.setLines(1);
            mTextView.setEllipsize(TextUtils.TruncateAt.END);

            update();
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);

            mImageView.setSelected(selected);
            mTextView.setSelected(selected);
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);

            mImageView.setEnabled(enabled);
            mTextView.setEnabled(enabled);
        }

        private void setBadge(@Nullable final Badge badge) {
            if (badge != mBadge) {
                mBadge = badge;
                update();
            }
        }

        final void update() {
            updateLayout();
            updateContent();
        }

        private void updateLayout() {
            setGravity(Gravity.CENTER);
            setBackgroundResource(mBadgeBackgroundResId);

            if (mBadgeTextPosition == BadgeTextPosition.LEFT || mBadgeTextPosition == BadgeTextPosition.RIGHT) {
                setOrientation(HORIZONTAL);
            } else {
                setOrientation(VERTICAL);
            }

            removeAllViews();

            int textViewIndex = 0;
            int imageViewIndex = 0;
            LayoutParams textViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutParams imageViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            switch (mBadgeTextPosition) {
                case LEFT:
                    imageViewIndex = 1;
                    textViewIndex = 0;
                    textViewLayoutParams.rightMargin = mBadgeContentSpacing;
                    break;

                case TOP:
                    imageViewIndex = 1;
                    textViewIndex = 0;
                    textViewLayoutParams.bottomMargin = mBadgeContentSpacing;
                    break;

                case RIGHT:
                    imageViewIndex = 0;
                    textViewIndex = 1;
                    textViewLayoutParams.leftMargin = mBadgeContentSpacing;
                    break;

                case BOTTOM:
                    imageViewIndex = 0;
                    textViewIndex = 1;
                    textViewLayoutParams.topMargin = mBadgeContentSpacing;
                    break;
            }

            addView(mImageView, imageViewIndex, imageViewLayoutParams);
            addView(mTextView, textViewIndex, textViewLayoutParams);
        }

        private void updateContent() {
            if (mBadge != null) {
                mImageView.setImageDrawable(mBadge.mIcon);

                mTextView.setText(mBadge.mText);
                mTextView.setTextColor(mBadgeTextColors);
                mTextView.setTextSize(mBadgeTextSize);
                if (TextUtils.isEmpty(mTextView.getText())) {
                    mTextView.setVisibility(GONE);
                } else {
                    mTextView.setVisibility(VISIBLE);
                }

                setSelected(mBadge.mSelected);
                setEnabled(mBadge.mEnabled);
            }
        }
    }
}
