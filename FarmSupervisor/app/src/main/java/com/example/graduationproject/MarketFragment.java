package com.example.graduationproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MarketFragment extends Fragment {

    LinearLayout seffarini, lefdawi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market, container, false);

        seffarini = view.findViewById(R.id.Saffarini);
        lefdawi = view.findViewById(R.id.Lefdawi);

        seffarini.setOnClickListener(v -> {
            SaffariniFragment saffariniFragment = new SaffariniFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, saffariniFragment);
            fragmentTransaction.addToBackStack(null); // Optional: adds this transaction to the back stack
            fragmentTransaction.commit();
        });

        lefdawi.setOnClickListener(v -> {
            LefdawiFragment lefdawiFragment = new LefdawiFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, lefdawiFragment);
            fragmentTransaction.addToBackStack(null); // Optional: adds this transaction to the back stack
            fragmentTransaction.commit();
        });

        return view;
    }
}