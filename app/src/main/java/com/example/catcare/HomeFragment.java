package com.example.catcare;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import com.bumptech.glide.Glide;
import com.example.database.DatabaseHelper; // Sesuaikan dengan path package Anda
import com.example.model.ModelDaftarPenyakit;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {
    private DatabaseHelper databaseHelper;
    private TextView dateTextView,name;
    private Calendar calendar;

    private ImageView profile;

    private String foto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dateTextView = view.findViewById(R.id.tanggal);
        name = view.findViewById(R.id.name);

        profile = view.findViewById(R.id.profile_image_view);
        calendar = Calendar.getInstance();


        displayCurrentDate();

        String username = getActivity().getIntent().getStringExtra("username");
         foto = getActivity().getIntent().getStringExtra("foto");


        if (foto != null) {
            Log.d("foto", foto);

            Glide.with(this).load(foto).into(profile);
        }
        if (username == null) {
            SharedPreferences.Editor editor = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            FirebaseAuth.getInstance().signOut();

            Intent logoutIntent = new Intent(requireContext(), Login.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            requireActivity().finish();
        }
   name.setText(username);

        databaseHelper = new DatabaseHelper(getActivity());

        try {

            databaseHelper.openDatabase();

            if (databaseHelper.isConnected()) {
                Log.d("DatabaseStatus", "Connected to database");
            } else {

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
