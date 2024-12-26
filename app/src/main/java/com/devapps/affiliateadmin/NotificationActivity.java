package com.devapps.affiliateadmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.devapps.affiliateadmin.models.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {
    EditText edtTitle;
    EditText edtMessage;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey;
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    RelativeLayout rel1;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    private ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    Button btnShowAllNotif;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        serverKey = "key=" + getResources().getString(R.string.serverKey);
        edtTitle = findViewById(R.id.edtTitle);
        edtMessage = findViewById(R.id.edtMessage);
        Button btnSend = findViewById(R.id.btnSend);
        rel1 = findViewById(R.id.rel1);
        btnShowAllNotif = findViewById(R.id.btnShowAllNotif);
        databaseReference = FirebaseDatabase.getInstance().getReference("Notifications");

        progressDialog = new ProgressDialog(this);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String strDate = sdf.format(c.getTime());
        Log.e("dateTime", strDate);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.isdemoEnabled) {
                    Toast.makeText(NotificationActivity.this, "Operation not allowed in Demo App", Toast.LENGTH_LONG).show();
                    return;
                }

                TOPIC = "/topics/Appusers"; //topic has to match what the receiver subscribed to
                NOTIFICATION_TITLE = edtTitle.getText().toString();
                NOTIFICATION_MESSAGE = edtMessage.getText().toString();

                if (TextUtils.isEmpty(NOTIFICATION_TITLE)) {
                    Toast.makeText(NotificationActivity.this, "Please Enter a title", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (TextUtils.isEmpty(NOTIFICATION_MESSAGE)) {
                    Toast.makeText(NotificationActivity.this, "Please Enter a Message", Toast.LENGTH_SHORT).show();

                    return;
                }

                progressDialog.setMessage("Please Wait...");
                progressDialog.show();

                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();
                try {
                    notifcationBody.put("title", NOTIFICATION_TITLE);
                    notifcationBody.put("message", NOTIFICATION_MESSAGE);
//                    notifcationBody.put("image", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/220px-Image_created_with_a_mobile_phone.png");


                    notification.put("to", TOPIC);
                    notification.put("data", notifcationBody);


                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: " + e.getMessage());
                }
                sendNotification(notification);

            }
        });

        btnShowAllNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NotificationActivity.this, AllNotifications.class);
                startActivity(i);
            }
        });

    }


    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        addNotifToDb();
                        edtTitle.setText("");
                        edtMessage.setText("");

                        new AlertDialog.Builder(NotificationActivity.this)
                                .setTitle("Success")
                                .setMessage("Notification Sent Successfully")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(R.drawable.success_icon)
                                .show();
//
//                        Snackbar snackbar = Snackbar
//                                .make(rel1, "Notification Sent Successfully", Snackbar.LENGTH_LONG);
//                        snackbar.show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NotificationActivity.this, "error" + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                        Log.e("Error", error.getLocalizedMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                progressDialog.dismiss();
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void addNotifToDb() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String strDate = sdf.format(c.getTime());

        String getKey = databaseReference.push().getKey();
        NotificationModel notificationModel = new NotificationModel(edtTitle.getText().toString().trim(), edtMessage.getText().toString().trim(), strDate);
        databaseReference.child(getKey).setValue(notificationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e("Date added", "successfully");
                } else {
                    Log.e("Date adding failed", "failed");
                }

            }


        });

    }
}
