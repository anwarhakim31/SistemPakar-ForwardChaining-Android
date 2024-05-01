package com.example.catcare;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Hasil_Diagnosa extends AppCompatActivity {

    private DatabaseReference database;
// ...


    private TextView penyakit,solusi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.hasil_diagnosa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        solusi = findViewById(R.id.solusi);
        penyakit = findViewById(R.id.hasil_penyakit);

        // Referensi ke node "penyakit"
        database = FirebaseDatabase.getInstance().getReference("penyakit");

        // Menambahkan listener untuk mendengarkan perubahan data di node "penyakit"
        database.addValueEventListener(new ValueEventListener() {
            String namaPenyakit = getIntent().getStringExtra("nama_penyakit");

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterasi melalui semua child di node "penyakit"
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Mendapatkan nama penyakit dari setiap child
                    String child = snapshot.getKey();
                    // Mendapatkan nilai solusi dari child sesuai dengan namaPenyakit
                    if (child.equals(namaPenyakit)) {
                        String Dsolusi = snapshot.child("solusi").getValue(String.class);
                        // Tetapkan nilai solusi ke TextView penyakit
                        solusi.setText(Dsolusi);
                        penyakit.setText(namaPenyakit);
                        // Keluar dari loop karena sudah mendapatkan solusi untuk Phyoderma
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle kegagalan dalam membaca data
                Log.e("MainActivity", "Gagal membaca data penyakit: " + databaseError.getMessage());
            }
        });
        // Mengambil username dari Intent

// Mengambil username dari Intent

// Menampilkan username di TextView penyakit



    }
}