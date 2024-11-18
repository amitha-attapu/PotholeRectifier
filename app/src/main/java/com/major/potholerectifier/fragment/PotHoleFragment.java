package com.major.potholerectifier.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.major.potholerectifier.R;
import com.major.potholerectifier.ViewPotHoleActivity;
import com.major.potholerectifier.adapter.PotHoleListAdapter;
import com.major.potholerectifier.model.PotHole;
import com.major.potholerectifier.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

import static com.major.potholerectifier.utils.AppConstants.GOVT_LOGIN;
import static com.major.potholerectifier.utils.AppConstants.REQ_VIEW_PH;
import static com.major.potholerectifier.utils.AppConstants.REQ_VIEW_PH_FROM;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PotHoleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PotHoleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ListView potHolesListView;
    private Spinner localityFilterSpinner;
    private ArrayAdapter spinnerArrayAdapter;
    private PotHoleListAdapter adapter;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<PotHole> potHoles;

    public PotHoleFragment() {
        // Required empty public constructor
    }
    public PotHoleFragment(List<PotHole> potHoles) {
        // Required empty public constructor
        this.potHoles = potHoles;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PotHoleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PotHoleFragment newInstance(String param1, String param2) {
        PotHoleFragment fragment = new PotHoleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pot_hole, container, false);
        potHolesListView = (ListView) view.findViewById(R.id.pot_holes_govt_list);
        adapter=new PotHoleListAdapter(getActivity(),potHoles);
        potHolesListView.setAdapter(adapter);
        localityFilterSpinner = (Spinner) view.findViewById(R.id.locality_spinner_filter);
        spinnerArrayAdapter = new ArrayAdapter(getActivity(),R.layout.support_simple_spinner_dropdown_item, AppConstants.localities);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        localityFilterSpinner.setAdapter(spinnerArrayAdapter);
        potHolesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // TODO Auto-generated method stub
                PotHole ph = potHoles.get(position);


                showToast("Clicked on "+ph.getLocality()+":"+ph.getStatus());

                Intent intent = new Intent(getActivity(), ViewPotHoleActivity.class);
                intent.putExtra(REQ_VIEW_PH,ph);
                intent.putExtra(REQ_VIEW_PH_FROM,GOVT_LOGIN);
                startActivity(intent);


            }
        });

        localityFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position!=0){
                    List<PotHole> newpotHoles = new ArrayList<>();
                    for(PotHole ph : potHoles){
                        if(ph.getLocality().equals(AppConstants.localities[position])){
                            newpotHoles.add(ph);
                        }
                    }
                    if(!newpotHoles.isEmpty()){
                        adapter.updateAdapter(newpotHoles);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


}
