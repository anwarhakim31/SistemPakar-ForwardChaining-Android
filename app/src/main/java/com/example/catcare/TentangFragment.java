package com.example.catcare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.util.DisplayMetrics;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.util.Log;
public class TentangFragment extends Fragment {

    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tentang, container, false);

        ImageView imageView = view.findViewById(R.id.gambar);
        CardView cardView = view.findViewById(R.id.cardView);

        // Mendapatkan tinggi layar perangkat dalam Density-independent Pixel (dp)
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenHeightDp = Math.round(displayMetrics.heightPixels / displayMetrics.density);

        // Mengubah visibilitas gambar berdasarkan tinggi layar perangkat
        if (screenHeightDp > 900) {
            imageView.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
            layoutParams.setMargins(0, 32, 0, 0);
            cardView.setLayoutParams(layoutParams);
        } else {
            imageView.setVisibility(View.GONE);
            int marginInDp = 10;
            int marginInPixel = (int) (marginInDp * displayMetrics.density);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
            layoutParams.setMargins(marginInPixel, marginInPixel, marginInPixel, marginInPixel);
            cardView.setLayoutParams(layoutParams);
        }

        return view;
    }

}
