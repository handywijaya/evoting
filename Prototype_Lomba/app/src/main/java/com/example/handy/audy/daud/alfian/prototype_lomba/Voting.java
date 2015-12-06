package com.example.handy.audy.daud.alfian.prototype_lomba;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Voting extends AppCompatActivity {

    TextView pertanyaan;
    TableLayout tableLayout;
    RadioGroup rg;
    Button btnKembali, btnKirim;
    List<String> pilihanVoting;
    RadioButton[] radioButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
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

        pertanyaan = (TextView) findViewById(R.id.tvPertanyaan);
        pertanyaan.setText(getIntent().getStringExtra("pertanyaanVoting"));

        rg = new RadioGroup(this);
        rg.setOrientation(LinearLayout.VERTICAL);
        pilihanVoting = new ArrayList<>();
        radioButtons = new RadioButton[3];

        for(int i = 0; i<3; i++) {
            pilihanVoting.add("Pilihan " + i);
            radioButtons[i] = new RadioButton(this);
            radioButtons[i].setText(pilihanVoting.get(i));
            rg.addView(radioButtons[i]);
        }

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
                int selectedId = rg.getCheckedRadioButtonId();

                if (selectedId != -1) {
                    RadioButton selectedRb = (RadioButton) findViewById(selectedId);
                    Snackbar.make(v, "Berhasil memilih " + selectedRb.getText().toString(), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(v, "Pilih satu dari pilihan voting terlebih dahulu", Snackbar.LENGTH_SHORT).show();
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

}
