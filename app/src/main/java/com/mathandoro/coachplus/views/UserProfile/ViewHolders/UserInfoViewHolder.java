package com.mathandoro.coachplus.views.UserProfile.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.ReducedUser;
import com.squareup.picasso.Picasso;

/**
 * Created by dominik on 18.02.18.
 */

public class UserInfoViewHolder extends RecyclerView.ViewHolder {
    ImageView teamImage;
    TextView usernameText;

    public UserInfoViewHolder(View view) {
        super(view);
        teamImage = (ImageView)view.findViewById(R.id.user_profile_user_image);
        usernameText = (TextView)view.findViewById(R.id.user_profile_username);
    }

    public void bind(ReducedUser user){
        usernameText.setText(user.getFirstname() + " " + user.getLastname());
        String imageUrl = BuildConfig.BASE_URL + "/uploads/" + user.getImage();
        Picasso.with(this.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.circle)
                .into(this.teamImage);
    }
}