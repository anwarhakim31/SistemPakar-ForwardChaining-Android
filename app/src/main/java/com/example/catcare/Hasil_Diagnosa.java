package com.example.catcare;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.util.Log;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.TypedValue;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;

import com.google.android.material.button.MaterialButton;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.example.database.DatabaseHelper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import java.util.ArrayList;


import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import com.example.catcare.HomeFragment;

public class Hasil_Diagnosa extends AppCompatActivity {

    private DatabaseReference database;
    private TextView containerDataDiterima;
    private TextView defenisiHeader;
    private TextView defenisi;
    private TextView solusiHeader;
    private TextView solusi;
    private TextView tvNamaPenyakit;

    private Calendar calendar;
    SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.hasil_diagnosa);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.diagnosa), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.navbar);
        setSupportActionBar(toolbar);


        LinearLayout btnBeranda = findViewById(R.id.btnBeranda);
        LinearLayout btnDiagnosis = findViewById(R.id.btnDiagnosis);
        containerDataDiterima = findViewById(R.id.dataDeterima);
        tvNamaPenyakit = findViewById(R.id.hd);
        defenisiHeader = findViewById(R.id.defenisiHeader);
        solusiHeader = findViewById(R.id.solusiHeader);
        defenisi = findViewById(R.id.defenisi);
        solusi = findViewById(R.id.solusi);
        ImageView imageView = findViewById(R.id.image);


        calendar = Calendar.getInstance();


        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());


        String tanggal = simpleDateFormat.format(calendar.getTime());


        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        if (databaseHelper.openDatabase())
            sqLiteDatabase = databaseHelper.getReadableDatabase();

        String str_hasil = getIntent().getStringExtra("HASIL");
        String username = getIntent().getStringExtra("username");

        String[] gejala_terpilih = new String[0];
        if (str_hasil != null) {
            gejala_terpilih = str_hasil.split("#");
        }

        double cf_gabungan;
        double cf;
        HashMap<String, Double> mapHasil = new HashMap<>();

        String query_penyakit = "SELECT id_penyakit FROM penyakit ORDER BY id_penyakit";
        Cursor cursor_penyakit = sqLiteDatabase.rawQuery(query_penyakit, null);
        while (cursor_penyakit.moveToNext()) {
            String id_penyakit = cursor_penyakit.getString(0);

            String query_gejala = "SELECT id_gejala FROM rules WHERE id_penyakit = '" + id_penyakit + "'";
            Cursor cursor_gejala = sqLiteDatabase.rawQuery(query_gejala, null);

            int total_gejala = cursor_gejala.getCount();
            int matched_gejala = 0;

            while (cursor_gejala.moveToNext()) {
                String id_gejala = cursor_gejala.getString(0);
                for (String gejala : gejala_terpilih) {
                    String query_gejala_terpilih = "SELECT id_gejala FROM gejala WHERE nama_gejala = '" + gejala + "'";
                    Cursor cursor_gejala_terpilih = sqLiteDatabase.rawQuery(query_gejala_terpilih, null);
                    cursor_gejala_terpilih.moveToFirst();

                    if (cursor_gejala_terpilih.getString(0).equals(id_gejala)) {
                        matched_gejala++;
                    }
                    cursor_gejala_terpilih.close();
                }
            }
            cursor_gejala.close();

            double probability = ((double) matched_gejala / total_gejala) * 100;
            mapHasil.put(id_penyakit, probability);
        }
        cursor_penyakit.close();


        StringBuffer output_gejala_terpilih = new StringBuffer();
        int no = 1;
        for (String s_gejala_terpilih : gejala_terpilih) {
            output_gejala_terpilih.append(no++)
                    .append(". ")
                    .append(s_gejala_terpilih)
                    .append("\n");
        }

        containerDataDiterima.setText(output_gejala_terpilih);

        Map<String, Double> sortedHasil = sortByValue(mapHasil);

        Map.Entry<String, Double> entry = sortedHasil.entrySet().iterator().next();
        String kode_penyakit = entry.getKey();
        double hasil_cf = entry.getValue();
        int persentase = (int) hasil_cf;
        Log.d("TAG", String.valueOf(hasil_cf));

        String query_penyakit_hasil = "SELECT nama_penyakit FROM penyakit where id_penyakit='" + kode_penyakit + "'";
        Cursor cursor_hasil = sqLiteDatabase.rawQuery(query_penyakit_hasil, null);
        cursor_hasil.moveToFirst();


        String namaPenyakit = cursor_hasil.getString(0);


        String coloredNamaPenyakit = "<font color='#007cfd' ><b>" + namaPenyakit + "</b></font>";
        String persen = "<font color='#007cfd' ><b>" + persentase + "</b></font>";

        tvNamaPenyakit.setText(Html.fromHtml("Kucing " + username + " kemungkinan menderita " + coloredNamaPenyakit + " dengan tingkat kepastian " + persen + "%"));

        cursor_hasil.close();

        DatabaseReference penyakitRef = FirebaseDatabase.getInstance().getReference("penyakit");



        penyakitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String np = snapshot.child("nama_penyakit").getValue(String.class);
                    String imageURL = snapshot.child("imageURL").getValue(String.class);
                    String penjelasan = snapshot.child("penjelasan").getValue(String.class);
                    String solution = snapshot.child("solusi").getValue(String.class);
                    defenisiHeader.setText("Apa itu " + namaPenyakit);
                    solusiHeader.setText("Solusi dari " + namaPenyakit);

                    // Membandingkan nama penyakit dengan namaPenyakit
                    if (np.equals(namaPenyakit)) {
                        Glide.with(Hasil_Diagnosa.this)
                                .load(imageURL)
                                .into(imageView);
                    defenisi.setText(penjelasan);
                    solusi.setText(solution);

                        DatabaseReference riwayatRef = FirebaseDatabase.getInstance().getReference("Riwayat");

                        Map<String, Object> riwayatData = new HashMap<>();
                        riwayatData.put("penyakit", namaPenyakit);
                        riwayatData.put("persentase", persentase);
                        riwayatData.put("tanggal", tanggal);
                        riwayatData.put("hasil", str_hasil);


                        riwayatRef.child(username).push().setValue(riwayatData);
                        break;
                    }
                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }

        });


        btnDiagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Hasil_Diagnosa.this, MainActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("navigateTo", "diagnosis");
                startActivity(intent);
                finish();
            }
        });
        btnBeranda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Buat objek fragment yang akan dituju
                Intent masuk = new Intent(getApplicationContext(), MainActivity.class);
                masuk.putExtra("username", username);
                startActivity(masuk);
                finish();
            }
        });
    }


    private static Map<String, Double> sortByValue(Map<String, Double> unsortMap) {
        List<Map.Entry<String, Double>> list = new LinkedList<>(unsortMap.entrySet());
        list.sort(Map.Entry.comparingByValue((o1, o2) -> o2.compareTo(o1)));
        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}