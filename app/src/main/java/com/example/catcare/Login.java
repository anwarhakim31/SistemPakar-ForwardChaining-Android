package com.example.catcare;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
public class Login extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, google;

    private ImageView show_pass_btn;
    private TextView btnRegister, forgotPassword;
    private DatabaseReference database;


    private FirebaseAuth auth;
     private GoogleSignInClient nGoogleSign;
    private GoogleApiClient mGoogleClient;

    private static final int RC_SIGN_IN =123;

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
        forgotPassword = findViewById(R.id.forgotPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        show_pass_btn = findViewById(R.id.show_pass_btn);
        google = findViewById(R.id.google);

        auth =  FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.webclientid)).requestEmail().build();

//        nGoogleSign = GoogleSignIn.getClient(this,gso);

        mGoogleClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(getApplicationContext(),"Koneksi dengan akun google gagal",Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginAkunGoogle();
            }
        });





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
                                // Simpan status login
                                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                                Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();
                                Intent masuk = new Intent(getApplicationContext(), MainActivity.class);
                                masuk.putExtra("username", username);
                                startActivity(masuk);
                                finish();
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

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Forgot.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Periksa status login saat aplikasi dibuka
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {

            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }
    }
    private void loginAkunGoogle() {


        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    String photoUrl = "";
                    String Email = "";
                    if (user.getPhotoUrl() != null) {
                        photoUrl = user.getPhotoUrl().toString();
                    }

                    if (user.getEmail() != null) {
                        Email = user.getEmail().toString();
                    }


                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("username", user.getDisplayName());
                    intent.putExtra("email", Email);
                    intent.putExtra("foto", photoUrl);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
