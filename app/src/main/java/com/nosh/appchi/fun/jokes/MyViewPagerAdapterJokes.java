package com.nosh.appchi.fun.jokes;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyViewPagerAdapterJokes extends FragmentStateAdapter {
    public MyViewPagerAdapterJokes(@NonNull FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position){
        if (position == 1) {
            return new AllJokesFragment();
        }
        return new JokesMainFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
