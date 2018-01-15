package de.hs_ulm.campingapp;

import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import javax.security.auth.callback.Callback;

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

    public void setAdmin(boolean admin_) { admin = admin_; }
    public void setEmail(String email_) { email = email_; }
    public void setName(String name_) { name = name_; }

    public User() {

    }

    public User(DatabaseReference mRootRef, String uid) {
        Task<User> tcs = this.getUser(mRootRef, uid);
        tcs.addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                admin = task.getResult().isAdmin();
                email = task.getResult().getEmail();
                name = task.getResult().getName();
            }
        });

    }


    public Task<User> getUser(DatabaseReference mRootRef, String uid) {
        final TaskCompletionSource<User> tcs = new TaskCompletionSource<>();
        mRootRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tcs.setResult(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tcs.setException(databaseError.toException());
            }
        });
        return tcs.getTask();
    }


    public User(String email_, String name_) {
        admin = false;
        email = email_;
        name = name_;
    }


}
