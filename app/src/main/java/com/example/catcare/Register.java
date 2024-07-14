package com.example.catcare;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;
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


public class Register extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword,confPassword;
    private Button btnRegister;

    private TextView memilikiakun;
    private boolean user = false;
    private boolean mail = false;

    private boolean pass = false;

    private DatabaseReference database;
    private ImageView show_pass_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register_catcare);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        etPassword = findViewById(R.id.etPassword);

        show_pass_btn = findViewById(R.id.show_pass_btn);
        memilikiakun = findViewById(R.id.memilikiAkun);

        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://catcare-736ed-default-rtdb.firebaseio.com/");


        memilikiakun.setOnClickListener(new View.OnClickListener() {
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



        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = etUsername.getText().toString().trim();





                if (username.equals("")) {
                    etUsername.setError("Masukan username");
                    user = false;
                } else if (username.length() <= 5) {

                    etUsername.setError("Username harus lebih dari 5 karakter");
                    user = false;
                } else if (username.length() >= 20) {

                    etUsername.setError("Username harus kurang dari 20 karakter");
                    user = false;
                } else {

                    etUsername.setError(null);
                    user = true;
                }
                database = FirebaseDatabase.getInstance().getReference("users");
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                            String existingUsername = userSnapshot.child("username").getValue(String.class);
                            if (existingUsername != null && existingUsername.equals(username)) {

                                etUsername.setError("Username telah digunakan");
                                user = false;
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error handling jika query database dibatalkan

                    }
                });

            }


            @Override
            public void afterTextChanged(Editable s) {
                // Method ini dipanggil setelah teks berubah

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




                if (email.equals("")) {

                    etEmail.setError("Masukan email");
                    mail = false;
                } else if (!email.contains("@")) {

                    etEmail.setError("Email harus terdapat karakter '@' ");
                    mail = false;

                } else {

                    etEmail.setError(null);
                    mail = true;
                }


                database = FirebaseDatabase.getInstance().getReference("users");
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                            String existingEmail = userSnapshot.child("email").getValue(String.class);
                            if (existingEmail != null && existingEmail.equals(email)) {

                                etEmail.setError("Email sudah di gunakan");
                                mail = false;
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
            }






            @Override
            public void afterTextChanged(Editable s) {


            }
        });



        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = etPassword.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
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
                } else if (password.equals(username)) {
                    etPassword.setError("Password tidak boleh sama dengan username");
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



            @Override
            public void afterTextChanged(Editable s) {
                // Method ini dipanggil setelah teks berubah

            }
        });



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = etUsername.getText().toString().trim();
                final String email = etEmail.getText().toString().trim();
                final String password = etPassword.getText().toString().trim();

                // Validasi input kosong
                if (TextUtils.isEmpty(username)) {
                    etUsername.setError("Masukan username");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Masukan Email");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Masukan password");
                    return;
                }


                if (user && mail && pass) {

                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.child("users").child(username).exists()) {
                                etUsername.setError("username sudah di guanakan");
                                return;
                            } else {

                                database.child(username).child("username").setValue(username);
                                database.child(username).child("email").setValue(email);
                                database.child(username).child("password").setValue(password);


                                Toast.makeText(Register.this, "Berhasil membuat akun", Toast.LENGTH_SHORT).show();
                                Intent loginIntent = new Intent(Register.this, Login.class);
                                startActivity(loginIntent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled event
                        }
                    });
                }
            }
        });


    }
}

