package au.com.dardle.sample.badgelayout;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
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
        fragmentManager.beginTransaction()
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
        private BadgeLayout mBadgeLayout;
        private BadgeLayout.Badge mBadge;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_badge_layout_from_code, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            setupDefaultBadgeLayout();
        }

        @Override
        public void onStop() {
            super.onStop();

            BadgeLayout badgeLayout = (BadgeLayout) getActivity().findViewById(R.id.badge_layout);
            badgeLayout.removeOnBadgeClickedListener(this);
        }

        private void setupDefaultBadgeLayout() {
            mBadgeLayout = (BadgeLayout) getActivity().findViewById(R.id.badge_layout);

            mBadge = mBadgeLayout
                    .newBadge()
                    .setText("Badge 1")
                    .setIcon(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, getContext().getTheme()));
            mBadgeLayout.addBadge(mBadge);

            mBadgeLayout.addBadge(mBadgeLayout
                    .newBadge()
                    .setText("Badge 2")
                    .setIcon(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, getContext().getTheme())));

            mBadgeLayout.addBadge(mBadgeLayout
                    .newBadge()
                    .setText("Badge 3")
                    .setIcon(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, getContext().getTheme())));

            mBadgeLayout.addBadge(mBadgeLayout
                    .newBadge()
                    .setText("Badge 4")
                    .setIcon(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, getContext().getTheme())));

            mBadgeLayout.addBadge(mBadgeLayout
                    .newBadge()
                    .setText("Badge 5")
                    .setIcon(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, getContext().getTheme())));

            mBadgeLayout.addBadge(mBadgeLayout
                    .newBadge()
                    .setText("Badge 6")
                    .setIcon(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, getContext().getTheme())));

            AppCompatSeekBar spacingAppCompatSeekBar = (AppCompatSeekBar) getActivity().findViewById(R.id.spacing_seek_bar);
            spacingAppCompatSeekBar.setProgress(mBadgeLayout.getSpacing());
            spacingAppCompatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    update();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });


            EditText editText = (EditText) getActivity().findViewById(R.id.edit_text);
            editText.setText(mBadge.getText());
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    update();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            AppCompatSeekBar textSizeSeekBar = (AppCompatSeekBar) getActivity().findViewById(R.id.text_size_seek_bar);
            textSizeSeekBar.setProgress(mBadgeLayout.getBadgeTextSize());
            textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    update();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            AppCompatSpinner textColorSpinner = (AppCompatSpinner) getActivity().findViewById(R.id.text_color_spinner);
            ArrayAdapter<String> textColorArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"Black", "Red", "Blue"});
            textColorArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            textColorSpinner.setAdapter(textColorArrayAdapter);
            textColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    update();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            AppCompatSpinner textPositionSpinner = (AppCompatSpinner) getActivity().findViewById(R.id.text_position_spinner);
            ArrayAdapter<BadgeLayout.BadgeTextPosition> textPositionArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, BadgeLayout.BadgeTextPosition.values());
            textPositionArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            textPositionSpinner.setAdapter(textPositionArrayAdapter);
            textPositionSpinner.setSelection(mBadgeLayout.getBadgeTextPosition().ordinal());
            textPositionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    update();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            AppCompatSeekBar contentSpacingSeekBar = (AppCompatSeekBar) getActivity().findViewById(R.id.content_spacing_seek_bar);
            contentSpacingSeekBar.setProgress(mBadgeLayout.getBadgeContentSpacing());
            contentSpacingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    update();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            AppCompatSpinner modeSpinner = (AppCompatSpinner) getActivity().findViewById(R.id.mode_spinner);
            ArrayAdapter<BadgeLayout.BadgeMode> modeArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, BadgeLayout.BadgeMode.values());
            modeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            modeSpinner.setAdapter(modeArrayAdapter);
            modeSpinner.setSelection(mBadgeLayout.getBadgeMode().ordinal());
            modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    update();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        private void update() {
            AppCompatSeekBar spacingAppCompatSeekBar = (AppCompatSeekBar) getActivity().findViewById(R.id.spacing_seek_bar);
            EditText editText = (EditText) getActivity().findViewById(R.id.edit_text);
            AppCompatSeekBar appCompatSeekBar = (AppCompatSeekBar) getActivity().findViewById(R.id.text_size_seek_bar);
            AppCompatSpinner textColorSpinner = (AppCompatSpinner) getActivity().findViewById(R.id.text_color_spinner);
            AppCompatSpinner appCompatSpinner = (AppCompatSpinner) getActivity().findViewById(R.id.text_position_spinner);
            AppCompatSeekBar contentSpacingSeekBar = (AppCompatSeekBar) getActivity().findViewById(R.id.content_spacing_seek_bar);
            AppCompatSpinner modeSpinner = (AppCompatSpinner) getActivity().findViewById(R.id.mode_spinner);

            mBadgeLayout.setSpacing(spacingAppCompatSeekBar.getProgress());

            mBadge.setText(editText.getText());
            mBadgeLayout.setBadgeTextSize(appCompatSeekBar.getProgress());
            mBadgeLayout.setBadgeTextPosition((BadgeLayout.BadgeTextPosition) appCompatSpinner.getSelectedItem());
            mBadgeLayout.setBadgeContentSpacing(contentSpacingSeekBar.getProgress());
            mBadgeLayout.setBadgeMode((BadgeLayout.BadgeMode) modeSpinner.getSelectedItem());

            int[][] states = new int[][]{
                    new int[]{}
            };
            int[] colors = new int[]{
                    Color.parseColor(textColorSpinner.getSelectedItem().toString().toLowerCase())
            };
            mBadgeLayout.setBadgeTextColor(new ColorStateList(states, colors));
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
