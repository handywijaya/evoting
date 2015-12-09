package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;

public class MainActivity extends AppCompatActivity {

    Button btnBuatVote, btnLihatVote, btnRiwayatVote, btnProfile;
    String idUser,idKtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        idUser = getIntent().getStringExtra("idUser");
        idKtp = getIntent().getStringExtra("idKtp");

        btnBuatVote = (Button) findViewById(R.id.btnVotingBaru);
        btnBuatVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BuatVote.class);
                i.putExtra("idKtp",idKtp);
                i.putExtra("idUser",idUser);
                startActivity(i);
            }
        });

        btnLihatVote = (Button) findViewById(R.id.btnLihatVote);
        btnLihatVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LihatVote.class);
                i.putExtra("idKtp",idKtp);
                i.putExtra("idUser",idUser);
                startActivity(i);
            }
        });

        btnRiwayatVote = (Button) findViewById(R.id.btnRiwayatVoting);
        btnRiwayatVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RiwayatVote.class);
                i.putExtra("idKtp",idKtp);
                i.putExtra("idUser",idUser);
                startActivity(i);
            }
        });

        btnProfile = (Button) findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                i.putExtra("idKtp",idKtp);
                i.putExtra("idUser",idUser);
                startActivity(i);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
