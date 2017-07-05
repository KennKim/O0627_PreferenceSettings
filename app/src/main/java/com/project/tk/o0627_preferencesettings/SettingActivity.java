package com.project.tk.o0627_preferencesettings;


import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SettingActivity extends PreferenceActivity {

	@Override

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		 setContentView(R.layout.activity_main);
		 addPreferencesFromResource(R.xml.appsetting);
//		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

		setOnPreferenceChange(findPreference("userName"));
		setOnPreferenceChange(findPreference("userNameOpen"));
		setOnPreferenceChange(findPreference("autoUpdate_ringtone"));
		setOnPreferenceChange(findPreference("myPrefTest"));
		setOnPreferenceChange(findPreference("myPrefTest2"));
	}

//	public static class MyPreferenceFragment extends PreferenceFragment {
//		@Override
//		public void onCreate(final Bundle savedInstanceState) {
//			super.onCreate(savedInstanceState);
//
//			addPreferencesFromResource(R.xml.appsetting);
//		}
//	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
            root.addView(bar, 0); // insert at top
            bar.setTitleTextColor(android.graphics.Color.WHITE);
            
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);

            root.removeAllViews();

            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);


            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }else{
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
}

	private void setOnPreferenceChange(Preference mPreference) {
		mPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);

		onPreferenceChangeListener.onPreferenceChange(mPreference, PreferenceManager
				.getDefaultSharedPreferences(mPreference.getContext()).getString(mPreference.getKey(), ""));
	}

	private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			String stringValue = newValue.toString();

			if (preference instanceof EditTextPreference) {
				preference.setSummary(stringValue);

			} else if (preference instanceof ListPreference) {
				/**
				 * ListPreference의 경우 stringValue가 entryValues이기 때문에 바로 Summary를
				 * 적용하지 못한다 따라서 설정한 entries에서 String을 로딩하여 적용한다
				 */

				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

			} else if (preference instanceof RingtonePreference) {
				/**
				 * RingtonePreference의 경우 stringValue가
				 * content://media/internal/audio/media의 형식이기 때문에
				 * RingtoneManager을 사용하여 Summary를 적용한다
				 * 
				 * 무음일경우 ""이다
				 */

				if (TextUtils.isEmpty(stringValue)) {
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary("무음으로 설정됨");

				} else {
					Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));

					if (ringtone == null) {
						// Clear the summary if there was a lookup error.
						preference.setSummary(null);

					} else {
						String name = ringtone.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}
			}

			return true;
		}

	};

}
