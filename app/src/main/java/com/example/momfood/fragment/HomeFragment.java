package com.example.momfood.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.momfood.R;
import com.example.momfood.activity.MainActivity;
import com.example.momfood.adapter.HomeSliderAdapter;
import com.example.momfood.adapter.NewProductAdapter;
import com.example.momfood.adapter.PopularProductAdapter;
import com.example.momfood.model.Category;
import com.example.momfood.model.Product;
import com.example.momfood.util.DataCallback;
import com.example.momfood.util.FirebaseDataFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    Timer timer;
    int page_position = 0;
    private int dotscount;
    private ImageView[] dots;
    private List<Category> categoryList = new ArrayList<>();
    private RecyclerView recyclerView, nRecyclerView, pRecyclerView;
    private NewProductAdapter nAdapter;
//    private PopularProductAdapter pAdapter;
    private Integer[] images = {R.drawable.slider1, R.drawable.slider2, R.drawable.slider3, R.drawable.slider4, R.drawable.slider5};
    private FirebaseDataFetcher dataFetcher, popularFetcher;
    private PopularProductAdapter adapter;
    private List<Product> productList, popularProductList;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.category_rv);
        pRecyclerView = view.findViewById(R.id.popular_product_rv);
        nRecyclerView = view.findViewById(R.id.new_product_rv);
        dataFetcher = new FirebaseDataFetcher();
        popularFetcher = new FirebaseDataFetcher();
        fetchProducts();
        fetchPopularProducts();
//        nAdapter = new NewProductAdapter(data.getNewList(), getContext(), "Home");
//        RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        nRecyclerView.setLayoutManager(nLayoutManager);
//        nRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        nRecyclerView.setAdapter(nAdapter);

//        pAdapter = new PopularProductAdapter(data.getPopularList(), getContext(), "Home");
//        RecyclerView.LayoutManager pLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        pRecyclerView.setLayoutManager(pLayoutManager);
//        pRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        pRecyclerView.setAdapter(pAdapter);


        timer = new Timer();
        viewPager = view.findViewById(R.id.viewPager);

        sliderDotspanel = view.findViewById(R.id.SliderDots);

        HomeSliderAdapter viewPagerAdapter = new HomeSliderAdapter(getContext(), images);

        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        scheduleSlider();

        return view;
    }


    public void scheduleSlider() {

        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == dotscount) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }
                viewPager.setCurrentItem(page_position, true);
            }
        };

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 4000);
    }

    @Override
    public void onStop() {
        timer.cancel();
        super.onStop();
    }

    @Override
    public void onPause() {
        timer.cancel();
        super.onPause();
    }

    public void onLetsClicked(View view) {
        startActivity(new Intent(getContext(), MainActivity.class));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Home");
    }

    private void fetchProducts() {
        dataFetcher.fetchProducts(new DataCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                productList = data;
                adapter = new PopularProductAdapter(productList, getContext(), "Home");
                RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                nRecyclerView.setLayoutManager(nLayoutManager);
                nRecyclerView.setItemAnimator(new DefaultItemAnimator());
                nRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle error
            }
        });
    }

    private void fetchPopularProducts() {
        dataFetcher.fetchPopularProducts(new DataCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                popularProductList = data;
                nAdapter = new NewProductAdapter(popularProductList, getContext(), "Home");
                RecyclerView.LayoutManager pLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                pRecyclerView.setLayoutManager(pLayoutManager);
                pRecyclerView.setItemAnimator(new DefaultItemAnimator());
                pRecyclerView.setAdapter(nAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle error
            }
        });
    }
}
