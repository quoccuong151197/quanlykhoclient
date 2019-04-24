package com.example.quanlykho;

import android.content.ClipData;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.model.DanhMuc;
import com.example.model.NhanVien;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NhanVien nhanVien;
   /* TextView txtTenNhanVien, txtDiachi, txtPhone, txtMail, txtUsername, txtChucVu;
    NhanVien nhanVienLoagin;*/





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

       getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HangHoaFragment()).commit();
        addControls();
        addEvents();
        fakeData();
    }
    private void addEvents() {
    }

    private void fakeData() {

    }

    private void addControls() {
        Intent intentNhanVien=getIntent();
        nhanVien= (NhanVien) intentNhanVien.getSerializableExtra("NHANVIEN");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_HangHoa) {
           getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HangHoaFragment()).commit();
        } else if (id == R.id.nav_DanhMuc) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new DanhMucFragment()).commit();
        }

        else if (id == R.id.nav_NhanSu) {
            if (nhanVien.getRole() == 0) {
                Intent intent= new Intent(MainActivity.this,NhanSuActivity.class);
                startActivity(intent);
            }
            else
                Toast.makeText(MainActivity.this,"Bạn không có quyền truy cập",Toast.LENGTH_LONG).show();
        }
        else if(id==R.id.nav_TaiKhoan){
            Intent intent= new Intent(MainActivity.this,TaiKhoanActivity.class);
            intent.putExtra("NHANVIEN",nhanVien);
            startActivity(intent);
        }
        else if(id==R.id.nav_DoiMatKhau){
            Intent intent= new Intent(MainActivity.this,DoiMatKhauActivity.class);
            intent.putExtra("NHANVIEN",nhanVien);
            startActivity(intent);
        }
        else if(id==R.id.nav_DangXuat){
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}