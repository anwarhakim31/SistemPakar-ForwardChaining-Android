package com.example.catcare;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;


import android.widget.Button;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;




import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DiagnosisFragment extends Fragment {
    private ArrayList<CheckBox> checkBoxList = new ArrayList<>();

    private Button prosesButton;
    private String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {




        View view = inflater.inflate(R.layout.fragment_diagnosis, container, false);


        prosesButton = view.findViewById(R.id.prosesButton);

        LinearLayout cardContainer = view.findViewById(R.id.cardContainer);
        cardContainer.removeAllViews();

        cardContainer.setPadding(20, 80, 20, 125);

        TextView titleTextView = new TextView(requireContext());
        LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleLayoutParams.setMargins(0, 100, 0, 20); // Mengurangi nilai margin
        titleTextView.setLayoutParams(titleLayoutParams);
        titleTextView.setText("Daftar Gejala");
        titleTextView.setTextSize(24);
        titleTextView.setTextColor(getResources().getColor(R.color.colorBlack));
        titleTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cardContainer.addView(titleTextView);

        LinearLayout parentLayout = new LinearLayout(requireContext());
        LinearLayout.LayoutParams parentLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        parentLayoutParams.setMargins(40, 0, 0, 100);
        parentLayout.setLayoutParams(parentLayoutParams);
        parentLayout.setOrientation(LinearLayout.VERTICAL);
        parentLayout.setPadding(8, 8, 8, 8);
        parentLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Transparent background


        TextView titleText = new TextView(requireContext());
        LinearLayout.LayoutParams titleLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleLayoutParams.setMargins(0, 0, 0, 40);
        titleText.setLayoutParams(titleLayout);
        titleText.setText("*Ketentuan");
        titleText.setTextSize(20);
        parentLayout.addView(titleText);


        TextView minRequirementTextView = new TextView(requireContext());
        LinearLayout.LayoutParams minLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        minRequirementTextView.setLayoutParams(minLayoutParams);
        minRequirementTextView.setText("- minimal pilih 3 gejala");
        minRequirementTextView.setTextSize(16);
        parentLayout.addView(minRequirementTextView);

        // Menambahkan TextView untuk persyaratan maksimal
        TextView maxRequirementTextView = new TextView(requireContext());
        LinearLayout.LayoutParams maxLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        maxRequirementTextView.setLayoutParams(maxLayoutParams);
        maxRequirementTextView.setText("- maksimal pilih 5 gejala");
        maxRequirementTextView.setTextSize(16);
        parentLayout.addView(maxRequirementTextView);


        cardContainer.addView(parentLayout);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("gejala");

        Query query = databaseReference.orderByChild("id_gejala");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String idg = snapshot.child("id_gejala").getValue(String.class);
                    String ng = snapshot.child("nama_gejala").getValue(String.class);

                    if (idg != null && ng != null) {
                        // Buat CardView baru
                        CardView cardView = new CardView(requireContext());
                        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        cardLayoutParams.setMargins(20, 0, 20, 60); // Atur margin untuk setiap CardView
                        cardView.setLayoutParams(cardLayoutParams);
                        cardView.setCardElevation(5);
                        cardView.setRadius(30); // Atur radius sudut card

                        // Buat LinearLayout baru sebagai container untuk CheckBox
                        LinearLayout layout = new LinearLayout(requireContext());
                        layout.setOrientation(LinearLayout.HORIZONTAL);
                        layout.setPadding(16, 16, 16, 16);

                        // Buat CheckBox
                        CheckBox checkBox = new CheckBox(requireContext());
                        checkBox.setId(idg.hashCode()); // Gunakan hashCode dari gejalaId sebagai ID CheckBox
                        LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        checkBoxParams.weight = 1;
                        checkBox.setLayoutParams(checkBoxParams);
                        checkBox.setText(ng);
                        layout.addView(checkBox);

                        // Tambahkan CheckBox ke dalam array
                        checkBoxList.add(checkBox);

                        // Tambahkan LinearLayout ke dalam CardView
                        cardView.addView(layout);

                        // Tambahkan CardView ke dalam container LinearLayout
                        cardContainer.addView(cardView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString("username");
        }


        StringBuffer gejalaTerpilih = new StringBuffer();


        prosesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengosongkan StringBuffer
                gejalaTerpilih.setLength(0);



                for (CheckBox checkBox : checkBoxList) {
                    if (checkBox.isChecked()) {
                        // Mengambil teks dari CheckBox yang dipilih dan menambahkannya ke StringBuffer
                        gejalaTerpilih.append(checkBox.getText().toString()).append("#");
                    }
                }

                // Memeriksa apakah tidak ada gejala yang dipilih
                if (gejalaTerpilih.length() == 0) {
                    Toast.makeText(getContext(), "Silakan pilih gejala dahulu!", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(requireContext(), Hasil_Diagnosa.class);
                    intent.putExtra("HASIL", gejalaTerpilih.toString());
                    intent.putExtra("username", username);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        return view;
    }
}