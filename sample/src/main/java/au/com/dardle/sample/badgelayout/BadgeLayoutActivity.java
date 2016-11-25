package au.com.dardle.sample.badgelayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import au.com.dardle.widget.BadgeLayout;

public class BadgeLayoutActivity extends AppCompatActivity {
    private static final String LOG_TAG = BadgeLayoutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_badge_layout);

        setupView();
    }

    private void setupView() {
        int from = getIntent().getIntExtra(BadgeLayoutApplication.EXTRA_FROM, BadgeLayoutApplication.FROM_XML);

        Fragment fragment;
        if (from == BadgeLayoutApplication.FROM_XML) {
            fragment = new BadgeLayoutFromXMLFragment();
        } else {
            fragment = new BadgeLayoutFromCodeFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .add(R.id.content_container, fragment)
                .commit();
    }

    public static class BadgeLayoutFromXMLFragment extends BadgeLayoutFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_badge_layout_from_xml, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            BadgeLayout badgeLayout = (BadgeLayout) getActivity().findViewById(R.id.badge_layout);
            badgeLayout.addOnBadgeClickedListener(this);

            BadgeLayout appBadgeLayout = (BadgeLayout) getActivity().findViewById(R.id.app_badge_layout);
            appBadgeLayout.addOnBadgeClickedListener(this);

            BadgeLayout soBadgeLayout = (BadgeLayout) getActivity().findViewById(R.id.so_badge_layout);
            soBadgeLayout.addOnBadgeClickedListener(this);
        }

        @Override
        public void onStop() {
            super.onStop();

            BadgeLayout badgeLayout = (BadgeLayout) getActivity().findViewById(R.id.badge_layout);
            badgeLayout.removeOnBadgeClickedListener(this);
        }
    }

    public static class BadgeLayoutFromCodeFragment extends BadgeLayoutFragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_badge_layout_from_code, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            setupDefaultBadgeLayout();
            setupAppBadgeLayout();
            setupSoBadgeLayout();
        }

        @Override
        public void onStop() {
            super.onStop();

            BadgeLayout badgeLayout = (BadgeLayout) getActivity().findViewById(R.id.badge_layout);
            badgeLayout.removeOnBadgeClickedListener(this);
        }

        private void setupDefaultBadgeLayout() {
            BadgeLayout badgeLayout = (BadgeLayout) getActivity().findViewById(R.id.badge_layout);

            // Add badges
            badgeLayout.addBadge(badgeLayout
                    .newBadge()
                    .setText("Badge")
                    .setIcon(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, getContext().getTheme())));

            badgeLayout.addBadge(badgeLayout
                    .newBadge()
                    .setText("Badge"));

            badgeLayout.addBadge(badgeLayout
                    .newBadge()
                    .setIcon(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, getContext().getTheme())));

            // Set item spacing
            badgeLayout.setSpacing((int) (getResources().getDisplayMetrics().density * 24));

            badgeLayout.addOnBadgeClickedListener(this);
        }

        private void setupAppBadgeLayout() {
            BadgeLayout appBadgeLayout = (BadgeLayout) getActivity().findViewById(R.id.app_badge_layout);
            appBadgeLayout.addOnBadgeClickedListener(this);

            appBadgeLayout.setBadgeBackground(R.drawable.background_app_badge);
            appBadgeLayout.setSpacing((int) (getResources().getDisplayMetrics().density * 8));
            appBadgeLayout.setBadgeTextColor(ResourcesCompat.getColorStateList(getResources(), android.R.color.white, getContext().getTheme()));

            // Add badges
            appBadgeLayout.addBadge(appBadgeLayout
                    .newBadge()
                    .setText("TOP CHARTS"));

            appBadgeLayout.addBadge(appBadgeLayout
                    .newBadge()
                    .setText("GAMES"));

            appBadgeLayout.addBadge(appBadgeLayout
                    .newBadge()
                    .setText("CATEGORIES"));

            appBadgeLayout.addBadge(appBadgeLayout
                    .newBadge()
                    .setText("EARLY ACCESS"));

            appBadgeLayout.addBadge(appBadgeLayout
                    .newBadge()
                    .setText("FAMILY"));

            appBadgeLayout.addBadge(appBadgeLayout
                    .newBadge()
                    .setText("EDITORS' CHOICE"));
        }

        private void setupSoBadgeLayout() {
            BadgeLayout soBadgeLayout = (BadgeLayout) getActivity().findViewById(R.id.so_badge_layout);
            soBadgeLayout.addOnBadgeClickedListener(this);

            soBadgeLayout.setBadgeBackground(R.drawable.background_so_badge);
            soBadgeLayout.setBadgeContentSpacing((int) (getResources().getDisplayMetrics().density * 10));
            soBadgeLayout.setSpacing((int) (getResources().getDisplayMetrics().density * 8));
            soBadgeLayout.setBadgeTextColor(ResourcesCompat.getColorStateList(getResources(), android.R.color.white, getContext().getTheme()));
            soBadgeLayout.setBadgeTextPosition(BadgeLayout.BadgeTextPosition.RIGHT);

            // Add badges
            soBadgeLayout.addBadge(soBadgeLayout
                    .newBadge()
                    .setText("Teacher")
                    .setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_so_badge_bronze, getContext().getTheme())));

            soBadgeLayout.addBadge(soBadgeLayout
                    .newBadge()
                    .setText("Guru")
                    .setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_so_badge_silver, getContext().getTheme())));

            soBadgeLayout.addBadge(soBadgeLayout
                    .newBadge()
                    .setText("Great Answer")
                    .setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_so_badge_gold, getContext().getTheme())));
        }
    }

    public static class BadgeLayoutFragment extends Fragment implements BadgeLayout.OnBadgeClickedListener {

        @Override
        public void onBadgeClicked(BadgeLayout.Badge badge) {
            Toast.makeText(getActivity(), (badge.getText() != null ? badge.getText() : "") + " is clicked", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
