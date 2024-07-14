package com.example.catcare;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Forgot extends AppCompatActivity {
    private EditText etUsername, etEmail, etPassword,confPassword;
    private TextView ingatakun;
    private Button btnGanti;
    private boolean mail = false;
    private boolean cpass = false;

    private boolean pass = false;
    private ImageView show_pass_btn;
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.forgot_catcare);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        show_pass_btn = findViewById(R.id.show_pass_btn);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnGanti = findViewById(R.id.btnGanti);
        confPassword = findViewById(R.id.etPasswords);
        ingatakun = findViewById(R.id.ingat);
        ingatakun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(getApplicationContext(), Login.class);
                startActivity(login);
            }
        });
        show_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selection = etPassword.getSelectionEnd();
                if (etPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {

                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    show_pass_btn.setImageResource(R.drawable.eye_slash);
                } else {

                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    show_pass_btn.setImageResource(R.drawable.eye);
                }
                etPassword.setSelection(selection);
            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Method ini dipanggil sebelum teks berubah
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = etEmail.getText().toString().trim();


            }
            @Override
            public void afterTextChanged(Editable s) {
                String email = etEmail.getText().toString().trim();

                if (!email.contains("@")) {

                    etEmail.setError("Email harus terdapat karakter '@' ");
                    mail = false;
                    return;

                }
                database = FirebaseDatabase.getInstance().getReference("users");
                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                            String existingEmail = userSnapshot.child("email").getValue(String.class);

                            Log.d("TAG", existingEmail);
                            Log.d("TAG", email);
                            Log.d("TAG", String.valueOf(existingEmail.equals(email)));

                            if (existingEmail != null && existingEmail.equals(email)) {

                                etEmail.setError(null);
                                mail = true; // Mengatur mail menjadi true karena email sudah digunakan

                            }else{
                                etEmail.setError("Email tidak valid");
                                mail = false;

                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = etPassword.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String passwordRegex = "^(?=.*\\d)[\\w\\W]*$";

                if (password.isEmpty()) {
                    etPassword.setError("Masukkan password");
                    pass = false;
                } else if (password.length() <= 5) {
                    etPassword.setError("Password harus lebih dari 5 karakter");
                    pass = false;
                } else if (password.length() >= 20) {
                    etPassword.setError("Password harus kurang dari 20 karakter");
                    pass = false;
                } else if (!password.matches(passwordRegex)) {
                    etPassword.setError("Password setidaknya harus mengandung satu angka");
                    pass = false;
                } else if (password.equals(email)) {
                    etPassword.setError("Password tidak boleh sama dengan email");
                    pass = false;
                } else {
                    etPassword.setError(null);
                    pass = true;
                }
            }
        });

        confPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = etPassword.getText().toString().trim();
                String cpassword = confPassword.getText().toString().trim();

                if (!cpassword.isEmpty() && !cpassword.equals(password)) {

                    cpass = false;
                } else {

                    cpass = true;
                }
            }
        });
        btnGanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = etEmail.getText().toString().trim();
                final String password = etPassword.getText().toString().trim();
                final String cpassword = confPassword.getText().toString().trim();




                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Masukan Email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Masukan password");
                    return;
                }


                if (TextUtils.isEmpty(cpassword)) {
                    etPassword.setError("Masukan konfirmasi password");
                    return;
                }

                if (!mail) {
                    etEmail.setError("Email tidak valid");
                    return;
                }
                if (!cpass) {
                    confPassword.setError("Password tidak sama");
                    return;
                }


                if (mail && cpass) {
                    database = FirebaseDatabase.getInstance().getReference("users");
                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean emailFound = false;
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                                String emails = userSnapshot.child("email").getValue(String.class);
                                if (emails != null && emails.equals(email)) {
                                    userSnapshot.child("password").getRef().setValue(cpassword);
                                    Toast.makeText(Forgot.this, "Berhasil mengganti password", Toast.LENGTH_SHORT).show();
                                    emailFound = true;
                                    break;
                                }
                            }
                            if (!emailFound) {
                                Toast.makeText(Forgot.this, "Email tidak ditemukan", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent loginIntent = new Intent(Forgot.this, Login.class);
                                startActivity(loginIntent);
                                finish();
                            }


//
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Forgot.this, "Gagal mengganti password: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}