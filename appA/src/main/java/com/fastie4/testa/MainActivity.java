package com.fastie4.testa;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;

import com.fastie4.common.Common;
import com.fastie4.testa.fragment.HistoryFragment;
import com.fastie4.testa.fragment.TestFragment;
import com.fastie4.testa.listener.OnLinkListener;

public class MainActivity extends AppCompatActivity implements OnLinkListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    @Override
    public void openLink(String action, String link, long id, int status) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(Common.APP_TEST_B_PACKAGE);
        if (intent != null) {
            intent.setAction(action);
            intent.putExtra(Common.EXTRA_LINK, link);
            intent.putExtra(Common.EXTRA_ID, id);
            intent.putExtra(Common.EXTRA_STATUS, status);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new TestFragment();
            } else return new HistoryFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}