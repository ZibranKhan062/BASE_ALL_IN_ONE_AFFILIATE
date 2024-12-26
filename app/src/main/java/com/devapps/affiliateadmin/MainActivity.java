package com.devapps.affiliateadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.devapps.affiliateadmin.adapters.HomeAdapter;
import com.devapps.affiliateadmin.models.HomeModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    HomeAdapter homeAdapter;
    FloatingActionButton addHomeItem;
    FloatingActionButton notifActivity;
    FirebaseRecyclerOptions<HomeModel> options;

    CardView siteCategoriesCard;
    CardView dealsCard;
    CardView appCard;
    CardView bannerImage;
    CardView storeProduct;
    CardView storeBannerImage;
    CardView couponCodesCard;
    CardView videoCard;
    CardView adsCard;
    CardView notificationCard;
    CardView contactCard;
    CardView blogArticleCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        siteCategoriesCard = findViewById(R.id.siteCategoriesCard);
        dealsCard = findViewById(R.id.dealsCard);
        appCard = findViewById(R.id.appCard);
        bannerImage = findViewById(R.id.bannerImage);
        storeProduct = findViewById(R.id.storeProduct);
        storeBannerImage = findViewById(R.id.storeBannerImage);
        couponCodesCard = findViewById(R.id.couponCodesCard);
        videoCard = findViewById(R.id.videoCard);
        adsCard = findViewById(R.id.adsCard);
        notificationCard = findViewById(R.id.notificationCard);
        contactCard = findViewById(R.id.contactCard);
        blogArticleCard = findViewById(R.id.blogArticleCard);
        blogArticleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AdminDashboardActivity.class);
                startActivity(i);
            }
        });

        siteCategoriesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SiteCategories.class);
                startActivity(i);

            }
        });

        dealsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DealsOffers.class);
                startActivity(i);

            }
        });

        appCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AppListings.class);
                startActivity(i);

            }
        });
        bannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BannerImages.class);
                startActivity(intent);
            }
        });
        storeProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShoppingListings.class);
                startActivity(intent);
            }
        });
        storeBannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManageStoreBannerImages.class);
                startActivity(intent);
            }
        });
        couponCodesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CouponActivity.class);
                startActivity(intent);
            }
        });
        videoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                startActivity(intent);

            }
        });
        adsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdsActivity.class);
                startActivity(intent);

            }
        });
        notificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
        contactCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactMe.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        if (!isNetworkAvailable()) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("No Internet connection !")
                    .setMessage("Please check your Internet connection and try again")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .show();

        }


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//        addHomeItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addItem(FirebaseDatabase.getInstance().getReference("HomeItems"));
//            }
//        });
//        loadResources();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();


        if (id == R.id.nav_websiteCategories) {

            Intent intent = new Intent(MainActivity.this, SiteCategories.class);
            startActivity(intent);

        }

        if (id == R.id.nav_dealsOffers) {

            Intent intent = new Intent(MainActivity.this, DealsOffers.class);
            startActivity(intent);

        }


        if (id == R.id.nav_appListings) {

            Intent intent = new Intent(MainActivity.this, AppListings.class);
            startActivity(intent);

        } else if (id == R.id.nav_bannerImages) {
            Intent intent = new Intent(MainActivity.this, BannerImages.class);
            startActivity(intent);

        } else if (id == R.id.nav_shopping) {
            Intent intent = new Intent(MainActivity.this, ShoppingListings.class);
            startActivity(intent);


        } else if (id == R.id.nav_store_banner_images) {
            Intent intent = new Intent(MainActivity.this, ManageStoreBannerImages.class);
            startActivity(intent);


        } else if (id == R.id.nav_coupons) {
            Intent intent = new Intent(MainActivity.this, CouponActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_videos) {
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_ads) {
            Intent intent = new Intent(MainActivity.this, AdsActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_notifications) {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_contact) {
            String url = "https://themeforest.net/user/zibrangni";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        } else if (id == R.id.nav_send_message) {
            Intent intent = new Intent(MainActivity.this, ContactMe.class);
            startActivity(intent);

        }
        return true;
    }

    public void addItem(final DatabaseReference databaseReference1) {
        final DialogPlus dialog = DialogPlus.newDialog(MainActivity.this)
                .setGravity(Gravity.CENTER)
                .setMargin(50, 0, 50, 0)
                .setContentHolder(new ViewHolder(R.layout.add_home_items))
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        View holderView = dialog.getHolderView();
        dialog.show();

        final EditText Name = holderView.findViewById(R.id.name);
        final EditText Image = holderView.findViewById(R.id.imgLink);
        Button addItem = holderView.findViewById(R.id.add_home_item);
        final ImageView info = holderView.findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Maximize use of Free Database")
                        .setMessage("Upload your Hi res images to Free websites like postimages.org/imgbb.com and just paste the Image Link here. 512x512 px recommended")
                        .setNegativeButton("OK", null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });


        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getName = Name.getText().toString().trim();
                String getImage = Image.getText().toString().trim();


                if (TextUtils.isEmpty(getName) || TextUtils.isEmpty(getImage)

                ) {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Config.isdemoEnabled) {
                    Toast.makeText(MainActivity.this, "Operation not allowed in Demo App", Toast.LENGTH_LONG).show();
                    return;
                }

                AddHomeModel addHomeModel = new AddHomeModel(getName, getImage);
                databaseReference = databaseReference1;

                String getKey = databaseReference1.push().getKey();
                databaseReference1.child(getKey).setValue(addHomeModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Task Completed Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }


                });


            }
        });


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        homeAdapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        homeAdapter.stopListening();
//    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}