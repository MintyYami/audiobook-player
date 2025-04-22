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
import com.example.cw_3.util;

import java.util.List;

public class BookmarkRecylerViewAdaptor extends RecyclerView.Adapter<BookmarkRecylerViewAdaptor.bookmarkViewHolder>{
    private List<Audio> bookmarkList;
    private Context context;
    private int colour;
    private LayoutInflater layoutInflater;
    private onBookmarkListener onBookmarkListener;

    public BookmarkRecylerViewAdaptor(Context context, List<Audio> bookmarkList, int colour, onBookmarkListener onBookmarkListener){
        this.bookmarkList = bookmarkList;
        this.context = context;
        this.colour = colour;
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutInflater = LayoutInflater.from(context);
        this.onBookmarkListener = onBookmarkListener;
    }

    @Override
    public BookmarkRecylerViewAdaptor.bookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.bookmark_layout, parent, false);
        return new BookmarkRecylerViewAdaptor.bookmarkViewHolder(itemView, onBookmarkListener);
    }

    @Override
    public void onBindViewHolder(@NonNull bookmarkViewHolder holder, int position) {
        holder.bind(bookmarkList.get(position), colour);
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    class bookmarkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView audioNameView;
        private TextView audioProgView;
        onBookmarkListener onBookmarkListener;
        private Audio audio;
        bookmarkViewHolder(View itemView, onBookmarkListener onBookmarkListener) {
            super(itemView);
            // set the text view style according to themeMode
            this.audioNameView = itemView.findViewById(R.id.textAudioName);
            this.audioNameView.setTextColor(colour);
            this.audioProgView = itemView.findViewById(R.id.textAudioProgress);
            this.audioProgView.setTextColor(colour);

            // store click listener
            this.onBookmarkListener = onBookmarkListener;
        }

        void bind(final Audio audio, int colour) {
            audioNameView.setText(audio.getAudioName());
            audioNameView.setTextColor(colour);
            audioProgView.setText(util.getFormatProg(audio.getAudioProgress()));
            audioProgView.setTextColor(colour);

            this.audio = audio;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onBookmarkListener.onBookmarkClick(getAdapterPosition(), audio.getAudioName() , audio.getAudioPath());
        }
    }

    public interface onBookmarkListener{
        void onBookmarkClick(int pos, String name, String filepath);
    }
}
