package com.devapps.affiliateadmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devapps.affiliateadmin.adapters.DealAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DealsFragment extends Fragment implements DealAdapter.OnDealClickListener {
    private static final String TAG = "DealsFragment";
    private RecyclerView recyclerView;
    private DealAdapter dealAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView emptyView;
    private DatabaseReference newsRef;
    FloatingActionButton fabAddCategory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deals, container, false);
        initializeViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        loadNews();
        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);
        fabAddCategory = view.findViewById(R.id.fabAddCategory);

        // Initialize Firebase reference with "news" node
        newsRef = FirebaseDatabase.getInstance().getReference("news");
        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddDealActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        dealAdapter = new DealAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(dealAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadNews);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorPrimaryDark
        );
    }

    private void loadNews() {
        showLoading();

        newsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Deal> dealsList = new ArrayList<>();

                for (DataSnapshot newsSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Deal deal = newsSnapshot.getValue(Deal.class);
                        if (deal != null) {
                            deal.setId(newsSnapshot.getKey());
                            dealsList.add(deal);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing news item: " + newsSnapshot.getKey(), e);
                    }
                }

                // Sort by timestamp (newest first)
                Collections.sort(dealsList, (deal1, deal2) ->
                        Long.compare(deal2.getTimestamp(), deal1.getTimestamp()));

                updateUI(dealsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read news", error.toException());
                showError(error.getMessage());
            }
        });
    }

    private void updateUI(List<Deal> dealsList) {
        hideLoading();

        if (dealsList.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
            dealAdapter.updateDeals(dealsList);
        }
    }


    @Override
    public void onDealClick(Deal deal) {

    }

    @Override
    public void onDealMenuClick(Deal deal, View anchor) {
        showDealOptionsBottomSheet(deal);
    }

    private void showDealOptionsBottomSheet(Deal deal) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_deal_menu, null);
        bottomSheetDialog.setContentView(bottomSheetView);

//        TextView tvAdd = bottomSheetView.findViewById(R.id.tvAdd);
        TextView tvEdit = bottomSheetView.findViewById(R.id.tvEdit);
        TextView tvDelete = bottomSheetView.findViewById(R.id.tvDelete);
//
//        tvAdd.setOnClickListener(v -> {
//            // Handle add to favorites
//            addToFavorites(deal);
//            bottomSheetDialog.dismiss();
//        });

        tvEdit.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), DealEditorActivity.class);
            // Pass only required data
            intent.putExtra("deal_id", deal.getId());
            intent.putExtra("title", deal.getTitle());
            intent.putExtra("description", deal.getDescription());
            intent.putExtra("image_url", deal.getImageUrl());
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });

        tvDelete.setOnClickListener(v -> {

            if (Config.isdemoEnabled) {
                Toast.makeText(getActivity(), "This feature is not available in demo mode.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Handle delete
            showDeleteConfirmation(deal);
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }


    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideEmptyView() {
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        hideLoading();
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }


    private void showDeleteConfirmation(Deal deal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle("Delete Deal")
                .setMessage("Are you sure you want to delete this deal? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Show loading state
                    ProgressDialog progressDialog = new ProgressDialog(requireContext());
                    progressDialog.setMessage("Deleting deal...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    // Perform deletion
                    if (deal.getId() != null) {
                        newsRef.child(deal.getId()).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(requireContext(), "Deal deleted successfully", Toast.LENGTH_SHORT).show();

                                    // Remove the item from the adapter
                                    int position = dealAdapter.getPosition(deal);
                                    if (position != -1) {
                                        dealAdapter.removeItem(position);

                                        // Show empty view if no items left
                                        if (dealAdapter.getItemCount() == 0) {
                                            showEmptyView();
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(requireContext(),
                                            "Failed to delete deal: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Style the delete button to be red
        Button deleteButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        deleteButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
    }
}