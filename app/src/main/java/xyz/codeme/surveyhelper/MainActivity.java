package xyz.codeme.surveyhelper;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private List<Fragment> mFragmentList;
    private List<String> mTitleList;
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private PagerTabStrip mTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabStrip = (PagerTabStrip) findViewById(R.id.tab_strip);

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        mFragmentList = new ArrayList<>();
        mFragmentList.add(new LevelingFragment());
        mFragmentList.add(new TraverseHorizontalFragment());
        mFragmentList.add(new TraverseVerticalFragment());

        mTitleList = new ArrayList<>();
        mTitleList.add(getString(R.string.title_leveling));
        mTitleList.add(getString(R.string.title_traverse_horizontal));
        mTitleList.add(getString(R.string.title_traverse_vertical));

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitleList.get(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
