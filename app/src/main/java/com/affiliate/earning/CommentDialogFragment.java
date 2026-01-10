package com.affiliate.affiliate;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CommentDialogFragment extends DialogFragment {
    private EditText etComment;
    private CommentListener listener;

    public interface CommentListener {
        void onCommentSubmitted(String comment);
    }

    public void setCommentListener(CommentListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_comment, null);
        etComment = view.findViewById(R.id.etComment);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Comment")
                .setView(view)
                .setPositiveButton("Post", (dialog, which) -> {
                    String comment = etComment.getText().toString().trim();
                    if (!comment.isEmpty() && listener != null) {
                        listener.onCommentSubmitted(comment);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }
}