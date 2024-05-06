package com.example.catcare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.TypedValue;


import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PenyakitFragment extends Fragment {

    private LinearLayout cardContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_penyakit, container, false);

        cardContainer = view.findViewById(R.id.cardContainer);
        cardContainer.removeAllViews();

        TextView titleTextView = new TextView(requireContext());
        LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleLayoutParams.setMargins(0, 80, 0, 100); // Atur margin
        titleTextView.setLayoutParams(titleLayoutParams);
        titleTextView.setText("Daftar Penyakit");
        titleTextView.setTextSize(24);
        titleTextView.setTextColor(getResources().getColor(R.color.colorBlack));
        titleTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cardContainer.addView(titleTextView);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("penyakit");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String namaPenyakit = snapshot.child("nama_penyakit").getValue(String.class);
                    String imageURL = snapshot.child("imageURL").getValue(String.class);

                    if (imageURL != null && !imageURL.isEmpty()) {

                        CardView cardView = new CardView(requireContext());
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(50, 0, 50, 50); // Atur margin untuk setiap LinearLayout yang berisi CardView
                        cardView.setLayoutParams(layoutParams);
                        cardView.setRadius(15);
                        cardView.setCardElevation(4);


                        LinearLayout layout = new LinearLayout(requireContext());
                        layout.setOrientation(LinearLayout.VERTICAL);


                        ImageView imageView = new ImageView(requireContext());
                        int heightInDp = 180;
                        int heightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());
                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                heightInPx
                        );
                        imageView.setLayoutParams(layoutParams1);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Glide.with(requireContext()).load(imageURL).into(imageView);
                        layout.addView(imageView);


                        TextView textView = new TextView(requireContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(12, 12, 0, 15); // Atur margin
                        textView.setLayoutParams(params);
                        textView.setText(namaPenyakit);
                        textView.setTextSize(20);
                        textView.setTextColor(getResources().getColor(R.color.colorBlack));
                        layout.addView(textView);


                        cardView.addView(layout);


                        cardContainer.addView(cardView);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        return view;
    }
}
