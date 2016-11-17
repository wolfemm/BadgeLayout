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

    public static class BadgeLayoutFromXMLFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_badge_layout_from_xml, container, false);
        }
    }

    public static class BadgeLayoutFromCodeFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_badge_layout_from_code, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if (savedInstanceState == null) {
                BadgeLayout badgeLayout = (BadgeLayout) getActivity().findViewById(R.id.badge_layout);

                badgeLayout.addBadge(badgeLayout
                        .newBadge()
                        .setText("Personal")
                        .setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_badge_personal, getContext().getTheme())));

                badgeLayout.addBadge(badgeLayout
                        .newBadge()
                        .setText("Business")
                        .setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_badge_business, getContext().getTheme())));


                badgeLayout.setItemSpacing((int) (getResources().getDisplayMetrics().density * 24));
            }
        }
    }
}
