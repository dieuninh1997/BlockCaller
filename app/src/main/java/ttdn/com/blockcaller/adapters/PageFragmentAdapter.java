package ttdn.com.blockcaller.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import ttdn.com.blockcaller.R;
import ttdn.com.blockcaller.fragments.BlackListFragment;
import ttdn.com.blockcaller.fragments.LogFragment;

/**
 * Created by Admin on 6/9/2018.
 */

public class PageFragmentAdapter extends FragmentPagerAdapter {
    private String[] tabTitles;
    private Fragment[] fragments;

    public PageFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        tabTitles = context.getResources().getStringArray(R.array.tabTitles);
        fragments = new Fragment[]{new BlackListFragment(), new LogFragment()/* , new AboutFragments()*/};

    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
