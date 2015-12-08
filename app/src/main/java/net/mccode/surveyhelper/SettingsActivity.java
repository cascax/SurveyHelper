package net.mccode.surveyhelper;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.setting_title);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getFragmentManager();
        Fragment settingsFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if(settingsFragment == null) {
            settingsFragment = new SettingsFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, settingsFragment)
                    .commit();
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}
