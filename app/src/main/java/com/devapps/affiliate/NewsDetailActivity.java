package com.devapps.affiliate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devapps.affiliate.LoginSignup.LoginActivity;
import com.devapps.affiliate.models.Comment;
import com.devapps.affiliate.models.NewsItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewsDetailActivity extends AppCompatActivity {
    private NewsItem newsItem;
    private DatabaseReference newsRef;
    private DatabaseReference userLikesRef;
    private DatabaseReference userBookmarksRef;
    private DatabaseReference newsInteractionsRef;
    private FirebaseAuth auth;
    private ValueEventListener likesListener;
    private ValueEventListener bookmarksListener;
    private ValueEventListener interactionsListener;

    private ImageView ivNewsImage;
    private TextView tvCategory;
    private TextView tvTitle;
    private TextView tvTimestamp;
    private TextView tvContent;
    private TextView tvLikesCount;
    private TextView tvCommentsCount;
    private TextView tvBookmarksCount;
    private ImageButton btnLike;
    private ImageButton btnComment;
    private ImageButton btnBookmark;
    private FloatingActionButton fabShare;
    private RecyclerView rvComments;
    private CommentsAdapter commentsAdapter;
    private ProgressBar progressBar;
    private DatabaseReference likesRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        newsItem = getIntent().getParcelableExtra("news_item");

        if (newsItem != null) {
            Log.d("NewsDetailActivity", "Received news item: " + newsItem.getId());
            Log.d("NewsDetailActivity", "Initial like count: " + newsItem.getLikeCount());
            Log.d("NewsDetailActivity", "Initial is liked status: " + newsItem.isLiked());

            newsRef = database.getReference("news").child(newsItem.getId());
            newsInteractionsRef = database.getReference("news_interactions").child(newsItem.getId());

            if (auth.getCurrentUser() != null) {
                String userId = auth.getCurrentUser().getUid();
                userLikesRef = database.getReference("user_interactions")
                        .child(userId)
                        .child("likedPosts")
                        .child(newsItem.getId());

                userBookmarksRef = database.getReference("user_interactions")
                        .child(userId)
                        .child("bookmarkedPosts")
                        .child(newsItem.getId());
            }
        }

        initViews();
        setupToolbar();
        loadNewsDetails();
        setupListeners();
        setupInteractionsListener();
    }

    private void initViews() {
        ivNewsImage = findViewById(R.id.ivNewsImage);
        tvCategory = findViewById(R.id.tvCategory);
        tvTitle = findViewById(R.id.tvTitle);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        tvContent = findViewById(R.id.tvContent);
        tvLikesCount = findViewById(R.id.tvLikesCount);
        tvCommentsCount = findViewById(R.id.tvCommentsCount);
//        tvBookmarksCount = findViewById(R.id.tvBookmarksCount);
        btnLike = findViewById(R.id.btnLike);
        btnComment = findViewById(R.id.btnComment);
        btnBookmark = findViewById(R.id.btnBookmark);
        fabShare = findViewById(R.id.fabShare);
        rvComments = findViewById(R.id.rvComments);
        progressBar = findViewById(R.id.progressBar);

        commentsAdapter = new CommentsAdapter();
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentsAdapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadNewsDetails() {
        if (newsItem != null) {
            Glide.with(this)
                    .load(newsItem.getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(ivNewsImage);

            tvCategory.setText(newsItem.getCategory());
            tvTitle.setText(newsItem.getTitle());
            tvTimestamp.setText(formatDate(newsItem.getTimestamp()));
            tvContent.setText(newsItem.getDescription());

            // Set initial like count and state
            tvLikesCount.setText(String.valueOf(newsItem.getLikeCount()));
            updateLikeButton(newsItem.isLiked());

            // Set initial bookmark state
            updateBookmarkButton(newsItem.isBookmarked());

            loadComments();
        }
    }

    private void setupListeners() {
        btnLike.setOnClickListener(v -> handleLike());
        btnComment.setOnClickListener(v -> showCommentDialog());
        btnBookmark.setOnClickListener(v -> handleBookmark());
        fabShare.setOnClickListener(v -> shareNews());

        if (auth.getCurrentUser() != null) {
            setupUserInteractionListeners();
        }
    }

    private void setupInteractionsListener() {
        Log.d("NewsDetailActivity", "Setting up interactions listener");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        likesRef = database.getReference("likes");
        // Change to use the same likes reference as NewsFragment
        likesRef.child(newsItem.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int likes = (int) snapshot.getChildrenCount();  // Count children like in NewsFragment
                Log.d("NewsDetailActivity", "Interaction update - likes: " + likes);

                tvLikesCount.setText(String.valueOf(likes));
                newsItem.setLikeCount(likes);

                // Update the like button state
                if (auth.getCurrentUser() != null) {
                    String userId = auth.getCurrentUser().getUid();
                    boolean isLiked = snapshot.hasChild(userId);
                    updateLikeButton(isLiked);
                    newsItem.setLiked(isLiked);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NewsDetailActivity Error",""+ error);
                showToast("Failed to load interaction counts");
            }
        });
    }

    private void setupUserInteractionListeners() {
        likesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isLiked = snapshot.exists();
                updateLikeButton(isLiked);
                newsItem.setLiked(isLiked);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Error checking like status");
            }
        };
        userLikesRef.addValueEventListener(likesListener);

        bookmarksListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isBookmarked = snapshot.exists();
                updateBookmarkButton(isBookmarked);
                newsItem.setBookmarked(isBookmarked);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Error checking bookmark status");
            }
        };
        userBookmarksRef.addValueEventListener(bookmarksListener);
    }

    private void handleLike() {
        if (auth.getCurrentUser() == null) {
            showLoginPrompt();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userLikeRef = likesRef.child(newsItem.getId()).child(userId);

        userLikeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userLikeRef.removeValue();
                    newsItem.setLiked(false);
                } else {
                    userLikeRef.setValue(true);
                    newsItem.setLiked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Failed to update like");
            }
        });
    }

    private void handleBookmark() {
        if (auth.getCurrentUser() == null) {
            showLoginPrompt();
            return;
        }

        userBookmarksRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Object value = mutableData.getValue();
                if (value == null) {
                    mutableData.setValue(ServerValue.TIMESTAMP);
                    newsInteractionsRef.child("bookmarks").child("count").setValue(ServerValue.increment(1));
                } else {
                    mutableData.setValue(null);
                    newsInteractionsRef.child("bookmarks").child("count").setValue(ServerValue.increment(-1));
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (!committed) {
                    showToast("Failed to update bookmark");
                }
            }
        });
    }

    private void showCommentDialog() {
        if (auth.getCurrentUser() == null) {
            showLoginPrompt();
            return;
        }

        CommentDialogFragment dialog = new CommentDialogFragment();
        dialog.setCommentListener(commentText -> {
            String commentId = newsRef.child("comments").push().getKey();
            if (commentId != null) {
                String userId = auth.getCurrentUser().getUid();

                // Create comment using the existing constructor
                Comment newComment = new Comment(
                        commentId,
                        userId,
                        commentText,
                        System.currentTimeMillis()
                );

                // Set the userName separately
                newComment.setUserName(auth.getCurrentUser().getDisplayName());

                Map<String, Object> updates = new HashMap<>();
                // Add comment to news comments
                updates.put("/news/" + newsItem.getId() + "/comments/" + commentId, newComment);
                // Increment comment count
                updates.put("/news_interactions/" + newsItem.getId() + "/comments/count",
                        ServerValue.increment(1));
                // Add to user's commented posts
                updates.put("/user_interactions/" + userId + "/commentedPosts/" +
                        newsItem.getId() + "/" + commentId, ServerValue.TIMESTAMP);

                FirebaseDatabase.getInstance().getReference().updateChildren(updates)
                        .addOnSuccessListener(unused -> {
                            loadComments();
                            showToast("Comment posted successfully");
                        })
                        .addOnFailureListener(e -> showToast("Failed to post comment"));
            }
        });
        dialog.show(getSupportFragmentManager(), "CommentDialog");
    }

    private void loadComments() {
        newsRef.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Comment> comments = new ArrayList<>();
                for (DataSnapshot commentSnap : snapshot.getChildren()) {
                    Comment comment = commentSnap.getValue(Comment.class);
                    if (comment != null) {
                        comments.add(comment);
                    }
                }
                comments.sort((c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));
                commentsAdapter.setComments(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Failed to load comments");
            }
        });
    }

    private void shareNews() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, newsItem.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                newsItem.getTitle() + "\n\nRead more at: " + getString(R.string.app_name));
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void showLoginPrompt() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Login Required")
                .setMessage("Please login to access this feature")
                .setPositiveButton("Login", (dialog, which) -> {
                    startActivity(new Intent(this, LoginActivity.class));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int getCount(DataSnapshot snapshot) {
        return snapshot.exists() ? ((Long) snapshot.getValue()).intValue() : 0;
    }

    private void updateLikeButton(boolean isLiked) {
        btnLike.setImageResource(isLiked ?
                R.drawable.fav : R.drawable.baseline_favorite_border_24_new);
    }

    private void updateBookmarkButton(boolean isBookmarked) {
        btnBookmark.setImageResource(isBookmarked ?
                R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark);
    }

    private String formatDate(long timestamp) {
        return new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                .format(new Date(timestamp));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (auth.getCurrentUser() != null) {
            if (likesListener != null) {
                userLikesRef.removeEventListener(likesListener);
            }
            if (bookmarksListener != null) {
                userBookmarksRef.removeEventListener(bookmarksListener);
            }
            if (interactionsListener != null) {
                newsInteractionsRef.removeEventListener(interactionsListener);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}