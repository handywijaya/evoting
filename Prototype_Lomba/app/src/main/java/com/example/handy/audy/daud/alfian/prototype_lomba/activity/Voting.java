package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Voting extends BaseActivity {

    TextView tvPertanyaan;
    TableLayout tableLayout;
    RadioGroup rg;
    Button btnKembali, btnKirim;
    List<HashMap<String,String>> pilihanVoting;
    RadioButton[] radioButtons;
    EditText txtIdKtp;

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

    DialogPlus dialogp;

    NfcAdapter nfc;
    String[][] techListsArray;
    IntentFilter[] intentFiltersArray;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idPertanyaan = getIntent().getStringExtra("idPertanyaan");
        idUser = getIntent().getStringExtra("idUser");
        idKtp = getIntent().getStringExtra("idKtp");
        tvPertanyaan = (TextView) findViewById(R.id.tvPertanyaan);
        tvPertanyaan.setText(getIntent().getStringExtra("pertanyaanVoting"));
        Log.e("idUser",idUser);
        Log.e("idKtp",idKtp);

        rg = new RadioGroup(this);
        rg.setOrientation(LinearLayout.VERTICAL);
        pilihanVoting = new ArrayList<>();

        new LoadPertanyaan().execute();

        nfc = NfcAdapter.getDefaultAdapter(this);

        /* nanti buka
        if (nfc == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }*/
        if(nfc!=null) {
            pendingIntent = PendingIntent.getActivity(
                    this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {
                ndef.addDataType("text/plain");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new RuntimeException("fail", e);
            }
            intentFiltersArray = new IntentFilter[]{ndef};

            techListsArray = new String[][]{new String[]{NfcF.class.getName()}};
        }

        LinearLayout lay = new LinearLayout(Voting.this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lay.setLayoutParams(l);
        TextView tvTitle = new TextView(Voting.this);
        tvTitle.setTextSize(20);
        tvTitle.setText("Tempel kembali e-ktp anda");
        lay.addView(tvTitle);
        txtIdKtp = new EditText(Voting.this);
        txtIdKtp.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //txtIdKtp.setEnabled(false);
        txtIdKtp.setSingleLine(true);
        lay.addView(txtIdKtp);

        final EditText txtIdKtp2 = txtIdKtp;

        Button btnVerifikasi = new Button(Voting.this);
        btnVerifikasi.setText("Verifikasi");
        btnVerifikasi.setGravity(Gravity.CENTER);
        lay.addView(btnVerifikasi);

        btnVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idKtp2 = txtIdKtp.getText().toString();
                if (idKtp.equals(idKtp2)) {
                    dialogp.dismiss();
                    new SubmitJawaban().execute();
                }
                else {
                    Toast.makeText(Voting.this, "Data E-KTP tidak valid", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogp = DialogPlus.newDialog(Voting.this)
                .setContentHolder(new ViewHolder(lay))
                .setGravity(Gravity.CENTER)
                .setPadding(25, 25, 25, 25)
                .create();

    }

    private void readIdKtp() {
        selectedId = rg.getCheckedRadioButtonId();

        if (selectedId != -1 && dialogp.isShowing()) {
            String idKtp2 = txtIdKtp.getText().toString();
            if (idKtp.equals(idKtp2)) {
                dialogp.dismiss();
                new SubmitJawaban().execute();
            } else {
                Toast.makeText(Voting.this, "Data E-KTP tidak valid", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onPause() {
        super.onPause();
        if(nfc != null) {
            nfc.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfc != null) {
            nfc.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        }
    }

    public void onNewIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        new NdefReaderTask().execute(tagFromIntent);
        //do something with tagFromIntent
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            try {
                Tag tag = params[0];

                Ndef ndef = Ndef.get(tag);
                if (ndef == null) {

                    // NDEF is not supported by this Tag.
                    return null;
                }
                NdefMessage ndefMessage = ndef.getCachedNdefMessage();

                NdefRecord[] records = ndefMessage.getRecords();
                for (NdefRecord ndefRecord : records) {
                    if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                        try {
                            return readText(ndefRecord);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();
            String a = "UTF-8";
            String b = "UTF-16";
            String textEncoding = ((payload[0] & 128) == 0) ? a : b;
            int languageCodeLength = payload[0] & 0063;

            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if(result.equals("kosong")) {
                    Toast.makeText(Voting.this, "Data kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    txtIdKtp.setText(result);
                    readIdKtp();
                }
            }
        }
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

                radioButtons.setId(Integer.valueOf(id));
                radioButtons.setText(pilihan.get(TAG_NAMAPILIHAN));
                rg.addView(radioButtons);
            }

            initialization();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<>();
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

                    List<NameValuePair> argsPilihan = new ArrayList<>();

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
                                HashMap<String, String> map = new HashMap<>();
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
                                    dialogp.show();
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
                Intent i = new Intent(getApplicationContext(),LihatVote.class);
                i.putExtra("idKtp", idKtp);
                i.putExtra("idUser", idUser);
                startActivity(i);
                finish();
            }
        });
    }


    static boolean isFinish = false;
    class SubmitJawaban extends AsyncTask<String, String, String>
    {
        int flag = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Voting.this);
            progressDialog.setMessage("Menyimpan Jawaban..");
            progressDialog.setIndeterminate(false);
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
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                Intent i = new Intent(getApplicationContext(),LihatVote.class);
                                i.putExtra("idKtp", idKtp);
                                i.putExtra("idUser", idUser);
                                if(!isFinish) {
                                    isFinish = false;
                                    startActivity(i);
                                }
                                finish();
                            }
                        })
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
            List<NameValuePair> args = new ArrayList<>();
            //idPertanyaan = "1";
            args.add(new BasicNameValuePair("idUser", idUser));
            args.add(new BasicNameValuePair("idSoal", idPertanyaan));
            args.add(new BasicNameValuePair("idPilihan", String.valueOf(selectedId)));

            args.add(new BasicNameValuePair("tag", "submit_voting"));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlWebService, "POST", args);
            Log.d("CreateResponse", jsonObject.toString());

            try
            {

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

    @Override
    public void onBackPressed() {
        btnKembali.callOnClick();
    }
}
