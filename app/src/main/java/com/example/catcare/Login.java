package com.example.catcare;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

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

public class Login extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button  btnLogin;
    private ImageView show_pass_btn;
    private TextView btnRegister;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_catcare);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnlogin);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        show_pass_btn = findViewById(R.id.show_pass_btn);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(getApplicationContext(), Register.class);
                startActivity(register);
            }
        });

        show_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Periksa metode transformasi saat ini pada EditText
                int selection = etPassword.getSelectionEnd();
                if (etPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    // Tampilkan password jika saat ini tersembunyi
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    show_pass_btn.setImageResource(R.drawable.eye_slash);
                } else {
                    // Sembunyikan password jika saat ini ditampilkan
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    show_pass_btn.setImageResource(R.drawable.eye);
                }
                etPassword.setSelection(selection);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                database = FirebaseDatabase.getInstance().getReference("users");

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Username atau Password Kosong", Toast.LENGTH_SHORT).show();
                } else {
                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean match = false;

                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String dbUsername = userSnapshot.child("username").getValue(String.class);
                                String dbPassword = userSnapshot.child("password").getValue(String.class);

                                if (dbUsername != null && dbPassword != null && dbUsername.equals(username) && dbPassword.equals(password)) {
                                    match = true;
                                    break;
                                }
                            }

                            if (match) {
                                Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();
                                Intent masuk = new Intent(getApplicationContext(), MainActivity.class);
                                masuk.putExtra("username", username);
                                startActivity(masuk);
                            } else {
                                Toast.makeText(getApplicationContext(), "Username atau Password Salah", Toast.LENGTH_SHORT).show();
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