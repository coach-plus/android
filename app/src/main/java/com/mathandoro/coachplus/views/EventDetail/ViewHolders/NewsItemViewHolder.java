package com.mathandoro.coachplus.views.EventDetail.ViewHolders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.helpers.Formatter;
import com.mathandoro.coachplus.models.News;
import com.squareup.picasso.Picasso;

public class NewsItemViewHolder extends RecyclerView.ViewHolder {

    public TextView text;
    public ImageView authorIcon;
    public TextView created;
    public View itemContainer;

    public NewsItemViewHolder(View view) {
        super(view);
        this.itemContainer = view;
        this.text = view.findViewById(R.id.news_item_text);
        this.authorIcon = view.findViewById(R.id.news_item_author_icon);
        this.created = view.findViewById(R.id.news_item_date);
    }

    public void bind(News news){
        if(news.getAuthor().getImage() != null){
            String imageUrl = BuildConfig.BASE_URL + "/uploads/" + news.getAuthor().getImage();
            Picasso.with(itemContainer.getContext())
                    .load(imageUrl)
                    .resize(Settings.TEAM_ICON_SIZE, Settings.TEAM_ICON_SIZE)
                    .placeholder(R.drawable.ic_user_black)
                    .transform(new CircleTransform())
                    .into(authorIcon);
        }
        this.created.setText(Formatter.formatUserName(news.getAuthor()) + " | " +Formatter.formatGermanTimestamp(news.getCreated()));
        this.text.setText(news.getText());
    }

}
