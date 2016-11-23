package au.com.dardle.widget;

import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class BadgeLayout extends FrameLayout {
    private static final Pools.Pool<Badge> sBadgePool = new Pools.SynchronizedPool<>(16);
    private final Pools.Pool<BadgeView> mBadgeViewPool = new Pools.SimplePool<>(12);
    private final ArrayList<Badge> mBadges = new ArrayList<>();
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

    private int mSpacing;
    private final int mBadgeBackgroundResId;

    public interface OnBadgeClickedListener {
        void onBadgeClicked(Badge badge);
    }

    @NonNull
    private final LinearLayout mContentContainer;

    public BadgeLayout(Context context) {
        this(context, null);
    }

    public BadgeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Add the content linear layout
        mContentContainer = new LinearLayout(context);
        mContentContainer.setOrientation(LinearLayout.HORIZONTAL);
        super.addView(mContentContainer, 0, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                .WRAP_CONTENT));

        final TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable
                .BadgeLayout);
        mSpacing = tintTypedArray.getDimensionPixelSize(R.styleable.BadgeLayout_spacing, 8);
        mBadgeBackgroundResId = tintTypedArray.getResourceId(R.styleable.BadgeLayout_badgeBackground, 0);
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

    @NonNull
    public Badge newBadge() {
        // Get a Badge object from pool or manual creation
        Badge badge = sBadgePool.acquire();
        if (badge == null) {
            badge = new Badge();
        }

        // Set badge's parent view
        badge.mParent = this;

        // Bind the view to the badge
        badge.mView = createBadgeView(badge);

        return badge;
    }

    public void addBadge(@NonNull Badge badge) {
        if (badge.mParent != this) {
            throw new IllegalArgumentException("Badge belongs to a different BadgeLayout");
        }

        // Add badge's view as a child view
        addBadgeView(badge);
        configureBadge(badge);
    }

    public void removeAllBadges() {
        // Remove all badge views
        mContentContainer.removeAllViews();

        // Remove all badges
        mBadges.clear();
    }

    public void setSpacing(int spacing) {
        mSpacing = spacing;
        for (int i = 1; i < mBadges.size(); i++) {
            Badge badge = mBadges.get(i);
            View view = badge.mView;
            if (view != null) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = mSpacing;
            }
        }
    }

    public void addOnBadgeClickedListener(@NonNull OnBadgeClickedListener onBadgeClickedListener) {
        if (!mOnBadgeClickedListeners.contains(onBadgeClickedListener)) {
            mOnBadgeClickedListeners.add(onBadgeClickedListener);
        }
    }

    public void removeOnBadgeClickedListener(@NonNull OnBadgeClickedListener onBadgeClickedListener) {
        mOnBadgeClickedListeners.remove(onBadgeClickedListener);
    }


    private void addViewInternal(final View child) {
        if (child instanceof BadgeItem) {
            // Add badge from BadgeItem object in XML
            addBadgeFromItemView((BadgeItem) child);
        } else {
            throw new IllegalArgumentException("Only BadgeItem instances can be added to BadgeLayout");
        }
    }

    private void addBadgeFromItemView(@NonNull BadgeItem badgeItem) {
        // Get a badge object
        final Badge badge = newBadge();

        // Set text
        badge.setText(badgeItem.mText);

        // Set icon
        badge.setIcon(badgeItem.mIcon);

        // Set enabled status
        badge.setEnabled(badgeItem.isEnabled());

        // Set selected status
        badge.setSelected(badgeItem.isSelected());

        addBadge(badge);
    }

    private BadgeView createBadgeView(@NonNull final Badge badge) {
        // Get a badge view from pool or manually create
        BadgeView badgeView = mBadgeViewPool.acquire();
        if (badgeView == null) {
            badgeView = new BadgeView(getContext());
        }

        // Bind badge to the view
        badgeView.setBadge(badge);

        return badgeView;
    }

    private void addBadgeView(Badge badge) {
        final BadgeView badgeView = badge.mView;
        if (badgeView != null) {
            if (badgeView.getParent() != null) {
                // Remove itself from parent
                mContentContainer.removeView(badgeView);
            }

            // Add view
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (mContentContainer.getChildCount() > 0) {
                layoutParams.leftMargin = mSpacing;
            }

            badgeView.setLayoutParams(layoutParams);
            mContentContainer.addView(badgeView);
        }
    }

    private void configureBadge(Badge badge) {
        // Add this badge to the list
        mBadges.add(badge);
        if (badge.mView != null) {
            badge.mView.setOnClickListener(mClickListener);
        }
    }


    /**
     * The Badge
     */
    public static final class Badge {
        // The icon
        @Nullable
        private Drawable mIcon;

        // The text
        @Nullable
        private CharSequence mText;

        // Selected status, default is un-selected
        private boolean mSelected = false;

        // Enabled status, default is disabled
        private boolean mEnabled = true;

        @Nullable
        private BadgeLayout mParent;
        @Nullable
        private BadgeView mView;

        @NonNull
        public Badge setText(@Nullable CharSequence text) {
            // Set text and update associated view
            mText = text;
            updateView();
            return this;
        }

        @NonNull
        public Badge setIcon(@Nullable Drawable icon) {
            // Set icon and update associated view
            mIcon = icon;
            updateView();
            return this;
        }

        @NonNull
        public Badge setSelected(boolean selected) {
            // Set selected status and update associated view
            mSelected = selected;
            updateView();
            return this;
        }

        @NonNull
        public Badge setEnabled(boolean enabled) {
            // Set enabled status and update associated view
            mEnabled = enabled;
            updateView();
            return this;
        }

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


    /**
     * The default badge view
     */
    private class BadgeView extends LinearLayout {
        @Nullable
        private Badge mBadge;

        @NonNull
        private final ImageView mImageView;
        @NonNull
        private final TextView mTextView;

        public BadgeView(Context context) {
            super(context);

            // By default, icon and text are placed vertically and aligned to the center
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            if (mBadgeBackgroundResId != 0) {
                setBackgroundResource(mBadgeBackgroundResId);
            }

            // Add image view for the icon
            mImageView = new ImageView(context);
            mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            addView(mImageView);

            // Add text view for the text
            mTextView = new TextView(context);
            mTextView.setLines(1);
            mTextView.setEllipsize(TextUtils.TruncateAt.END);
            mTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            addView(mTextView);
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);

            // Set child views' selected status
            mImageView.setSelected(selected);
            mTextView.setSelected(selected);
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);

            // Set child views' enabled status
            mImageView.setEnabled(enabled);
            mTextView.setEnabled(enabled);
        }

        private void setBadge(Badge badge) {
            // Bind the badge object to the view and update using this badge's content
            mBadge = badge;
            update();
        }

        private void update() {
            if (mBadge != null) {
                // Set icon
                mImageView.setImageDrawable(mBadge.mIcon);

                // Set text
                mTextView.setText(mBadge.mText);

                // Set status
                setSelected(mBadge.mSelected);
                setEnabled(mBadge.mEnabled);
            } else {
                // Clear and hide icon
                mImageView.setImageDrawable(null);
                mImageView.setVisibility(GONE);

                // Clear and hide text
                mTextView.setText("");
                mTextView.setVisibility(GONE);

                // Set status to default values
                setSelected(false);
                setEnabled(false);
            }
        }
    }
}
