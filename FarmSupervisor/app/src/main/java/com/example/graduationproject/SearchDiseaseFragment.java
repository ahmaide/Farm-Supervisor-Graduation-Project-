package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class SearchDiseaseFragment extends Fragment {

    SearchView searchView;
    ListView searchListView;
    ImageView reportProblem;
    ImageView reportProblem1;
    ArrayList<String> fullArrayList;
    ArrayList<String> pestsArray;
    ArrayList<String> filteredArrayList;
    ArrayAdapter<String> arrayAdapter;

    SharedPrefManager sharedPrefManager;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_disease, container, false);
        sharedPrefManager = SharedPrefManager.getInstance(getActivity());
        searchView = view.findViewById(R.id.searchView);
        searchListView = view.findViewById(R.id.search_listView);
        reportProblem = view.findViewById(R.id.report_problem);
        reportProblem1 = view.findViewById(R.id.report_problem1);

        pestsArray = new ArrayList<>();
        getDataFromAPI();

        Log.d("Pests number", String.valueOf(pestsArray.size()));

        filteredArrayList = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, filteredArrayList);
        searchListView.setAdapter(arrayAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Clear the previous results
                filteredArrayList.clear();

                // If the search query is not empty, filter the full list
                if (!newText.isEmpty()) {
                    for (String item : pestsArray) {
                        if (item.toLowerCase().contains(newText.toLowerCase())) {
                            filteredArrayList.add(item);
                        }
                    }
                }
                //update adapter
                arrayAdapter.notifyDataSetChanged();

                return false;
            }
        });

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String query = (String) parent.getItemAtPosition(position);  // Get the clicked item's text

                Bundle bundle = new Bundle();
                bundle.putString("pest name", query);
                PestInfoFragment pestInfoFragment = new PestInfoFragment();
                pestInfoFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, pestInfoFragment);
                fragmentTransaction.addToBackStack(view.getTransitionName());
                fragmentTransaction.commit();
            }
        });

        reportProblem.setOnClickListener(v -> {
            ReportProblemFragment reportProblemFragment = new ReportProblemFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, reportProblemFragment);
            fragmentTransaction.addToBackStack(view.getTransitionName());
            fragmentTransaction.commit();
        });

        reportProblem1.setOnClickListener(v -> {
            ReportSymptomProblem reportSymptomProblem = new ReportSymptomProblem();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, reportSymptomProblem);
            fragmentTransaction.addToBackStack(view.getTransitionName());
            fragmentTransaction.commit();
        });

        return view;
    }

    private void getDataFromAPI() {
        String url = "http://10.0.2.2:8000/api/get_pest_names/";

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"}) JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            pestsArray.clear();
            try {
                JSONArray result = response.getJSONArray("pest_names");
                for (int i = 0; i < result.length(); i++) {
                    String pest = result.getString(i);
                    Log.d("showResultDialog", pest);
                    pestsArray.add(pest);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> Toast.makeText(getContext(), "There is no response", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }


}