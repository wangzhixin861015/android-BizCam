package com.bcnetech.hyphoto.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.bcnetech.bcnetchhttp.bean.response.MarketParamsIndexListData;
import com.bcnetech.hyphoto.ui.fragment.MarketFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a1234 on 2017/8/7.
 */

public class MarketPagerAdapter extends FragmentPagerAdapter {

    private List<MarketFragment> mFragmentList;
    private List<MarketParamsIndexListData.PresetParmIndexManageItem> mTabNameList;
    private MarketFragment mCurFragment;

   /* MarketPagerAdapter(FragmentManager fm, List<PresetParmIndexManageItem> categoryList) {
        super(fm);
        mFragmentList = new ArrayList<>();
        mTabNameList = categoryList;
        for (PresetParmIndexManageItem presetParmIndexManageItem : categoryList) {
            Fragment f = new MarketFragment().setArguments(presetParmIndexManageItem.getId());
            mFragmentList.add(f);
        }
    }
*/

    public MarketPagerAdapter(FragmentManager fm, List<MarketParamsIndexListData.PresetParmIndexManageItem> mTabNameList) {
        super(fm);
        mFragmentList = new ArrayList<>();
        this.mTabNameList = mTabNameList;
        mFragmentList = new ArrayList<>();
        for (MarketParamsIndexListData.PresetParmIndexManageItem presetParmIndexManageItem : mTabNameList) {
            MarketFragment f = new MarketFragment().setArguments(presetParmIndexManageItem.getId(), presetParmIndexManageItem.getName());
            mFragmentList.add(f);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList != null ? mFragmentList.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabNameList.get(position).getName();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCurFragment = (MarketFragment) object;

    }

    public MarketFragment getCurrentFragment() {
        return mCurFragment;
    }

    public void selectAllFragment() {
        for (MarketFragment marketFragment : mFragmentList) {
            marketFragment.setSeleted(false);
        }
    }

    public void updateCurrentFragment() {
        mCurFragment.updateCurrentFragment();
    }

}


