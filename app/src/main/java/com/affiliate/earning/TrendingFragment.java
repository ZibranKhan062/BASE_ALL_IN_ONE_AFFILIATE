package com.affiliate.earning;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class TrendingFragment extends Fragment {

    SliderView catSlider;
    EditText search;
    RecyclerView shopRecycler;
    List<CategorySliderItems> sliderItemList;
    DatabaseReference sliderReference, searchReference;
    CategorySliderAdapter sliderAdapter;
    SliderView sliderView;
    FirebaseRecyclerOptions<CategoryModel> options;
    CategoryAdapter categoryAdapter;
    Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trending, null);

        search = view.findViewById(R.id.search);
        shopRecycler = view.findViewById(R.id.shopRecycler);
        sliderView = view.findViewById(R.id.catSlider);


        checkConnection();
        loadSliderImages();
        loadCategories();
        searchOp();

        return view;


    }

    private void checkConnection() {
        if (!isNetworkAvailable()) {
            Toast.makeText(mContext, "No Internet Connection Available", Toast.LENGTH_LONG).show();

        }
    }
    
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void searchOp() {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /* text methods */
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /* text methods */

            }

            @Override
            public void afterTextChanged(Editable editable) {


                if (TextUtils.isEmpty(search.getText().toString())) {
                    loadCategories();
                } else {
                    searchReference = FirebaseDatabase.getInstance().getReference("ShopCategories");
                    searchReference.keepSynced(true);
                    options = new FirebaseRecyclerOptions.Builder<CategoryModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference("ShopCategories").orderByChild("title").startAt(String.valueOf(editable)).endAt(editable + "\uf8ff"), CategoryModel.class)
                            .build();

                    shopRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
                    categoryAdapter = new CategoryAdapter(options, mContext);
                    categoryAdapter.startListening();

                    shopRecycler.setAdapter(categoryAdapter);
                }

            }

        });
    }

    private void loadCategories() {

        options = new FirebaseRecyclerOptions.Builder<CategoryModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("ShopCategories"), CategoryModel.class)
                .build();
        shopRecycler.setLayoutManager(new GridLayoutManager(mContext, 3));
        categoryAdapter = new CategoryAdapter(options, mContext);
        categoryAdapter.startListening();
        shopRecycler.setAdapter(categoryAdapter);


    }

    private void loadSliderImages() {
        sliderItemList = new ArrayList<>();
        sliderReference = FirebaseDatabase.getInstance().getReference("ShopSliderItems");
        sliderReference.keepSynced(true);
        sliderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        CategorySliderItems sliderItem = dataSnapshot1.getValue(CategorySliderItems.class);
                        sliderItemList.add(sliderItem);
                    }

                    sliderAdapter = new CategorySliderAdapter(getContext(), sliderItemList);

                    sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
                    sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                    sliderView.startAutoCycle();


                    sliderView.setSliderAdapter(new CategorySliderAdapter(getContext(), sliderItemList));

                } else {
                    Toast.makeText(getContext(), "No data available !", Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        categoryAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        categoryAdapter.stopListening();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}