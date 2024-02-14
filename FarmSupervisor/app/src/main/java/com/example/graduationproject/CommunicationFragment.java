package com.example.graduationproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class CommunicationFragment extends Fragment {

    LinearLayout tillageSpec, sprayingSpec, pruningSpec;
    Button addFarmer, deleteFramer;
    SharedPrefManager sharedPrefManager;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communication, container, false);

        addFarmer = view.findViewById(R.id.addSpec);
        deleteFramer = view.findViewById(R.id.deleteSpec);
        tillageSpec = view.findViewById(R.id.tillageSpec);
        sprayingSpec = view.findViewById(R.id.sprayingSpec);
        pruningSpec = view.findViewById(R.id.pruningSpec);
        sharedPrefManager = SharedPrefManager.getInstance(getActivity());

        tillageSpec.setOnClickListener(v -> {
            TillageFarmersFragment tillageFarmersFragment = new TillageFarmersFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, tillageFarmersFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        sprayingSpec.setOnClickListener(v -> {
            SprayFarmersFragment sprayFarmersFragment = new SprayFarmersFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, sprayFarmersFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        pruningSpec.setOnClickListener(v -> {
            PruningFarmersFragment pruningFarmersFragment = new PruningFarmersFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, pruningFarmersFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        addFarmer.setOnClickListener(v -> {
            AddFarmerSpecFragment addFarmerSpecFragment = new AddFarmerSpecFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, addFarmerSpecFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        deleteFramer.setOnClickListener(v -> {
            DeleteFramerSpecFragment deleteFramerSpecFragment = new DeleteFramerSpecFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, deleteFramerSpecFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        return view;
    }


}