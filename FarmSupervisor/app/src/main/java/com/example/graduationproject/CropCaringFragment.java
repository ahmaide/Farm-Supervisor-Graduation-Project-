package com.example.graduationproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CropCaringFragment extends Fragment {

    ImageView search;
    LinearLayout avocado;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crop_caring, container, false);

        search = view.findViewById(R.id.search_problem);
        avocado = view.findViewById(R.id.avocado);

        search.setOnClickListener(v -> {
            SearchDiseaseFragment searchDiseaseFragment = new SearchDiseaseFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, searchDiseaseFragment);
            fragmentTransaction.addToBackStack(view.getTransitionName());
            fragmentTransaction.commit();
        });

        return view;
    }
}