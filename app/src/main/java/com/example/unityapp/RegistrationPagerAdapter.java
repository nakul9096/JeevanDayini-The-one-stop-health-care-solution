package com.example.unityapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RegistrationPagerAdapter extends FragmentStateAdapter {

    public RegistrationPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the appropriate fragment based on the position
        switch (position) {
            case 0:
                return new FragmentReg1();
            case 1:
                return new FragmentReg2();
            case 2:
                return new FragmentReg3();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        // Return the total number of fragments
        return 3;
    }
}
