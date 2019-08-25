package com.example.publicservices.Database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Firebase {

    public FirebaseFirestore db = null;

    // --------- Firebase

    public Firebase(){
        if(db == null) db = FirebaseFirestore.getInstance();
    }

    public void saveDataLocation(Map<String, Object> data){
        db.collection("locations")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("lokasi_db", "Success Add ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("lokasi_db", "Error adding document", e);
                    }
                });

    }
}
