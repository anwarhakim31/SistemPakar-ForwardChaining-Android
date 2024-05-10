package com.example.catcare;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Ambil nilai 'username' dari intent di sini
        username = getIntent().getStringExtra("username");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_beranda);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_beranda) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (item.getItemId() == R.id.nav_penyakit) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PenyakitFragment()).commit();
        } else if (item.getItemId() == R.id.nav_tentang) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TentangFragment()).commit();
        } else if (item.getItemId() == R.id.nav_riwayat) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RiwayatFragment()).commit();
        } else if (item.getItemId() == R.id.nav_diagnosis) {
            DiagnosisFragment fragment = new DiagnosisFragment();
            Bundle args = new Bundle();
            args.putString("username", username);
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        } else if (item.getItemId() == R.id.nav_logout) {
            // Hapus status login dari SharedPreferences
            getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().putBoolean("isLoggedIn", false).apply();
            Intent logout = new Intent(MainActivity.this, Login.class);
            startActivity(logout);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
