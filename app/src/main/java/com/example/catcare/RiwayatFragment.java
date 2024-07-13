package com.example.catcare;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.ImageView;

public class RiwayatFragment extends Fragment {
    private DatabaseReference database;
    boolean riwayatFound = false;
    private String[] Email;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riwayat, container, false);

        String username = getActivity().getIntent().getStringExtra("username");
        String email = getActivity().getIntent().getStringExtra("email");

        database = FirebaseDatabase.getInstance().getReference("Riwayat");


        if(email!=null){
            Email =  email != null ? email.split("@"):new String[0];
        }
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (isAdded()) { // Periksa apakah fragment sudah terpasang ke activity
                    LinearLayout cardContainer = view.findViewById(R.id.cardContainer);


                    cardContainer.removeAllViews();

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String dbusername = userSnapshot.getKey();
                        if (email != null && Email[0].equals(dbusername)) {
                            for (DataSnapshot riwayatSnapshot : userSnapshot.getChildren()) {


                                String penyakit = riwayatSnapshot.child("penyakit").getValue(String.class);
                                Double persentase = riwayatSnapshot.child("persentase").getValue(Double.class);
                                String tanggal = riwayatSnapshot.child("tanggal").getValue(String.class);


                                CardView cardView = createCardView(dbusername, penyakit, persentase, tanggal);
                                cardContainer.addView(cardView);
                                riwayatFound = true;
                            }
                            return;
                        } else if (username != null && username.equals(dbusername)) {
                            for (DataSnapshot riwayatSnapshot : userSnapshot.getChildren()) {
                                String pushId = riwayatSnapshot.getKey(); // Dapatkan push ID unik untuk setiap riwayat

                                String penyakit = riwayatSnapshot.child("penyakit").getValue(String.class);
                                Double persentase = riwayatSnapshot.child("persentase").getValue(Double.class);
                                String tanggal = riwayatSnapshot.child("tanggal").getValue(String.class);


                                CardView cardView = createCardView(dbusername, penyakit, persentase, tanggal);
                                cardContainer.addView(cardView);
                                riwayatFound = true;
                            }
                            return;
                        }
                    }

                    // Tampilkan layout "No Data" jika tidak ada riwayat yang ditemukan
                    if (!riwayatFound) {
                        cardContainer.removeAllViews(); // Remove any existing views
                        RelativeLayout noDataLayout = createNoDataLayout(); // Create the "No Data" layout
                        cardContainer.addView(noDataLayout); // Add the "No Data" layout to the card container
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle onCancelled event
            }
        });

        return view;
    }

    // Metode untuk membuat CardView
    private CardView createCardView(String username, String penyakit, Double persentase, String tanggal) {
        CardView cardView = new CardView(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.card_margin_bottom));
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(getResources().getDimension(R.dimen.card_corner_radius));
        cardView.setCardElevation(getResources().getDimension(R.dimen.card_elevation));

        LinearLayout linearLayout = new LinearLayout(requireContext());
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linearLayout.setPadding(
                getResources().getDimensionPixelSize(R.dimen.card_padding_horizontal),
                getResources().getDimensionPixelSize(R.dimen.card_padding_vertical),
                getResources().getDimensionPixelSize(R.dimen.card_padding_horizontal),
                getResources().getDimensionPixelSize(R.dimen.card_padding_vertical)
        );
        linearLayout.setLayoutParams(linearLayoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        addLabelValuePair(linearLayout, "Tanggal: ", tanggal);
        addLabelValuePair(linearLayout, "Username: ", username);
        addLabelValuePair(linearLayout, "Penyakit: ", penyakit);
        addLabelValuePair(linearLayout, "Kepastian: ", String.valueOf(persentase));

        cardView.addView(linearLayout);
        return cardView;
    }

    // Metode untuk membuat layout "No Data"
    private RelativeLayout createNoDataLayout() {
        RelativeLayout relativeLayout = new RelativeLayout(requireContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        relativeLayout.setLayoutParams(layoutParams);

        LinearLayout linearLayout = new LinearLayout(requireContext());
        RelativeLayout.LayoutParams linearLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        linearLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        linearLayout.setLayoutParams(linearLayoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(
                0,0,0,0
        );

        ImageView imageView = new ImageView(requireContext());
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        imageView.setLayoutParams(imageParams);
        imageView.setImageResource(R.drawable.no_data_icon);

        TextView textView = new TextView(requireContext());
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(textParams);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setText("Tidak ada riwayat");
        textView.setTextSize(getResources().getDimension(R.dimen.text_size));
        textView.setPadding(
                0,
                50,
                40,
                0
        );

        linearLayout.addView(imageView);
        linearLayout.addView(textView);

        relativeLayout.addView(linearLayout);
        return relativeLayout;
    }

    // Metode untuk menambahkan label dan nilainya ke layout
    private void addLabelValuePair(LinearLayout parentLayout, String label, String value) {
        LinearLayout pairLayout = new LinearLayout(requireContext());
        pairLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        pairLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView labelTextView = new TextView(requireContext());
        labelTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        labelTextView.setText(label);
        labelTextView.setTypeface(null, Typeface.BOLD);
        pairLayout.addView(labelTextView);

        TextView valueTextView = new TextView(requireContext());
        LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        valueParams.setMargins(getResources().getDimensionPixelSize(R.dimen.label_value_spacing), 0, 0, 20);
        valueTextView.setLayoutParams(valueParams);
        valueTextView.setText(value);
        pairLayout.addView(valueTextView);

        parentLayout.addView(pairLayout);
    }
}

