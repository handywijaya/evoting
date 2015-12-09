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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.adapter.ListPilihanAdapter;
import com.example.handy.audy.daud.alfian.prototype_lomba.jsonparser.JSONParser;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.Pilihan;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.Soal;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BuatVote extends AppCompatActivity {


    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    //private static String urlWebService = "http://xalvsx.esy.es/api/index.php";
    private static String urlWebService = "http://10.0.2.2:81/Lomba/index.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ITEMS = "items";
    private int flag = 0;

    EditText txtPertanyaan, txtTanggalMulai, txtTanggalSelesai;
    Button btnTambahPilihan, btnKembali, btnKirim;
    ListView lviPilihan;
    int flagKeyboard = 0;
    Calendar calendar = Calendar.getInstance();
    List<Pilihan> pilihan;
    ListPilihanAdapter adapter;
    List<String> namaPilihan;
    String isiSoal, tanggalMulaiString, tanggalSelesaiString, idPembuat;
    Spinner spnKategori;

    private void updateLabelMulai() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        txtTanggalMulai.setText(sdf.format(calendar.getTime()));
    }

    private void updateLabelSelesai() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        txtTanggalSelesai.setText(sdf.format(calendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_vote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pilihan = new ArrayList<Pilihan>();
        namaPilihan = new ArrayList<String>();
        idPembuat = "1";

        spnKategori = (Spinner)findViewById(R.id.spnKategori);
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        spnKategori.setAdapter(spinAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        //buat ambil state keyboard buka/tutup
        /*final RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                layout.getWindowVisibleDisplayFrame(r);

                int heightDiff = layout.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                    flagKeyboard = 1;
                }
                else {
                    flagKeyboard = 0;
                }
            }
        });*/

        lviPilihan = (ListView)findViewById(R.id.lviPilihan);

        txtPertanyaan = (EditText) findViewById(R.id.txtPertanyaan);

        txtTanggalMulai = (EditText) findViewById(R.id.txtTanggalMulai);
        txtTanggalMulai.setInputType(InputType.TYPE_NULL);
        //soft keyboard
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        txtTanggalMulai.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    new DatePickerDialog(BuatVote.this, tanggalMulai, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        txtTanggalSelesai = (EditText) findViewById(R.id.txtTanggalSelesai);
        txtTanggalSelesai.setInputType(InputType.TYPE_NULL);
        txtTanggalSelesai.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    new DatePickerDialog(BuatVote.this, tanggalSelesai, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        btnTambahPilihan = (Button) findViewById(R.id.btnTambahPilihan);
        btnKembali = (Button) findViewById(R.id.btnKembali);
        btnKirim = (Button) findViewById(R.id.btnKirim);

        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isiSoal = txtPertanyaan.getText().toString();

                // Ambil semua pilihan di sini
                namaPilihan.clear();
                for(int i=0; i<adapter.getCount(); i++){
                    namaPilihan.add(((TextView)adapter.getItem(i)).getText().toString());
                }

                tanggalMulaiString = txtTanggalMulai.getText().toString();
                tanggalSelesaiString = txtTanggalSelesai.getText().toString();

                if(!isiSoal.trim().equals("") && !tanggalMulaiString.trim().equals("") && !tanggalSelesaiString.trim().equals("")) {
                    new InsertPertanyaan().execute();
                    Snackbar.make(v, "Pertanyaan terkirim", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        btnTambahPilihan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Pilihan p = new Pilihan();
                    p.setIdPilihan("");
                    p.setIdSoal("");
                    p.setNamaPilihan("");
                    pilihan.add(p);

                    adapter = new ListPilihanAdapter(getApplicationContext(), pilihan);
                    lviPilihan.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class InsertPertanyaan extends AsyncTask<String,String,String> {
        int success, success2 = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BuatVote.this);
            pDialog.setMessage("Memasukkan pertanyaan baru");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("tag","create_voting"));
            args.add(new BasicNameValuePair("isiSoal",isiSoal));
            //args.add(new BasicNameValuePair("kategori",idUser));
            args.add(new BasicNameValuePair("idPembuat", idPembuat));
            args.add(new BasicNameValuePair("tanggal_mulai",tanggalMulaiString));
            args.add(new BasicNameValuePair("tanggal_selesai", tanggalSelesaiString));
            //args.add(new BasicNameValuePair("channel", idUser));
            JSONObject jsonObject = jsonParser.makeHttpRequest(urlWebService, "POST", args);

            try {
                success = jsonObject.getInt(TAG_SUCCESS);

                if(success == 1) {
                    JSONObject jsonObject2;
                    for(int i=0; i<namaPilihan.size(); i++){
                        args = new ArrayList<NameValuePair>();
                        args.add(new BasicNameValuePair("tag","insert_pilihan_jawaban"));
                        args.add(new BasicNameValuePair("idSoal",jsonObject.getString(TAG_ITEMS)));
                        args.add(new BasicNameValuePair("namaPilihan",namaPilihan.get(i)));
                        jsonObject2 = jsonParser.makeHttpRequest(urlWebService, "POST", args);
                        success2 = jsonObject2.getInt(TAG_SUCCESS);
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            pDialog.dismiss();
            if(flag == 1){
                //Toast.makeText(getApplicationContext(), "New Account Successfully Made", Toast.LENGTH_SHORT).show();
            }
            else{
                //Toast.makeText(getApplicationContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
