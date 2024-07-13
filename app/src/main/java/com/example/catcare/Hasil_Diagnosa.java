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


        Log.d("TAG", tanggal);

        String str_hasil = getIntent().getStringExtra("HASIL");
        String username = getIntent().getStringExtra("username");
        String email = getIntent().getStringExtra("email");







        String[] gejala_terpilih = str_hasil != null ? str_hasil.split("#") : new String[0];

        double cf_gabungan;
        double cf;
        HashMap<String, Double> mapHasil = new HashMap<>();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        sqLiteDatabase = dbHelper.getReadableDatabase();


        String query_penyakit = "SELECT id_penyakit FROM penyakit order by id_penyakit";
        Cursor cursor_penyakit = sqLiteDatabase.rawQuery(query_penyakit, null);
        while (cursor_penyakit.moveToNext()) {
            cf_gabungan = (double) 0;
            int i = 0;

            String query_rule = "SELECT nilai_cf, id_gejala FROM rules where id_penyakit = '" + cursor_penyakit.getString(0) + "'";
            Cursor cursor_rule = sqLiteDatabase.rawQuery(query_rule, null);
            while (cursor_rule.moveToNext()) {
                cf = cursor_rule.getDouble(0);
                for (String s_gejala_terpilih : gejala_terpilih) {
                    String query_gejala = "SELECT id_gejala FROM gejala where nama_gejala = '" + s_gejala_terpilih + "'";
                    Cursor cursor_gejala = sqLiteDatabase.rawQuery(query_gejala, null);


                    cursor_gejala.moveToFirst();

                    if (cursor_gejala.moveToFirst()) {

                        if (cursor_rule.getString(1).equals(cursor_gejala.getString(0))) {
                            if (i > 1) {
                                cf_gabungan = cf + (cf_gabungan * (1 - cf));
                            } else if (i == 1) {
                                cf_gabungan = cf_gabungan + (cf * (1 - cf_gabungan));
                            } else {
                                cf_gabungan = cf;
                            }
                            i++;
                        }
                        cursor_gejala.close();
                    } else {

                        Log.e("CursorEmpty", "Cursor_gejala is empty");
                    }
                }

            }
            cursor_rule.close();
            mapHasil.put(cursor_penyakit.getString(0), cf_gabungan * 100);

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

                        if(username!=null && email != null){
                            riwayatRef.child(email).push().setValue(riwayatData);
                        } else if (username!=null && email ==null) {
                            riwayatRef.child(username).push().setValue(riwayatData);
                        }



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

                DiagnosisFragment diagnosisFragment = new DiagnosisFragment();
                Bundle args = new Bundle();
                args.putString("username", username);
                if(email!=null){

                args.putString("email", email);
                }
                diagnosisFragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.diagnosa, diagnosisFragment)
                        .commit();

            }
        });

        btnBeranda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Buat objek fragment yang akan dituju
                Intent masuk = new Intent(getApplicationContext(), MainActivity.class);
                masuk.putExtra("username", username);
                if(email!=null){
                    masuk.putExtra("email", email);
                }
                startActivity(masuk);
                finish();
            }
        });
    }

    public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm) {
        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(hm.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
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