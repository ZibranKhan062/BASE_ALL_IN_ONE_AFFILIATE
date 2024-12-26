package com.devapps.affiliateadmin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devapps.affiliateadmin.AllProductsListings;
import com.devapps.affiliateadmin.Config;
import com.devapps.affiliateadmin.R;
import com.devapps.affiliateadmin.ShoppingListings;
import com.devapps.affiliateadmin.models.CategoryModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.HashMap;
import java.util.Map;

public class ShopAdapter extends FirebaseRecyclerAdapter<CategoryModel, ShopAdapter.Viewholder> {
    private Context context;
    private int itemCount = 0;

    public ShopAdapter(@NonNull FirebaseRecyclerOptions<CategoryModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        try {
            itemCount = getItemCount();
            if (context instanceof ShoppingListings) {
                ProgressBar progressBar = ((ShoppingListings) context).findViewById(R.id.progressBar);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull final Viewholder holder, final int position, @NonNull final CategoryModel model) {
        if (position >= itemCount) {
            return;
        }

        try {
            final int stablePosition = holder.getBindingAdapterPosition();
            if (stablePosition == RecyclerView.NO_POSITION) {
                return;
            }

            if (context != null && !((Activity) context).isDestroyed()) {
                Glide.with(context)
                        .load(model.getImage())
                        .centerCrop()
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.item_image);
            }

            holder.item_name.setText(model.getTitle() != null ? model.getTitle() : "");

            holder.shopItemCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentPosition = holder.getBindingAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION && currentPosition < itemCount) {
                        Intent intent = new Intent(context, AllProductsListings.class);
                        intent.putExtra("CurrentPosition", getRef(currentPosition).getKey());
                        intent.putExtra("itemName", holder.item_name.getText().toString().trim());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });

            holder.editbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPosition = holder.getBindingAdapterPosition();
                    if (currentPosition == RecyclerView.NO_POSITION || currentPosition >= itemCount) {
                        return;
                    }

                    final DialogPlus dialog = DialogPlus.newDialog(context)
                            .setGravity(Gravity.CENTER)
                            .setMargin(50, 20, 50, 20)
                            .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.update_shopping_item_content))
                            .setExpanded(false)
                            .create();

                    View holderView = dialog.getHolderView();
                    final EditText category_name = holderView.findViewById(R.id.category_name);
                    final EditText category_image = holderView.findViewById(R.id.category_image);

                    category_name.setText(model.getTitle());
                    category_image.setText(model.getImage());
                    dialog.show();

                    Button update_category = holderView.findViewById(R.id.update_category);
                    update_category.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (Config.isdemoEnabled) {
                                Toast.makeText(context, "Operation not allowed in Demo App", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String newName = category_name.getText().toString().trim();
                            String newImage = category_image.getText().toString().trim();

                            if (newName.isEmpty() || newImage.isEmpty()) {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Map<String, Object> map = new HashMap<>();
                            map.put("title", newName);
                            map.put("image", newImage);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("ShopCategories")
                                    .child(getRef(currentPosition).getKey())
                                    .updateChildren(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Category Successfully Updated!", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(context, "Update failed: " + task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPosition = holder.getBindingAdapterPosition();
                    if (currentPosition == RecyclerView.NO_POSITION || currentPosition >= itemCount) {
                        return;
                    }

                    new AlertDialog.Builder(context)
                            .setTitle("Delete Item")
                            .setMessage("Are you sure you want to delete this Item?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Config.isdemoEnabled) {
                                        Toast.makeText(context, "Operation not allowed in Demo App", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("ShopCategories")
                                            .child(getRef(currentPosition).getKey())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(context, "Delete failed: " + task.getException().getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });

        } catch (Exception e) {
            Log.e("ShopAdapter", "Error in onBindViewHolder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_recycler_items, parent, false);
        return new Viewholder(v);
    }

    static class Viewholder extends RecyclerView.ViewHolder {
        ImageView item_image, editbutton, deleteButton;
        TextView item_name;
        CardView shopItemCard;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_name = itemView.findViewById(R.id.item_name);
            shopItemCard = itemView.findViewById(R.id.shopItemCard);
            editbutton = itemView.findViewById(R.id.editbutton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public void onError(@NonNull DatabaseError error) {
        super.onError(error);
        Log.e("FirebaseError", "Database error: " + error.getMessage());
    }
}