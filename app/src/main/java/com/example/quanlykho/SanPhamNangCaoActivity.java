package com.example.quanlykho;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.conts.Constant;
import com.example.model.DanhMuc;
import com.example.model.SanPham;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SanPhamNangCaoActivity extends AppCompatActivity {
    Intent intent;
    SanPham sanPham;
    ImageView iv_back;
    EditText et_Ma, et_Loai, et_Ten, et_Gia, et_SoLuong, et_Size,et_MaLoai;
    Button bt_Sua, bt_Xoa, bt_luu;
    Spinner spinner_TinhTrang;
    ArrayAdapter<String> tinhTrangAdapter;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_san_pham_nang_cao);
        addControls();
        addevents();
    }

    private void addevents() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",1);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        bt_Xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyXoa();
            }
        });
        bt_Sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLySua();
            }
        });
        bt_luu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyLuu();
            }
        });
    }

    private void xuLyLuu() {
        SanPham sanPham= new SanPham();
        sanPham.setMaSanPham(Integer.parseInt(et_Ma.getText().toString()));
        sanPham.setTenSanPham(et_Ten.getText().toString());
        sanPham.setSoLuong(Integer.parseInt(et_SoLuong.getText().toString()));
        sanPham.setDonGia(Integer.parseInt(et_Gia.getText().toString()));
        sanPham.setSize(Integer.parseInt(et_Size.getText().toString()));

        SuaSanPhamTask suaSanPhamTask= new SuaSanPhamTask();
        suaSanPhamTask.execute(sanPham);
    }

    private void xuLySua() {
        et_Ten.setEnabled(true);
        et_Gia.setEnabled(true);
        et_SoLuong.setEnabled(true);
        et_Size.setEnabled(true);
        spinner_TinhTrang.setEnabled(true);
    }

    private void xuLyXoa() {
        dialog = new Dialog(SanPhamNangCaoActivity.this);
        dialog.setTitle("Xác nhận xóa");
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.show();
    }
    public void XacNhanXoa(View view) {
        dialog.dismiss();
        XoaSanPhamTask task= new XoaSanPhamTask();
        task.execute(Integer.parseInt(et_Ma.getText().toString()));
    }

    public void XacNhanHuy(View view) {
        dialog.dismiss();
    }

    private void addControls() {
        intent=getIntent();
        sanPham= (SanPham) intent.getSerializableExtra("SANPHAM");

        iv_back=findViewById(R.id.iv_backChiTiet);

        et_Ma=findViewById(R.id.edtMaSpChiTiet);
        et_Loai=findViewById(R.id.edtLoaiSpChiTiet);
        et_Ten=findViewById(R.id.edtTenSpChiTiet);
        et_Gia=findViewById(R.id.edtDonGiaSpChiTiet);
        et_SoLuong=findViewById(R.id.edtSoLuongSpChiTiet);
        et_Size=findViewById(R.id.edtSizeSpChiTiet);
        et_MaLoai=findViewById(R.id.edtMaDanhMucSpChiTiet);

        bt_luu=findViewById(R.id.btnLuuChiTiet);
        bt_Sua=findViewById(R.id.btnSuaChiTiet);
        bt_Xoa=findViewById(R.id.btnXoaChiTiet);

        et_Ma.setText(sanPham.getMaSanPham()+"");
        et_Ten.setText(sanPham.getTenSanPham());
        et_Gia.setText(sanPham.getDonGia()+"");
        et_SoLuong.setText(sanPham.getSoLuong()+"");
        et_Size.setText(sanPham.getSize()+"");
        et_MaLoai.setText(sanPham.getMaDanhMuc()+"");

        et_Ma.setEnabled(false);
        et_Ten.setEnabled(false);
        et_Gia.setEnabled(false);
        et_SoLuong.setEnabled(false);
        et_Size.setEnabled(false);
        et_MaLoai.setEnabled(false);
        et_Loai.setEnabled(false);

        spinner_TinhTrang=findViewById(R.id.spinner_TinhTrang);
        tinhTrangAdapter=new ArrayAdapter<>(SanPhamNangCaoActivity.this,android.R.layout.simple_spinner_item);
        tinhTrangAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_TinhTrang.setAdapter(tinhTrangAdapter);
        ArrayList<String> dstinhTrang= new ArrayList<>();
        dstinhTrang.add("Còn hàng");
        dstinhTrang.add("Hết hàng");
        tinhTrangAdapter.addAll(dstinhTrang);
        tinhTrangAdapter.notifyDataSetChanged();
        spinner_TinhTrang.setEnabled(false);

        TimDanhMucTheoMaTask task= new TimDanhMucTheoMaTask();
        task.execute(sanPham.getMaDanhMuc());
    }
    class TimDanhMucTheoMaTask extends AsyncTask<Integer,Void, DanhMuc> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(DanhMuc danhMuc) {
            super.onPostExecute(danhMuc);
            if(danhMuc!=null){
                et_Loai.setText(danhMuc.getTenDanhMuc().toString());
            }
            else
                Toast.makeText(SanPhamNangCaoActivity.this,"Không tìm thấy danh muc",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected DanhMuc doInBackground(Integer... integers) {
            try{
                URL url= new URL(Constant.IP_ADDRESS+"DanhMuc/"+integers[0]);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                InputStreamReader isr= new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br= new BufferedReader(isr);
                StringBuilder builder= new StringBuilder();
                String line=null;
                while ((line=br.readLine())!=null){
                    builder.append(line);
                }
                JSONObject object=new JSONObject(builder.toString());
                int maDm=object.getInt("MaDanhMuc");
                String tenDm=object.getString("TenDanhMuc");
                DanhMuc danhMuc= new DanhMuc();
                danhMuc.setTenDanhMuc(tenDm);
                danhMuc.setMaDanhMuc(maDm);

                return danhMuc;
            }
            catch (Exception ex){
                Log.e("LOI",ex.toString());
            }
            return null;
        }
    }
    class XoaSanPhamTask extends AsyncTask<Integer,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean==true){
                Toast.makeText(SanPhamNangCaoActivity.this, "Xóa thành công", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(SanPhamNangCaoActivity.this, "Xóa thất bại, vui lòng kiểm tra lại", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            try{
                URL url= new URL(Constant.IP_ADDRESS+"SanPham/?maSp="+integers[0]);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                InputStreamReader isr= new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br= new BufferedReader(isr);
                StringBuilder builder= new StringBuilder();
                String line=null;
                while ((line=br.readLine())!=null){
                    builder.append(line);
                }
                boolean kq=builder.toString().contains("true");
                return kq;
            }
            catch (Exception ex){
                Log.e("LOI",ex.toString());
            }
            return false;
        }
    }
    class SuaSanPhamTask extends AsyncTask<SanPham,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean==true){
                Toast.makeText(SanPhamNangCaoActivity.this, "Lưu thành công", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(SanPhamNangCaoActivity.this, "Lưu thất bại", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(SanPham... sanPhams) {
            try{
                int masp=sanPhams[0].getMaSanPham();
                String tensp=sanPhams[0].getTenSanPham();
                int gia=sanPhams[0].getDonGia();
                int soluong=sanPhams[0].getSoLuong();
                boolean tinhtrang=sanPhams[0].isTinhTrang();
                int size=sanPhams[0].getSize();
                int madm=sanPhams[0].getMaDanhMuc();

                String params="maSpSua="+masp +
                        "&tenSp="+ URLEncoder.encode(tensp) +
                        "&giaSp="+gia+
                        "&soLuong="+soluong +
                        "&tinhTrang="+tinhtrang +
                        "&size="+size;
                URL url= new URL(Constant.IP_ADDRESS+"SanPham/?"+params);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                InputStreamReader isr= new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br= new BufferedReader(isr);
                StringBuilder builder= new StringBuilder();
                String line=null;
                while ((line=br.readLine())!=null){
                    builder.append(line);
                }
                boolean kq=builder.toString().contains("true");
                return kq;

            }
            catch (Exception ex){
                Log.e("LOI",ex.toString());
            }
            return false;
        }
    }
}