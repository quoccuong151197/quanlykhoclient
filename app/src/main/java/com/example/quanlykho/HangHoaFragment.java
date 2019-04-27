package com.example.quanlykho;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.adapter.SanPhamAdapter;
import com.example.adapterimpl.DeleteButtonOnclick;
import com.example.conts.Constant;
import com.example.model.DanhMuc;
import com.example.model.SanPham;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class HangHoaFragment extends Fragment implements DeleteButtonOnclick {
    static View view;
    EditText edtTen, edtGia, edtSoLuong;
    Spinner spiner_ChungLoai, spinner_ChucNang;
    Button btnTim, btnThem, btnSua, btnLuu, btnNangCao;
    ArrayAdapter chucNangAdapter,chungLoaiAdapter;
    static SanPhamAdapter sanPhamAdapter;
    ListView lvSanPham;
    LinearLayout llNangCao, llGia;
    ArrayList<SanPham> dsSanPham;
    static int positionChucNang = 0;
    static int maSanPhamXoa=0;
    static int viTriSanPham=0;
    SanPham spSua;
    Spinner spinner_GiaMin, spinner_GiaMax;
    ArrayAdapter<Integer> giaAdapter;
    ArrayList<Integer>dsdonGia;
    ArrayList<SanPham> dsTatCaSanPham= new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (View) inflater.inflate(R.layout.fragment_hanghoa, container, false);
        addControls();
        addEvents();
        return view;
    }

    private void addEvents() {
        spinner_ChucNang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionChucNang = position;
                if (position == 0) {
                    llGia.setVisibility(View.GONE);
                } else if (position == 1) {
                    llGia.setVisibility(View.GONE);
                } else if (position == 2) {
                    llGia.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spiner_ChungLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lvSanPham.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                spSua= dsSanPham.get(position);
                DanhMuc danhMuc= (DanhMuc) spiner_ChungLoai.getSelectedItem();
                spSua.setMaDanhMuc(danhMuc.getMaDanhMuc());
                edtTen.setText(spSua.getTenSanPham());
                edtGia.setText(spSua.getDonGia()+"");
                edtGia.setEnabled(false);
                edtSoLuong.setText(spSua.getSoLuong()+"");
                edtSoLuong.setEnabled(false);

                Intent intent= new Intent(getContext(),SanPhamNangCaoActivity.class);
                intent.putExtra("SANPHAM",spSua);
                startActivityForResult(intent,1);

            }
        });
        lvSanPham.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                registerForContextMenu(lvSanPham);
                viTriSanPham=position;
                SanPham sanPham=sanPhamAdapter.getItem(position);
                maSanPhamXoa=sanPham.getMaSanPham();
                return false;
            }
        });
        btnTim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyTim();
            }
        });
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtGia.setEnabled(true);
                edtSoLuong.setEnabled(true);
            }
        });
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spSua != null) {
                    xuLySuaSanPham();
                }
            }
        });
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(),ThemSanPhamActivity.class);
                startActivity(intent);
            }
        });
        btnNangCao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleButton= String.valueOf(btnNangCao.getText()).toUpperCase();
                if(titleButton.equals("NÂNG CAO")){
                    llNangCao.setVisibility(View.VISIBLE);
                    btnNangCao.setText("THU NHỎ");
                    btnNangCao.setBackgroundResource(R.drawable.boder_buuton_dong);
                }
                else if(titleButton.equals("THU NHỎ")){
                    llNangCao.setVisibility(View.GONE);
                    btnNangCao.setText("NÂNG CAO");
                    btnNangCao.setBackgroundResource(R.drawable.boder_buuton_luu);
                }
            }
        });
    }

    private void xuLyXacNhanXoaSanPham() {
        ConfirmDialog confirmDialog= new ConfirmDialog();
        confirmDialog.show(getFragmentManager(),"sss");
    }

    public void xuLyTim() {
        if (positionChucNang == 0)
            xuLyTimTheoLoai();
        else if (positionChucNang == 1)
            xuLyTimTheoTen();
        else if (positionChucNang == 2)
            xuLyTimTheoGia();
    }


    private void xuLySuaSanPham() {
        SanPham sanPham= new SanPham();
        sanPham.setMaSanPham(spSua.getMaSanPham());
        sanPham.setTenSanPham(edtTen.getText().toString());
        sanPham.setSoLuong(Integer.parseInt(edtSoLuong.getText().toString()));
        sanPham.setDonGia(Integer.parseInt(edtGia.getText().toString()));
        SuaSanPhamTask task= new SuaSanPhamTask();
        task.execute(sanPham);
    }

    private void xuLyTimTheoLoai() {
        DanhMuc dm = (DanhMuc) spiner_ChungLoai.getSelectedItem();
        TraCuuSanPhamTheoMaDmTask task = new TraCuuSanPhamTheoMaDmTask();
        task.execute(dm.getMaDanhMuc());
    }

    private void xuLyTimTheoGia() {
        int giaMin= (int) spinner_GiaMin.getSelectedItem();
        int giaMax= (int) spinner_GiaMax.getSelectedItem();
        TraCuuSanPhamTheoDonGiaTask task= new TraCuuSanPhamTheoDonGiaTask();
        String params="a="+giaMin+"&b="+giaMax;
        task.execute(params);
    }

    private void xuLyTimTheoTen() {
        String ten= String.valueOf(edtTen.getText());
        SanPham sanPham= new SanPham();
        sanPham.setTenSanPham(ten);
        TraCuuSanPhamTheoTenTask theoTenTask= new TraCuuSanPhamTheoTenTask();
        theoTenTask.execute(sanPham);
    }
    private void addControls() {
        edtGia = view.findViewById(R.id.edtGiaSanPham);
        edtSoLuong = view.findViewById(R.id.edtSoLuong);
        edtTen = view.findViewById(R.id.edtTenSanPham);

        btnLuu = view.findViewById(R.id.btnLuu);
        btnSua = view.findViewById(R.id.btnSua);
        btnThem = view.findViewById(R.id.btnThem);
        btnTim = view.findViewById(R.id.btnTim);

        spinner_ChucNang = view.findViewById(R.id.spinner_TraCuu);
        chucNangAdapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_item);
        chucNangAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayList<String> dsChucNang = new ArrayList<>();
        dsChucNang.add("Loại");
        dsChucNang.add("Tên");
        dsChucNang.add("Giá");
        chucNangAdapter.addAll(dsChucNang);
        spinner_ChucNang.setAdapter(chucNangAdapter);

        spiner_ChungLoai = view.findViewById(R.id.spinner_ChungLoai);
        chungLoaiAdapter = new ArrayAdapter(view.getContext(),android.R.layout.simple_spinner_item);
        chungLoaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiner_ChungLoai.setAdapter(chungLoaiAdapter);

        LayDanhSachDanhMucTask task = new LayDanhSachDanhMucTask();
        task.execute();

        dsSanPham = new ArrayList<>();
        lvSanPham = view.findViewById(R.id.lvSanPham);
        sanPhamAdapter = new SanPhamAdapter(this.view.getContext(), R.layout.row_sanpham, dsSanPham);
        sanPhamAdapter.isClicked(this);
        lvSanPham.setAdapter(sanPhamAdapter);

        llNangCao=view.findViewById(R.id.ll_nang_cao);
        llNangCao.setVisibility(View.GONE);

        btnNangCao=view.findViewById(R.id.btnNangCao);

        llGia=view.findViewById(R.id.llGia);
        llGia.setVisibility(View.GONE);
        spinner_GiaMax=view.findViewById(R.id.spinner_GiaMax);
        spinner_GiaMin=view.findViewById(R.id.spinner_GiaMin);
        giaAdapter=new ArrayAdapter<>(view.getContext(),android.R.layout.simple_spinner_item);
        giaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dsdonGia= new ArrayList<>();
        addDataGia();
        giaAdapter.addAll(dsdonGia);
        spinner_GiaMin.setAdapter(giaAdapter);
        spinner_GiaMax.setAdapter(giaAdapter);
    }

    private void addDataGia() {
        dsdonGia.add(0);
        dsdonGia.add(500000);
        dsdonGia.add(1000000);
        dsdonGia.add(1500000);
        dsdonGia.add(2000000);
        dsdonGia.add(2500000);
        dsdonGia.add(3000000);
        dsdonGia.add(3500000);
        dsdonGia.add(4000000);
        dsdonGia.add(4500000);
        dsdonGia.add(5000000);
        dsdonGia.add(5500000);
        dsdonGia.add(6000000);
        dsdonGia.add(6500000);
        dsdonGia.add(7000000);
        dsdonGia.add(7500000);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
                getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.itemXoa:
                xuLyXacNhanXoaSanPham();
                break;
            case R.id.itemSua:
                xuLySuaSanPhamChitiet();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void xuLySuaSanPhamChitiet() {
        SanPham sanPhamChon=sanPhamAdapter.getItem(viTriSanPham);
        DanhMuc danhMuc= (DanhMuc) spiner_ChungLoai.getSelectedItem();
        sanPhamChon.setMaDanhMuc(danhMuc.getMaDanhMuc());
        Intent intent= new Intent(getContext(),SanPhamNangCaoActivity.class);
        intent.putExtra("SANPHAM",sanPhamChon);
        startActivityForResult(intent,1);
    }

    public static void xuLyXoaSanPham() {
        XoaSanPhamTask task= new XoaSanPhamTask();
        task.execute(maSanPhamXoa);

    }
    public static void xuLyCapNhatDSSanPHamSauKhiXoa() {
        SanPham sanPham=sanPhamAdapter.getItem(viTriSanPham);
        sanPhamAdapter.remove(sanPham);
        sanPhamAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteOnclick(int position) {
        viTriSanPham=position;
        SanPham sanPham=sanPhamAdapter.getItem(position);
        maSanPhamXoa=sanPham.getMaSanPham();
        xuLyXacNhanXoaSanPham();
    }

    class LayDanhSachDanhMucTask extends AsyncTask<Void, Void, ArrayList<DanhMuc>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<DanhMuc> danhMucs) {
            super.onPostExecute(danhMucs);
            if (danhMucs != null) {
                chungLoaiAdapter.clear();
                chungLoaiAdapter.addAll(danhMucs);
                chucNangAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<DanhMuc> doInBackground(Void... voids) {
            ArrayList<DanhMuc> dsDanhMuc = new ArrayList<>();
            try {
                URL url = new URL(Constant.IP_ADDRESS+"DanhMuc");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                JSONArray jsonArray = new JSONArray(builder.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object == null) {
                        Toast.makeText(view.getContext(), "không tìm thấy danh mục", Toast.LENGTH_LONG).show();
                    } else if (object != null) {
                        int ma = object.getInt("MaDanhMuc");
                        String ten = object.getString("TenDanhMuc");
                        DanhMuc dm = new DanhMuc();
                        dm.setTenDanhMuc(ten);
                        dm.setMaDanhMuc(ma);

                        dsDanhMuc.add(dm);
                    }
                }
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
            }
            return dsDanhMuc;
        }
    }

    class TraCuuSanPhamTheoMaDmTask extends AsyncTask<Integer, Void, ArrayList<SanPham>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<SanPham> sanPhams) {
            super.onPostExecute(sanPhams);
            if (sanPhams != null) {
                dsSanPham.clear();
                dsSanPham.addAll(sanPhams);
                sanPhamAdapter.notifyDataSetChanged();
            } else
                Toast.makeText(view.getContext(), "không tìm thấy sản phẩm", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<SanPham> doInBackground(Integer... integers) {
            ArrayList<SanPham> dsSp = new ArrayList<>();
            try {
                URL url = new URL(Constant.IP_ADDRESS+"SanPham/?madm=" + integers[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                JSONArray jsonArray = new JSONArray(builder.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object == null) {
                        Toast.makeText(view.getContext(), "không tìm thấy sản phẩm", Toast.LENGTH_LONG).show();
                    } else if (object != null) {
                        int ma = object.getInt("Ma");
                        String ten = object.getString("Ten");
                        int donGia = object.getInt("DonGia");
                        int soLuong = object.getInt("SoLuong");
                        boolean tinhTrang = object.getBoolean("TinhTrang");
                        int size = object.getInt("Size");

                        SanPham sp = new SanPham();
                        sp.setMaSanPham(ma);
                        sp.setTenSanPham(ten);
                        sp.setDonGia(donGia);
                        sp.setSoLuong(soLuong);
                        sp.setTinhTrang(tinhTrang);
                        sp.setSize(size);

                        dsSp.add(sp);
                    }
                }
                return dsSp;
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
            }
            return null;
        }
    }

    class TraCuuSanPhamTheoMaTask extends AsyncTask<Integer, Void, SanPham> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(SanPham sanPham) {
            super.onPostExecute(sanPham);
            try {
                if (sanPham != null) {
                    ArrayList<SanPham> dsSp = new ArrayList<>();
                    dsSp.add(sanPham);
                    sanPhamAdapter.clear();
                    sanPhamAdapter.addAll(dsSp);
                } else if (sanPham == null) {
                    Toast.makeText(view.getContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected SanPham doInBackground(Integer... integers) {
            try {
                URL url = new URL(Constant.IP_ADDRESS+"SanPham/" + integers[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                JSONObject object = new JSONObject(builder.toString());
                if (object == null) {
                    Toast.makeText(view.getContext(), "không tìm thấy sản phẩm", Toast.LENGTH_LONG).show();
                }
                int ma = object.getInt("Ma");
                String ten = object.getString("Ten");
                int donGia = object.getInt("DonGia");
                int soLuong = object.getInt("SoLuong");
                boolean tinhTrang = object.getBoolean("TinhTrang");
                int size = object.getInt("Size");

                SanPham sp = new SanPham();
                sp.setMaSanPham(ma);
                sp.setTenSanPham(ten);
                sp.setDonGia(donGia);
                sp.setSoLuong(soLuong);
                sp.setTinhTrang(tinhTrang);
                sp.setSize(size);

                return sp;
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
            }
            return null;
        }
    }

    class TraCuuSanPhamTheoTenTask extends AsyncTask<SanPham,Void,ArrayList<SanPham>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<SanPham> sanPhams) {
            super.onPostExecute(sanPhams);
            if (sanPhams != null) {
                sanPhamAdapter.clear();
                sanPhamAdapter.addAll(sanPhams);
            } else{
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle("không tìm thấy sản phẩm");
                alertDialog.setIcon(R.drawable.ic_error);
                alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<SanPham> doInBackground(SanPham... sanPhams) {
            ArrayList<SanPham> dsSp = new ArrayList<>();
            try {
                String params=sanPhams[0].getTenSanPham().toString();
                String []paramsm=params.split(" ");
                StringBuilder builder1=new StringBuilder();
                for (int i=0;i<paramsm.length-1;i++){
                    builder1.append(paramsm[i]).append("%20");
                }
                String kq=builder1.append(paramsm[paramsm.length-1]).toString();
                URL url = new URL(Constant.IP_ADDRESS+"SanPham/?tenSanPhamTim=" +kq);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                JSONObject object = new JSONObject(builder.toString());
                if (object == null) {
                    Toast.makeText(view.getContext(), "không tìm thấy sản phẩm", Toast.LENGTH_LONG).show();
                } else if (object != null) {
                    int ma = object.getInt("Ma");
                    String ten = object.getString("Ten");
                    int donGia = object.getInt("DonGia");
                    int soLuong = object.getInt("SoLuong");
                    boolean tinhTrang = object.getBoolean("TinhTrang");
                    int size = object.getInt("Size");

                    SanPham sp = new SanPham();
                    sp.setMaSanPham(ma);
                    sp.setTenSanPham(ten);
                    sp.setDonGia(donGia);
                    sp.setSoLuong(soLuong);
                    sp.setTinhTrang(tinhTrang);
                    sp.setSize(size);

                    dsSp.add(sp);
                    return dsSp;
                }
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
            }
            return null;
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
                Toast.makeText(view.getContext(), "Lưu thành công", Toast.LENGTH_LONG).show();
                xuLyTim();
            }
            else {
                Toast.makeText(view.getContext(), "Lưu thất bại", Toast.LENGTH_LONG).show();
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
    class LayDanhSachSanPhamTask extends AsyncTask<Void, Void, ArrayList<SanPham>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<SanPham> sanPhams) {
            super.onPostExecute(sanPhams);
            dsTatCaSanPham.addAll(sanPhams);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<SanPham> doInBackground(Void... voids) {
            ArrayList<SanPham> dsSp= new ArrayList<>();
            try{
                URL url= new URL(Constant.IP_ADDRESS+"SanPham");
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                JSONArray jsonArray=new JSONArray(builder.toString());
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject object= jsonArray.getJSONObject(i);
                    if(object==null){
                        Toast.makeText(view.getContext(),"không tìm thấy sản phẩm",Toast.LENGTH_LONG).show();
                    }
                    else if(object!=null){
                        int ma=object.getInt("Ma");
                        String ten=object.getString("Ten");
                        int donGia=object.getInt("DonGia");
                        int soLuong=object.getInt("SoLuong");
                        boolean tinhTrang=object.getBoolean("TinhTrang");
                        int size=object.getInt("Size");

                        SanPham sp= new SanPham();
                        sp.setMaSanPham(ma);
                        sp.setTenSanPham(ten);
                        sp.setDonGia(donGia);
                        sp.setSoLuong(soLuong);
                        sp.setTinhTrang(tinhTrang);
                        sp.setSize(size);

                        dsSp.add(sp);
                    }
                }
                return dsSp;
            }
            catch (Exception ex){
                Log.e("LOI",ex.toString());
            }
            return null;
        }
    }
    class TraCuuSanPhamTheoDonGiaTask extends AsyncTask<String, Void, ArrayList<SanPham>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<SanPham> sanPhams) {
            super.onPostExecute(sanPhams);
            if(sanPhams!=null){
                sanPhamAdapter.clear();
                sanPhamAdapter.addAll(sanPhams);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<SanPham> doInBackground(String... strings) {
            ArrayList<SanPham> dsSp= new ArrayList<>();
            try{
                URL url= new URL(Constant.IP_ADDRESS+"SanPham/?"+strings[0]);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                JSONArray jsonArray=new JSONArray(builder.toString());
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject object= jsonArray.getJSONObject(i);
                    if(object==null){
                        Toast.makeText(view.getContext(),"không tìm thấy sản phẩm",Toast.LENGTH_LONG).show();
                    }
                    else if(object!=null){
                        int ma=object.getInt("Ma");
                        String ten=object.getString("Ten");
                        int donGia=object.getInt("DonGia");
                        int soLuong=object.getInt("SoLuong");
                        boolean tinhTrang=object.getBoolean("TinhTrang");
                        int size=object.getInt("Size");

                        SanPham sp= new SanPham();
                        sp.setMaSanPham(ma);
                        sp.setTenSanPham(ten);
                        sp.setDonGia(donGia);
                        sp.setSoLuong(soLuong);
                        sp.setTinhTrang(tinhTrang);
                        sp.setSize(size);

                        dsSp.add(sp);
                    }
                }
                return dsSp;
            }
            catch (Exception ex){
                Log.e("LOI",ex.toString());
            }
            return null;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                xuLyTim();
            }
        }
    }
    static class XoaSanPhamTask extends AsyncTask<Integer,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean==true){
                xuLyCapNhatDSSanPHamSauKhiXoa();
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle("Xóa thành công");
                alertDialog.setIcon(R.drawable.ic_ok);
                alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            }
            else {
                Toast.makeText(view.getContext(),"Xóa thất bại",Toast.LENGTH_LONG).show();
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
}