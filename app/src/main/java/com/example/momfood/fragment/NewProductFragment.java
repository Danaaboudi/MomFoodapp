package com.example.momfood.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momfood.R;
import com.example.momfood.adapter.NewProductAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewProductFragment extends Fragment {
    RecyclerView nRecyclerView;
    private NewProductAdapter pAdapter;

    public NewProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new, container, false);
        nRecyclerView = view.findViewById(R.id.new_product_rv);
//        pAdapter = new NewProductAdapter(data.getNewList(), getContext(), "new");
//        RecyclerView.LayoutManager pLayoutManager = new LinearLayoutManager(getContext());
//        nRecyclerView.setLayoutManager(pLayoutManager);
//        nRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        nRecyclerView.setAdapter(pAdapter);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("New");
    }
}
