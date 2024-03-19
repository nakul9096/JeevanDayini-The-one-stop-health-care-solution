package com.example.unityapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;

public class Tab3Fragment extends Fragment {
    private BiometricPrompt.PromptInfo promptInfo;
    private Button tbutton, contactbtn, btnBooking, faqtvi, btnApperance;
    private TextView privacytvi, termstvi;
    private Button btnchangelang;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
        tbutton = view.findViewById(R.id.tubutton);
        btnchangelang = view.findViewById(R.id.btnchangelang);
        btnApperance = view.findViewById(R.id.btnApperance);
        contactbtn = view.findViewById(R.id.contactbtn);
        privacytvi = view.findViewById(R.id.privacytv);
        termstvi = view.findViewById(R.id.termstv);
        btnBooking = view.findViewById(R.id.btnbookings);
        faqtvi = view.findViewById(R.id.faqbtn);
        tbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Tutorial.class);
                startActivity(intent);
            }
        });
        btnApperance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.showToast(getActivity(),"This version supports only moonlight-dark theme!");
            }
        });
        btnchangelang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.showToast(getActivity(), "This version supports only english!");
            }
        });
        privacytvi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to start PrivacyActivity
                Intent intent = new Intent(getActivity(), Privacy.class);
                startActivity(intent);
            }
        });
        termstvi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Terms.class);
                startActivity(intent);
            }
        });
        faqtvi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to start PrivacyActivity
                Intent intent = new Intent(getActivity(), FAQ.class);
                startActivity(intent);
            }
        });
        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getActivity(), Booking_history.class);
               startActivity(intent);
            }
        });
        contactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ContactUs.class);
                startActivity(intent);
            }
        });
        return view;
    }
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}