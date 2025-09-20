package com.example.unityapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class RegisterFragment extends Fragment {
    private ViewPager2 viewPager;
    private RegistrationPagerAdapter pagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        pagerAdapter = new RegistrationPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(false);
        return view;
    }

    private void finishRegistration() {
        // Handle completion or submission logic
    }
}