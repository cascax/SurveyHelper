package xyz.codeme.surveyhelper;


import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import xyz.codeme.surveyhelper.utils.CommonUtils;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         @NonNull Preference preference) {
        switch(preference.getKey()) {
            case "about_me":
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(String.format(
                                getResources().getString(R.string.text_now_version),
                                CommonUtils.getVersionName(getActivity())
                        ))
                        .setMessage(R.string.text_about_me)
                        .setPositiveButton(R.string.btn_ok, null)
                        .show();
                return true;
            default:
                return false;
        }
    }
}
