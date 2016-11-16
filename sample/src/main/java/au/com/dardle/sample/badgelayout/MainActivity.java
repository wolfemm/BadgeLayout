package au.com.dardle.sample.badgelayout;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.fragment_main_preference);

            findPreference(getString(R.string.key_from_xml)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), BadgeLayoutActivity.class);
                    intent.putExtra(BadgeLayoutApplication.EXTRA_FROM, BadgeLayoutApplication.FROM_XML);
                    startActivity(intent);
                    return false;
                }
            });

            findPreference(getString(R.string.key_from_code)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), BadgeLayoutActivity.class);
                    intent.putExtra(BadgeLayoutApplication.EXTRA_FROM, BadgeLayoutApplication.FROM_CODE);
                    startActivity(intent);
                    return false;
                }
            });
        }
    }
}
