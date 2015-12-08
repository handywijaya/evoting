package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

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

import com.example.handy.audy.daud.alfian.prototype_lomba.R;

import java.util.ArrayList;
import java.util.List;

public class LihatVote extends AppCompatActivity {

    ListView lvVoting;
    Button btnKembali;
    List<String> pertanyaanVoting;
    List<String> idVoting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_vote);
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

        pertanyaanVoting = new ArrayList<String>();
        idVoting = new ArrayList<String>();

        for(int i = 0; i<5; i++) {
            pertanyaanVoting.add("Pertanyaan " + i);
            idVoting.add("0000" + i);
        }

        lvVoting = (ListView) findViewById(R.id.lvVoting);
        lvVoting.setTextFilterEnabled(true);
        lvVoting.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pertanyaanVoting));

        lvVoting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), Voting.class);
                //i.putExtra("idVoting", idVoting.get(position));
                i.putExtra("pertanyaanVoting", pertanyaanVoting.get(position));
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
