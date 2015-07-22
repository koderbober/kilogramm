package com.kilograpp.kilogrammmusic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ghost on 20.07.2015.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<Song> songList;

    public RecyclerViewAdapter(List<Song> songList) {
        this.songList = songList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Song song = songList.get(i);
        viewHolder.song.setText(song.getName());
        viewHolder.author.setText(song.getAuthor());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void removeItem(int position) {
        songList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(Song song) {
        songList.add(song);
        notifyItemInserted(songList.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView song;
        private TextView author;

        public ViewHolder(View itemView) {
            super(itemView);
            song = (TextView) itemView.findViewById(R.id.tvSong);
            author = (TextView) itemView.findViewById(R.id.tvAuthor);
        }
    }
}