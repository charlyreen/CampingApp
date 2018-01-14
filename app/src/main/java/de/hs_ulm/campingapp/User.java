package de.hs_ulm.campingapp;

import android.renderscript.Sampler;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Rene on 14.01.2018.
 */

public class User {
    private boolean admin;
    private String email;
    private String name;

    public boolean isAdmin() { return admin; }
    public String getEmail() { return email; }
    public String getName() { return name; }

    public User() {

    }

    public User(String email_, String name_) {
        admin = false;
        email = email_;
        name = name_;
    }

}
