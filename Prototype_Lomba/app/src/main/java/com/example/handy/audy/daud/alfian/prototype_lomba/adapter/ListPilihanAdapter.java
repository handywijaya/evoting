package com.example.handy.audy.daud.alfian.prototype_lomba.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.Pilihan;

import java.util.List;

/**
 * Created by Daud on 12/8/2015.
 */
public class ListPilihanAdapter extends BaseAdapter {

    private Context context;
    List<Pilihan> pilihan;
    TextView txtPilihan;

    public ListPilihanAdapter(Context context, List<Pilihan> pilihan) {
        this.context = context;
        this.pilihan = pilihan;
    }

    @Override
    public int getCount() {
        return pilihan.size();
    }

    @Override
    public Object getItem(int position) {
        return txtPilihan;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.pilihan_item, null);
        }

        Pilihan p = pilihan.get(position);

        if (p != null) {
            txtPilihan = (TextView) v.findViewById(R.id.txtPilihan);

            if (txtPilihan != null) {
                txtPilihan.setText(p.getNamaPilihan());
            }
        }

        return v;
    }
}
