package com.mathandoro.coachplus.views.EventDetail.ViewHolders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.helpers.Formatter;
import com.mathandoro.coachplus.models.News;
import com.squareup.picasso.Picasso;

public class NewsItemViewHolder extends RecyclerView.ViewHolder {

    public TextView text;
    public TextView authorName;
    public ImageView authorIcon;
    public TextView created;
    public View itemContainer;

    public NewsItemViewHolder(View view) {
        super(view);
        this.itemContainer = view;
        this.text = view.findViewById(R.id.news_item_text);
        this.authorName = view.findViewById(R.id.news_item_author_name);
        this.authorIcon = view.findViewById(R.id.news_item_author_icon);
        this.created = view.findViewById(R.id.news_item_date);
    }

    public void bind(News news){
        this.authorName.setText(Formatter.formatUserName(news.getAuthor()));
        if(news.getAuthor().getImage() != null){
            Picasso.with(this.itemView.getContext()).load(news.getAuthor().getImage()).placeholder(R.drawable.circle);
        }
        this.created.setText(Formatter.formatGermanTimestamp(news.getCreated()));
        this.text.setText(news.getText());
    }

}
