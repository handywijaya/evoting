package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Voting extends BaseActivity {

    TextView tvPertanyaan;
    TableLayout tableLayout;
    RadioGroup rg;
    Button btnKembali, btnKirim;
    List<HashMap<String,String>> pilihanVoting;
    RadioButton[] radioButtons;

    String idPertanyaan, soal, idUser, idKtp;

    ProgressDialog progressDialog;
    private JSONArray pertanyaan, pilihanJawaban;


    private static final String TAG_IDSOAL= "ID_SOAL";
    private static final String TAG_ISISOAL = "ISI_SOAL";
    private static final String TAG_IDUSER = "ID_USER";
    private static final String TAG_TANGGALMULAI = "TANGGAL_MULAI";
    private static final String TAG_TANGGALSELESAI = "TANGGAL_SELESAI";
    private static final String TAG_CHANNEL = "channel";

    private static final String TAG_IDPILIHAN = "ID_PILIHAN";
    private static final String TAG_NAMAPILIHAN = "NAMA_PILIHAN";


    private int selectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idPertanyaan = getIntent().getStringExtra("idPertanyaan");
        //idPertanyaan = "1";
        idUser = getIntent().getStringExtra("idUser");
        idKtp = getIntent().getStringExtra("idKtp");
        tvPertanyaan = (TextView) findViewById(R.id.tvPertanyaan);
        tvPertanyaan.setText(getIntent().getStringExtra("pertanyaanVoting"));

        rg = new RadioGroup(this);
        rg.setOrientation(LinearLayout.VERTICAL);
        pilihanVoting = new ArrayList<>();

        new LoadPertanyaan().execute();



    }

    class LoadPertanyaan extends AsyncTask<String, String, String>
    {
        int flag = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Voting.this);
            progressDialog.setMessage("Mengambil Data..");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);

            progressDialog.dismiss();

            tvPertanyaan.setText(soal);
            for(HashMap<String, String> pilihan : pilihanVoting)
            {
                RadioButton radioButtons = new RadioButton(Voting.this);
                String id = pilihan.get(TAG_IDPILIHAN);
                Log.d("test", id);

                radioButtons.setId(Integer.valueOf(id));
                radioButtons.setText(pilihan.get(TAG_NAMAPILIHAN));
                rg.addView(radioButtons);
            }

            initialization();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            //idPertanyaan = "1";
            args.add(new BasicNameValuePair("idPertanyaan", idPertanyaan));
            args.add(new BasicNameValuePair("tag", "get_pertanyaan"));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlWebService, "POST", args);
            Log.d("CreateResponse", jsonObject.toString());

            try
            {
                int success = jsonObject.getInt(TAG_SUCCESS);
                if(success == 1)
                {
                    flag = 1;
                    pertanyaan = jsonObject.getJSONArray("items");
                    JSONObject p = pertanyaan.getJSONObject(0);

                    soal = p.getString(TAG_ISISOAL);

                    List<NameValuePair> argsPilihan = new ArrayList<NameValuePair>();

                    argsPilihan.add(new BasicNameValuePair("idPertanyaan", idPertanyaan));
                    argsPilihan.add(new BasicNameValuePair("tag", "get_pilihan_jawaban"));

                    JSONObject jsonObjectPilihan = jsonParser.makeHttpRequest(urlWebService, "POST", argsPilihan);
                    Log.d("CreateResponse", jsonObjectPilihan.toString());


                    try
                    {
                        pilihanJawaban = jsonObjectPilihan.getJSONArray("items");

                        int success2 = jsonObjectPilihan.getInt(TAG_SUCCESS);
                        if(success2 == 1)
                        {
                            for(int i = 0;i < pilihanJawaban.length(); i++)
                            {
                                JSONObject c = pilihanJawaban.getJSONObject(i);
                                String idPilihan = c.getString(TAG_IDPILIHAN);
                                String namaPilihan = c.getString(TAG_NAMAPILIHAN);
                                HashMap<String, String> map = new HashMap<String,String>();
                                map.put(TAG_IDPILIHAN, idPilihan);
                                map.put(TAG_NAMAPILIHAN, namaPilihan);
                                pilihanVoting.add(map);
                            }
                        }


                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }



                }
                else
                {
                    flag = 0;
                }
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }

            return null;
        }
    }

    public void initialization()
    {
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams btnParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(trParams);
        tr.addView(rg);

        TableRow tr1 = new TableRow(this);
        tr1.setLayoutParams(trParams);
        btnKembali = new Button(this);
        btnKembali.setLayoutParams(btnParams);
        btnKembali.setText("Kembali");
        btnKirim = new Button(this);
        btnKirim.setLayoutParams(btnParams);
        btnKirim.setText("Kirim");

        tr1.addView(btnKembali);
        tr1.addView(btnKirim);

        tableLayout.addView(tr);
        tableLayout.addView(tr1);

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedId = rg.getCheckedRadioButtonId();

                if (selectedId != -1) {
                    RadioButton selectedRb = (RadioButton) findViewById(selectedId);

                    new AlertDialog.Builder(Voting.this)
                            .setTitle("Konfirmasi Pemilihan")
                            .setMessage("Anda yakin memilih pemilihan Anda?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new SubmitJawaban().execute();
                                }
                            })
                            .setNegativeButton("Tidak", null)
                            .show();



                } else {
                    Snackbar.make(v, "Pilih satu dari pilihan jawaban terlebih dahulu", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LihatVote.class);
                startActivity(i);
                finish();
            }
        });
    }


    class SubmitJawaban extends AsyncTask<String, String, String>
    {
        int flag = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Voting.this);
            progressDialog.setMessage("Menyimpan Jawaban..");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            progressDialog.dismiss();


            if(flag == 1)
            {
                new AlertDialog.Builder(Voting.this)
                        .setTitle("Konfirmasi Pemilihan")
                        .setMessage("Terima kasih telah ikut berpartisipasi dalam pemilu. Silahkan lihat hasil pemilihan sementara di daftar riwayat pemilihan Anda")
                        .setPositiveButton("Ok", null)
                        .show();
            }

            else
            {
                new AlertDialog.Builder(Voting.this)
                        .setTitle("Konfirmasi Pemilihan")
                        .setMessage("Gagal menyimpan hasil pemilihan. Silahkan coba lagi")
                        .setPositiveButton("Ok", null)
                        .show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            //idPertanyaan = "1";
            args.add(new BasicNameValuePair("idUser", idUser));
            args.add(new BasicNameValuePair("idPilihan", String.valueOf(selectedId)));

            args.add(new BasicNameValuePair("tag", "submit_voting"));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlWebService, "POST", args);
            Log.d("CreateResponse", jsonObject.toString());

            try
            {
                JSONArray hasil = jsonObject.getJSONArray("items");

                int success = jsonObject.getInt(TAG_SUCCESS);
                if(success == 1)
                {
                    flag = 1;
                }
                else flag = 0;


            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }


            return null;
        }
    }
}
