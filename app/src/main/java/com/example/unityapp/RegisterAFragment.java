package com.example.unityapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class RegisterAFragment extends Fragment {
    private ViewPager2 viewPager;
    private PagerAdapter pagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_a, container, false);

        viewPager = view.findViewById(R.id.viewPagerAdmin1);
        pagerAdapter = new PagerAdapter(this); // Pass 'this' as a Fragment
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(false);
        return view;
    }

    private void finishRegistration() {
    }
}
