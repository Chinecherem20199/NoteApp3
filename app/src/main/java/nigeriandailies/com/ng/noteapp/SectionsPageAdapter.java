package nigeriandailies.com.ng.noteapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


//implements methods for fragmentPageAdapter and also create constructor for it
class SectionsPageAdapter extends FragmentPagerAdapter {

//    keeping tracks of the fragments
    private final List<Fragment> mFragmentList = new ArrayList<>();

    //    keeping tracks of the name of the fragment
    private final List<String>mFragmentTitleList = new ArrayList<>();


//  this method will add fragment to the fragment list
    public void  addFragment(Fragment fragment, String mTitle){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(mTitle);
    }


    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
//    return page Title here
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "Home";

            case 1: return "Calender";

            case 2: return "Setting";

        }
        return null;
//        return mFragmentTitleList.get(position);
    }
//  return fragment
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0: fragment = new Home();
                break;
            case 1: fragment = new Calender();


        }return fragment;
//        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
