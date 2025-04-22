package com.example.cw_3.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw_3.R;
import com.example.cw_3.object.Audio;

import java.util.List;

public class AudioRecyclerViewAdaptor extends RecyclerView.Adapter<AudioRecyclerViewAdaptor.audioViewHolder> {
    private List<Audio> audioList;
    private Context context;
    private int colour;
    private LayoutInflater layoutInflater;
    private onAudioListener onAudioListener;

    public AudioRecyclerViewAdaptor(Context context, List<Audio> audioList, int colour, onAudioListener onAudioListener){
        this.audioList = audioList;
        this.context = context;
        this.colour = colour;
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutInflater = LayoutInflater.from(context);
        this.onAudioListener = onAudioListener;
    }

    @Override
    public audioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.audio_layout, parent, false);
        return new audioViewHolder(itemView, onAudioListener);
    }

    @Override
    public void onBindViewHolder(@NonNull audioViewHolder holder, int position) {
        holder.bind(audioList.get(position), colour);
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    class audioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textView;
        onAudioListener onAudioListener;
        audioViewHolder(View itemView, onAudioListener onAudioListener) {
            super(itemView);
            // set the textview style according to themeMode
            this.textView = itemView.findViewById(R.id.textAudioName);
            this.textView.setTextColor(colour);

            // store click listener
            this.onAudioListener = onAudioListener;
        }

        void bind(final Audio audio, int colour) {
            textView.setText(audio.getAudioName());
            textView.setTextColor(colour);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onAudioListener.onAudioClick(getAdapterPosition());
        }
    }

    public interface onAudioListener{
        void onAudioClick(int pos);
    }
}
