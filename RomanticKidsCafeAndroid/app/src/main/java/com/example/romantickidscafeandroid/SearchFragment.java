package com.example.romantickidscafeandroid;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView listview;
    static ProgressBar pb;
    String name;
    static HashMap<String,String> AlarmHashMap = new HashMap<String,String>();
    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        pb = (ProgressBar) v.findViewById(R.id.progressbar);
        pb.setVisibility(View.INVISIBLE);
        pb.getIndeterminateDrawable().setColorFilter(Color.parseColor("#C57603"), android.graphics.PorterDuff.Mode.SRC_IN);
        ListViewAdapter adapter = new ListViewAdapter();
        listview = (ListView) v.findViewById(R.id.list3);
        SearchView sv = v.findViewById(R.id.search_view);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!s.equals(""))
                    pb.setVisibility(View.VISIBLE);
                SearchListview(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                /*if(!s.equals(""))
                    pb.setVisibility(View.VISIBLE);
                SearchListview(s);*/
                return false;
            }
        });
        return v;
    }
    public void SearchListview(String s){
        ListViewAdapter adapter = new ListViewAdapter();
        String DeviceID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseDatabase.getInstance().getReference("real_alarm").child(DeviceID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AlarmHashMap.put(snapshot.getKey(), snapshot.getValue().toString());
                    //Log.d(TAG, "CafeFragment snapshot.getValue(): " + AlarmHashMap.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference("cafe").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clearItem();
                ArrayList<String> cafeUrl = new ArrayList<String>();
                ArrayList<String> cafeName = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String CafeName = snapshot.getKey();
                    if(CafeName.contains(s)) {
                        Cafe c = snapshot.getValue(Cafe.class);
                        assert c != null;
                        cafeUrl.add(c.url);
                        cafeName.add(CafeName);
                        Log.d(TAG, "CafeFragment snapshot.getValue(): " + AlarmHashMap.get(CafeName));
                        String ahmkey = AlarmHashMap.get(CafeName);
                    /*boolean b = (ahmkey.equals("1"));
                    Log.d(TAG, "boolean b: "+b+" "+ahmkey);*/
                        if (ahmkey != null) {
                            if (ahmkey.equals("1"))
                                adapter.addItem(CafeName, c.address, 1);
                            else
                                adapter.addItem(CafeName, c.address, 0);
                        } else
                            adapter.addItem(CafeName, c.address, 0);
                        listview.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        //setListview();
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                intent.putExtra("url", cafeUrl.get(i));
                                intent.putExtra("name", cafeName.get(i));
                                startActivity(intent);
                            }
                        });
                    }

                    //Log.d(TAG, "cafe name is : "+ CafeName);
                    //Log.d(TAG, "onDataChange: "+c.address + " " + c.url + " " + CafeName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        pb.setVisibility(View.INVISIBLE);
    }
}