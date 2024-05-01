package com.example.catcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.widget.Toast;

public class HomeFragment extends Fragment {

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



        return view;
    }


    private void displayCurrentDate() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        String tanggal = simpleDateFormat.format(calendar.getTime());
        dateTextView.setText(tanggal);
    }
}
