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
    private Button tbutton, contactbtn, btneditprof, faqtvi;
    private TextView privacytvi, termstvi;
    private Button btnchangelang;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
        tbutton = view.findViewById(R.id.tubutton);
        btnchangelang = view.findViewById(R.id.btnchangelang);
        contactbtn = view.findViewById(R.id.contactbtn);
        privacytvi = view.findViewById(R.id.privacytv);
        termstvi = view.findViewById(R.id.termstv);
        btneditprof = view.findViewById(R.id.btneditprof);
        faqtvi = view.findViewById(R.id.faqbtn);
        tbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Tutorial.class);
                startActivity(intent);
            }
        });
        btnchangelang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Only english language is available in this version!",Toast.LENGTH_SHORT).show();
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
        btneditprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with FragmentTab4
                Tab4Fragment fragmentTab4 = new Tab4Fragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container7, fragmentTab4)
                        .addToBackStack(null)
                        .commit();
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