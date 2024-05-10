package com.example.catcare;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import android.widget.Toast;

import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.database.DatabaseHelper; // Sesuaikan dengan path package Anda
import com.example.model.ModelDaftarPenyakit;

public class HomeFragment extends Fragment {
    private DatabaseHelper databaseHelper;
    private TextView dateTextView,name;
    private Calendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dateTextView = view.findViewById(R.id.tanggal);
        name = view.findViewById(R.id.name);
        calendar = Calendar.getInstance();


        displayCurrentDate();
        String username = getActivity().getIntent().getStringExtra("username");
        name.setText(username);

        databaseHelper = new DatabaseHelper(getActivity());
        if (username == null) {
            SharedPreferences.Editor editor = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();


            Intent logoutIntent = new Intent(requireContext(), Login.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            requireActivity().finish();
        }
        try {
            // 2. Buka koneksi ke database
            databaseHelper.openDatabase();

            // 3. Periksa koneksi
            if (databaseHelper.isConnected()) {



                Log.d("DatabaseStatus", "Connected to database");
            } else {
                // Koneksi belum terbuka
                Log.d("DatabaseStatus", "Not connected to database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error opening database", Toast.LENGTH_SHORT).show();
        }


        return view;
    }


    private void displayCurrentDate() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        String tanggal = simpleDateFormat.format(calendar.getTime());
        dateTextView.setText(tanggal);
    }
}
