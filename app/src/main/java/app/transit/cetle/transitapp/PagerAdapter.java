package app.transit.cetle.transitapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                DeparturesFragment wash_fragment = DeparturesFragment.newInstance("getWash", MainActivity.WASH);
                return wash_fragment;
            case 1:
                DeparturesFragment home_fragment = DeparturesFragment.newInstance("getHome", MainActivity.HOME);
                return home_fragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) return "Wash";
        return "Home";
    }
}