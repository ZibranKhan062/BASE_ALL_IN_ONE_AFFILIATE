package com.devapps.affiliateadmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.devapps.affiliateadmin.models.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.FirebaseDatabase;

// AdminDashboardActivity.java
public class AdminDashboardActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
//    private FloatingActionButton fabAdd;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupViewPager();
        setupListeners();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
//        fabAdd = findViewById(R.id.fabAdd);
        searchView = findViewById(R.id.searchView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }
    }

    private void setupViewPager() {
        AdminPagerAdapter pagerAdapter = new AdminPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupListeners() {
//        fabAdd.setOnClickListener(v -> {
//            int currentTab = viewPager.getCurrentItem();
//            if (currentTab == 0) {
//                showAddCategoryDialog();
//            } else {
////                startActivity(new Intent(this, AddDealActivity.class));
//            }
//        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Fragment currentFragment = ((AdminPagerAdapter) viewPager.getAdapter())
                        .getItem(viewPager.getCurrentItem());
                if (currentFragment instanceof SearchableFragment) {
                    ((SearchableFragment) currentFragment).onSearch(newText);
                }
                return true;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshCurrentTab();
        });

        // Add page change listener for FAB visibility
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                fabAdd.show(); // Always show FAB when page changes
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void showAddCategoryDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        EditText etName = dialogView.findViewById(R.id.etCategoryName);
        EditText etDescription = dialogView.findViewById(R.id.etCategoryDescription);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Category")
                .setView(dialogView)
                .setPositiveButton("Add", null) // Set to null initially
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String name = etName.getText().toString().trim();
                String description = etDescription.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    etName.setError("Name is required");
                    return;
                }

                addNewCategory(name, description);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void addNewCategory(String name, String description) {
        String id = name.toLowerCase().replace(" ", "_");
        Category category = new Category(id, name, description, "");

        FirebaseDatabase.getInstance().getReference()
                .child("categories")
                .child(id)
                .setValue(category)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void refreshCurrentTab() {
        Fragment currentFragment = ((AdminPagerAdapter) viewPager.getAdapter())
                .getItem(viewPager.getCurrentItem());
        if (currentFragment instanceof RefreshableFragment) {
            ((RefreshableFragment) currentFragment).onRefresh();
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}

// AdminPagerAdapter.java
class AdminPagerAdapter extends FragmentPagerAdapter {
    private final Fragment[] fragments;
    private final String[] titles = new String[]{"Categories", "Deals/News"};

    public AdminPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragments = new Fragment[]{
                new CategoriesFragment(),
                new DealsFragment()
        };
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}

// SearchableFragment.java
interface SearchableFragment {
    void onSearch(String query);
}

// RefreshableFragment.java
interface RefreshableFragment {
    void onRefresh();
}