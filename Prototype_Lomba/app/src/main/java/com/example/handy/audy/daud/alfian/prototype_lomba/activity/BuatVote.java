package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.enumeration.KategoriEnum;
import com.example.handy.audy.daud.alfian.prototype_lomba.enumeration.KategoriEnumLurah;
import com.example.handy.audy.daud.alfian.prototype_lomba.enumeration.KategoriEnumRT;
import com.example.handy.audy.daud.alfian.prototype_lomba.enumeration.KategoriEnumRW;
import com.example.handy.audy.daud.alfian.prototype_lomba.gcm.service.GcmSender;
import com.example.handy.audy.daud.alfian.prototype_lomba.jsonparser.JSONParser;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.Soal;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BuatVote extends BaseActivity {


    private ProgressDialog pDialog;
    private static final String TAG_ITEMS = "items";
    private static final String TAG_CHANNEL = "channel";
    private int flag = 0;

    EditText txtPertanyaan, txtTanggalMulai, txtTanggalSelesai;
    Button btnTambahPilihan, btnKembali, btnKirim, btnHapusPilihan;
    int flagKeyboard = 0;
    Calendar calendar = Calendar.getInstance();
    List<String> namaPilihan;
    String isiSoal, tanggalMulaiString, tanggalSelesaiString, idPembuat, kategori, idKtp, level;
    Spinner spnKategori;
    LinearLayout layoutPilihan;
    Object theActivity;
    ArrayAdapter<Enum> spinAdapter;
    List<Enum> spinnerArray;
    DatePickerDialog dMulai, dSelesai;
    JSONObject jsonObject;

    private void updateLabelMulai() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        txtTanggalMulai.setText(sdf.format(calendar.getTime()));
    }

    private void updateLabelSelesai() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        txtTanggalSelesai.setText(sdf.format(calendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_vote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layoutPilihan = (LinearLayout)findViewById(R.id.layoutPilihan);
        namaPilihan = new ArrayList<>();
        idKtp = getIntent().getStringExtra("idKtp");
        idPembuat = getIntent().getStringExtra("idUser");
        theActivity = this;

        spnKategori = (Spinner)findViewById(R.id.spnKategori);
        spinnerArray = new ArrayList<>();
        spinAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,spinnerArray);
        spnKategori.setAdapter(spinAdapter);

        new checkLevel().execute(); // Buat ngecek level dan nentuin enum yang mau dipakai

        final DatePickerDialog.OnDateSetListener tanggalMulai = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelMulai();
            }
        };

        final DatePickerDialog.OnDateSetListener tanggalSelesai = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelSelesai();
            }
        };

        txtPertanyaan = (EditText) findViewById(R.id.txtPertanyaan);

        txtTanggalMulai = (EditText) findViewById(R.id.txtTanggalMulai);
        txtTanggalMulai.setInputType(InputType.TYPE_NULL);
        dMulai = new DatePickerDialog(BuatVote.this, tanggalMulai, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        //soft keyboard
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        txtTanggalMulai.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    dMulai.show();
                }
            }
        });

        txtTanggalSelesai = (EditText) findViewById(R.id.txtTanggalSelesai);
        txtTanggalSelesai.setInputType(InputType.TYPE_NULL);
        dSelesai = new DatePickerDialog(BuatVote.this, tanggalSelesai, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        txtTanggalSelesai.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    dSelesai.show();
                }
            }
        });

        btnTambahPilihan = (Button) findViewById(R.id.btnTambahPilihan);
        btnKembali = (Button) findViewById(R.id.btnKembali);
        btnKirim = (Button) findViewById(R.id.btnKirim);
        btnHapusPilihan = (Button)findViewById(R.id.btnHapusPilihan);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 0;
                isiSoal = txtPertanyaan.getText().toString();
                if(spnKategori.getSelectedItem() == null){
                    flag = 1;
                }
                else{
                    kategori = spnKategori.getSelectedItem().toString();
                }
                tanggalMulaiString = txtTanggalMulai.getText().toString();
                tanggalSelesaiString = txtTanggalSelesai.getText().toString();

                // Ambil semua pilihan di sini
                namaPilihan.clear();
                for (int i = 0; i < layoutPilihan.getChildCount(); i++) {
                    View child = layoutPilihan.getChildAt(i);
                    if (child instanceof EditText) {
                        if(((EditText) child).getText().toString().equals(""))
                        {
                            ((EditText) child).setError("Pilihan harus diisi");
                        }
                        else namaPilihan.add(((EditText) child).getText().toString());
                    }
                }
                if(namaPilihan.size() < 2){
                    flag = 2;
                }
                if(flag == 1){
                    Toast.makeText(BuatVote.this, "Tolong pilih kategori dari pertanyaan.", Toast.LENGTH_SHORT).show();
                }
                else if(flag == 2){
                    Toast.makeText(BuatVote.this, "Jumlah pilihan minimum adalah 2(dua). Tolong tambah jumlah pilihan.", Toast.LENGTH_SHORT).show();
                }
                else{

                    if(validate(isiSoal, tanggalMulaiString, tanggalSelesaiString)) {
                        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

                        try
                        {
                            if((sdfDate.parse(tanggalSelesaiString.toString()).after(sdfDate.parse(tanggalMulaiString.toString()))) ||
                                    sdfDate.parse(tanggalSelesaiString.toString()).equals(sdfDate.parse(tanggalMulaiString.toString())))
                            {
                                new InsertPertanyaan().execute();

                            }
                            else
                            {
                                txtTanggalMulai.setError(null);
                                txtTanggalSelesai.setError("Tanggal selesai pemilu harus sama atau minimal terdapat rentang 1 hari dari tanggal mulai pemilu");
                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }


                    }
                }
            }
        });

        btnTambahPilihan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText txtPilihan = new EditText(BuatVote.this);
                    txtPilihan.setHint("Pilihan");
                    txtPilihan.setSingleLine(true);
                    txtPilihan.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    layoutPilihan.addView(txtPilihan);

                    if(layoutPilihan.getChildCount() < 1)btnHapusPilihan.setVisibility(View.GONE);
                    else btnHapusPilihan.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        btnHapusPilihan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txt = (EditText) layoutPilihan.getChildAt(layoutPilihan.getChildCount()-1);
                layoutPilihan.removeView(txt);

                if(layoutPilihan.getChildCount() < 1)btnHapusPilihan.setVisibility(View.GONE);
                else btnHapusPilihan.setVisibility(View.VISIBLE);
            }
        });

    }

    private boolean validate(String isiSoal, String tanggalMulaiString, String tanggalSelesaiString)
    {
        boolean valid = true;
        if(isiSoal.trim().equals(""))
        {
            valid = false;
            txtPertanyaan.setError("Pertanyaan harus diisi");
        }

        if(tanggalMulaiString.trim().equals(""))
        {
            valid = false;
            txtTanggalMulai.setError("Tanggal mulai pemilu harus diisi");
        }

        if(tanggalSelesaiString.trim().equals(""))
        {
            valid = false;
            txtTanggalSelesai.setError("Tanggal selesai pemilu harus diisi");
        }
        return valid;

    }

    class InsertPertanyaan extends AsyncTask<String,String,String> {
        int success, success2 = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BuatVote.this);
            pDialog.setMessage("Memasukkan pertanyaan baru...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                List<NameValuePair> args = new ArrayList<>();
                args.add(new BasicNameValuePair("tag","create_voting"));
                args.add(new BasicNameValuePair("isiSoal",isiSoal));
                args.add(new BasicNameValuePair("idPembuat", idPembuat));
                args.add(new BasicNameValuePair("tanggal_mulai",tanggalMulaiString));
                args.add(new BasicNameValuePair("tanggal_selesai", tanggalSelesaiString));
                args.add(new BasicNameValuePair("idKtp", idKtp));
                args.add(new BasicNameValuePair("kategori",kategori.toLowerCase()));
                jsonObject = jsonParser.makeHttpRequest(urlWebService, "POST", args);

                success = jsonObject.getInt(TAG_SUCCESS);

                if(success == 1) {
                    JSONObject jsonObject2;
                    for(int i=0; i<namaPilihan.size(); i++){
                        args = new ArrayList<>();
                        args.add(new BasicNameValuePair("tag","insert_pilihan_jawaban"));
                        args.add(new BasicNameValuePair("idSoal",jsonObject.getString(TAG_ITEMS)));
                        args.add(new BasicNameValuePair("namaPilihan",namaPilihan.get(i)));
                        jsonObject2 = jsonParser.makeHttpRequest(urlWebService, "POST", args);
                        success2 = jsonObject2.getInt(TAG_SUCCESS);
                    }
                    GcmSender sender = new GcmSender();
                    sender.sendNotification(jsonObject.getString(TAG_CHANNEL),
                            "Terdapat pemilihan baru yang dapat dipilih. Sentuh untuk memilih sekarang!",
                            "Aplikasi E-Voting",
                            jsonObject.getString(TAG_ITEMS));
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            pDialog.dismiss();
            if(success2 == 1){
                Toast.makeText(getApplicationContext(), "Pertanyaan terkirim", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "Pertanyaan gagal terkirim", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class checkLevel extends AsyncTask<String,String,String> {
        int success = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BuatVote.this);
            pDialog.setMessage("Memuat formulir...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                List<NameValuePair> args = new ArrayList<>();
                args.add(new BasicNameValuePair("tag","get_level"));
                args.add(new BasicNameValuePair("idUser",idPembuat));
                JSONObject jsonObject = jsonParser.makeHttpRequest(urlWebService, "POST", args);

                success = jsonObject.getInt(TAG_SUCCESS);

                if(success == 1) {
                    level = jsonObject.getString("items").toLowerCase();
                }


            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            pDialog.dismiss();
            if(success == 1) {
                if(level.equals("camat")){
                    for (KategoriEnum temp:KategoriEnum.values()) {
                        spinnerArray.add(temp);
                    }
                    spinAdapter.notifyDataSetChanged();
                }
                else if(level.equals("lurah")){
                    for (KategoriEnumLurah temp:KategoriEnumLurah.values()) {
                        spinnerArray.add(temp);
                    }
                    spinAdapter.notifyDataSetChanged();
                }
                else if(level.equals("rw")){
                    for (KategoriEnumRW temp:KategoriEnumRW.values()) {
                        spinnerArray.add(temp);
                    }
                    spinAdapter.notifyDataSetChanged();
                }
                else if(level.equals("rt")){
                    for (KategoriEnumRT temp:KategoriEnumRT.values()) {
                        spinnerArray.add(temp);
                    }
                    spinAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(BuatVote.this, "Warga tidak boleh membuat voting", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
