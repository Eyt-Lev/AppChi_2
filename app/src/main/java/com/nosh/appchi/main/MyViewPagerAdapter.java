package com.nosh.appchi.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position){
        switch (position){
            case 1:
                return new ToolsFragment();
            case 2:
                return new FunFragment();
            default:
                return new ProductivityFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
