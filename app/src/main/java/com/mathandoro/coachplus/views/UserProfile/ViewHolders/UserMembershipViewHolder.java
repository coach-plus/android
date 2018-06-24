package com.mathandoro.coachplus.views.UserProfile.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.models.Membership;
import com.squareup.picasso.Picasso;

/**
 * Created by dominik on 18.02.18.
 */

public class UserMembershipViewHolder extends RecyclerView.ViewHolder {
    TextView teamNameText;
    ImageView teamImage;

    public UserMembershipViewHolder(View view) {
        super(view);
        teamNameText = (TextView)view.findViewById(R.id.user_profile_membership_item_team_name); // wrong
        teamImage = (ImageView) view.findViewById(R.id.user_profile_membership_item_team_icon);
    }

    public void bind(Membership membership){
        teamNameText.setText(membership.getTeam().getName());
        String image = membership.getTeam().getImage();
        if(image != null){
            String imageUrl = BuildConfig.BASE_URL + "/uploads/" + image;
            Picasso.with(this.itemView.getContext())
                    .load(imageUrl)
                    .resize(Settings.USER_ICON_SIZE, Settings.USER_ICON_SIZE)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.circle)
                    .into(this.teamImage);
        }
    }
}