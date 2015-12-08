package com.example.handy.audy.daud.alfian.prototype_lomba.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.Soal;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Alvs on 12/8/2015.
 */
public class ListSoalAdapter  extends RecyclerView.Adapter<ListSoalAdapter.ViewHolder> {

    private final Context context;
    private List<Soal> soal = new ArrayList<>();
    private OnItemClickListener itemClickListener;

    public ListSoalAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.soal_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void setData(List<Soal> s) {
        soal = s;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Soal s = soal.get(position);
        String judul = s.getJudul();
        holder.tvJudul.setText(judul);
        holder.tvKeterangan.setText("Tingkat : " + s.getKategori());
    }

    @Override
    public int getItemCount() {
        return soal.size();
    }

    public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public String getSoalID(int position) {
        return soal.get(position).getIdSoal();
    }

    public String getSoalName(int position) {
        return soal.get(position).getJudul();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'grid_movie_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.tvJudul)
        TextView tvJudul;
        @Bind(R.id.tvKeterangan)
        TextView tvKeterangan;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
