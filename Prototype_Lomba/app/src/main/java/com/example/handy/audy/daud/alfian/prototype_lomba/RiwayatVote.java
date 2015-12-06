package com.example.handy.audy.daud.alfian.prototype_lomba;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class RiwayatVote extends AppCompatActivity {

    Button btnKembali;
    ListView lvBelum, lvSudah;

    List<String> pertanyaanBelum;
    List<String> idBelum;

    List<String> pertanyaanSudah;
    List<String> idSudah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_vote);
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

        pertanyaanBelum = new ArrayList<>();
        idBelum = new ArrayList<>();
        pertanyaanSudah= new ArrayList<>();
        idSudah = new ArrayList<>();

        int i = 0;
        for(;i<5;i++) {
            pertanyaanBelum.add("Pertanyaan " + i);
            idBelum.add("0000" + i);
        }
        for(;i<10;i++) {
            pertanyaanSudah.add("Pertanyaan " + i);
            idSudah.add("0000" + i);
        }

        lvBelum = (ListView) findViewById(R.id.lvBelum);
        lvBelum.setTextFilterEnabled(true);
        lvBelum.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pertanyaanBelum));
        lvBelum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), HasilVoting.class);
                i.putExtra("Riwayat Voting", "Belum");
                startActivity(i);
            }
        });

        lvSudah = (ListView) findViewById(R.id.lvSudah);
        lvSudah.setTextFilterEnabled(true);
        lvSudah.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pertanyaanSudah));
        lvSudah.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), HasilVoting.class);
                i.putExtra("Riwayat Voting", "Sudah");
                startActivity(i);
            }
        });

        btnKembali = (Button) findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

}
