package com.example.romantickidscafeandroid;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CafeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CafeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static HashMap<String,String> AlarmHashMap = new HashMap<String,String>();
    ListViewAdapter adapter = new ListViewAdapter();
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG1 = "1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CafeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CafeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CafeFragment newInstance(String param1, String param2) {
        CafeFragment fragment = new CafeFragment();
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
        adapter.clearItem();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_cafe, container, false);
        String DeviceID = getDeviceId(getContext());
        firebaseAuth = FirebaseAuth.getInstance();

        swipeRefreshLayout = v.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        reload();

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
        FirebaseDatabase.getInstance().getReference("real_alarm").child(DeviceID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AlarmHashMap.put(snapshot.getKey(), snapshot.getValue().toString());
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
                    ListView listview = (ListView) v.findViewById(R.id.cafelist);
                    ArrayList<String> cafeUrl = new ArrayList<String>();
                    ArrayList<String> cafeName = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String CafeName = snapshot.getKey();
                        Cafe c = snapshot.getValue(Cafe.class);
                        assert c != null;
                        cafeUrl.add(c.url);
                        cafeName.add(CafeName);
                        Log.d(TAG, "CafeFragment snapshot.getValue(): " + AlarmHashMap.get(CafeName));
                        String ahmkey = AlarmHashMap.get(CafeName);

                        if(ahmkey != null) {
                            if (ahmkey.equals("1"))
                                adapter.addItem(CafeName, c.address, 1);
                            else
                                adapter.addItem(CafeName, c.address, 0);
                        }
                        else
                            adapter.addItem(CafeName, c.address, 0);
                        listview.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        return v;
    }
    public static String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void reload() {
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getFragmentManager().beginTransaction().detach(this).commitNow();
            getFragmentManager().beginTransaction().attach(this).commitNow();
        } else {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}