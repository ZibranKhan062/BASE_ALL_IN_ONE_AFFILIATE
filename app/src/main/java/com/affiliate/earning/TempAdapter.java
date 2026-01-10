package com.affiliate.earning;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affiliate.earning.models.TempModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class TempAdapter extends FirebaseRecyclerAdapter<TempModel, TempAdapter.Viewholder> {
    private final Context context;
    private final Map<String, TempChildAdapter> childAdapters = new HashMap<>();

    public TempAdapter(@NonNull FirebaseRecyclerOptions<TempModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.home_items_temp, parent, false);
        return new Viewholder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Viewholder holder, int position, @NonNull TempModel tempModel) {
        try {
            // Get the actual position
            final int adapterPosition = holder.getAbsoluteAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            holder.label.setText(tempModel.getName());

            // Setup child RecyclerView
            String key = getRef(adapterPosition).getKey();
            if (key != null) {
                setupChildRecyclerView(holder, key);
            }

            holder.sellAllLebel.setOnClickListener(view -> {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    String currentKey = getRef(adapterPosition).getKey();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("CurrentPosition", currentKey);
                    intent.putExtra("itemName", holder.label.getText().toString().trim());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupChildRecyclerView(@NonNull final Viewholder holder, String key) {
        try {
            // Stop previous adapter if exists
            if (holder.recyclerView.getAdapter() instanceof TempChildAdapter) {
                ((TempChildAdapter) holder.recyclerView.getAdapter()).stopListening();
            }

            // Configure child RecyclerView
            GridLayoutManager layoutManager = new GridLayoutManager(context, 4);
            holder.recyclerView.setLayoutManager(layoutManager);
            holder.recyclerView.setItemAnimator(null);

            // Create options for child adapter
            FirebaseRecyclerOptions<TempChildModel> childOptions = new FirebaseRecyclerOptions.Builder<TempChildModel>()
                    .setQuery(
                            FirebaseDatabase.getInstance()
                                    .getReference("HomeItems")
                                    .child(key)
                                    .child("items")
                                    .limitToFirst(8),
                            TempChildModel.class
                    )
                    .build();

            // Create and set child adapter
            TempChildAdapter childAdapter = new TempChildAdapter(childOptions, context);
            holder.recyclerView.setAdapter(childAdapter);
            childAdapter.startListening();

            // Store reference to child adapter
            childAdapters.put(key, childAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewRecycled(@NonNull Viewholder holder) {
        super.onViewRecycled(holder);
        try {
            if (holder.recyclerView.getAdapter() instanceof TempChildAdapter) {
                ((TempChildAdapter) holder.recyclerView.getAdapter()).stopListening();
            }
            holder.recyclerView.setAdapter(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopListening() {
        super.stopListening();
        for (TempChildAdapter adapter : childAdapters.values()) {
            if (adapter != null) {
                adapter.stopListening();
            }
        }
        childAdapters.clear();
    }

    static class Viewholder extends RecyclerView.ViewHolder {
        TextView label, sellAllLebel;
        RecyclerView recyclerView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            sellAllLebel = itemView.findViewById(R.id.sellAllLebel);
        }
    }
}