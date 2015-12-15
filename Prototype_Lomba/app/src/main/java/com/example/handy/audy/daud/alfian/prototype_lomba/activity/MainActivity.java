package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.gcm.service.MyGcmListenerService;
import com.example.handy.audy.daud.alfian.prototype_lomba.gcm.service.MyInstanceIDListenerService;
import com.example.handy.audy.daud.alfian.prototype_lomba.gcm.service.RegistrationIntentService;

public class MainActivity extends BaseActivity {
    ViewGroup btnBuatVote,btnLihatVote, btnRiwayatVote, btnProfile, btnLogout;
    String idUser,idKtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        idUser = getIntent().getStringExtra("idUser");
        idKtp = getIntent().getStringExtra("idKtp");

        if(getIntent().getStringExtra("idSoal") != null) { //buat nangkep notif, kirim aja extra idSoal di intentnya, supaya di redirect ke Voting langsung
            String idSoal = getIntent().getStringExtra("idSoal");
            boolean logged_in = sharedPreferences.getBoolean("logged_in", false);
            if(logged_in) {
                idKtp = sharedPreferences.getString("idKtp", null);
                idUser = sharedPreferences.getString("idUser", null);
                Intent i = new Intent(getApplicationContext(), Voting.class);
                i.putExtra("idKtp", idKtp);
                i.putExtra("idUser", idUser);
                i.putExtra("idPertanyaan", idSoal);
                i.putExtra("pertanyaanVoting", ""); //biarin kosong
                startActivity(i);
            }
            else {
                //kalo belom login, aneh harusnya notif ga masuk, tapi yaudah suruh login aja abis itu cek sendiri di Lihat Pilihan Voting
                Intent i = new Intent(getApplicationContext(),StartActivity.class);
                startActivity(i);
                finish();
            }
        }

        btnBuatVote = (ViewGroup) findViewById(R.id.btnVotingBaru);
        btnBuatVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BuatVote.class);
                i.putExtra("idKtp",idKtp);
                i.putExtra("idUser",idUser);
                startActivity(i);
            }
        });

        btnLihatVote = (ViewGroup) findViewById(R.id.btnLihatVote);
        btnLihatVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LihatVote.class);
                i.putExtra("idKtp",idKtp);
                i.putExtra("idUser",idUser);
                startActivity(i);
            }
        });

        btnRiwayatVote = (ViewGroup) findViewById(R.id.btnRiwayatVoting);
        btnRiwayatVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RiwayatVote.class);
                i.putExtra("idKtp",idKtp);
                i.putExtra("idUser",idUser);
                startActivity(i);
            }
        });

        btnProfile = (ViewGroup) findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                i.putExtra("idKtp",idKtp);
                i.putExtra("idUser",idUser);
                startActivity(i);
            }
        });

        btnLogout = (ViewGroup)findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("logged_in", false);
                editor.commit();



                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                i.putExtra("stopService",true);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
