package com.example.catcare;

import android.os.Bundle;

import android.content.Intent;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Diagnosa extends AppCompatActivity {
    private CheckBox demamCheckBox, pusingCheckBox, pilekCheckBox;
    private Button prosesButton;
    private TextView textView,profileName;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.diagnosa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.diagnosa), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        demamCheckBox = findViewById(R.id.demam);
        pilekCheckBox = findViewById(R.id.pilek);
        pusingCheckBox = findViewById(R.id.pusing);
        prosesButton = findViewById(R.id.prosesButton);
        textView = findViewById(R.id.textView);

        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://catcare-736ed-default-rtdb.firebaseio.com/");
        profileName = findViewById(R.id.profileName);

        // Mengambil username dari Intent
        String username = getIntent().getStringExtra("username");

        // Menampilkan username di TextView
        profileName.setText("Welcome, " + username);

        textView.setText("");

        prosesButton.setOnClickListener(new View.OnClickListener() {
            String namapenyakit = "";

            @Override
            public void onClick(View v) {

                namapenyakit += "";
                // Cek apakah checkbox demam dicentang
                if (demamCheckBox.isChecked() && pilekCheckBox.isChecked() ) {
                    namapenyakit = "Scabies";
                }

                if(pusingCheckBox.isChecked()){
                    namapenyakit= "Phyoderma";
                }

                database = FirebaseDatabase.getInstance().getReference("Riwayat");
                database.child(username).child("username").setValue(username);
                database.child(username).child("penyakit").setValue(namapenyakit);

                Intent hasil = new Intent(getApplicationContext(), Hasil_Diagnosa.class);
//                hasil.putExtra("username", username);
                hasil.putExtra("nama_penyakit", namapenyakit);
                startActivity(hasil);
            }
        });
    }

    // Metode yang dipanggil ketika checkbox diklik

    public void onBackPressed() {
        // Biarkan tombol back tidak melakukan apa-apa di layar MainMenu
    }
}