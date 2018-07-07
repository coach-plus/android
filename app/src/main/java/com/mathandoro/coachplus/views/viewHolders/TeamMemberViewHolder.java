package com.mathandoro.coachplus.views.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.models.TeamMember;
import com.squareup.picasso.Picasso;

public class TeamMemberViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    ImageView icon;
    ImageView whistle;
    ImageButton attend;
    ImageButton dontAttend;

    public TeamMemberViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.member_item_name);
        icon = view.findViewById(R.id.member_item_icon);
        whistle = view.findViewById(R.id.member_item_whistle);
        attend = view.findViewById(R.id.member_item_attend);
        dontAttend = view.findViewById(R.id.member_item_dont_attend);
    }

    public void bind(TeamMember teamMember, JWTUser myUser, boolean attendenceMode){
        String username = teamMember.getUser().getFirstname() + " " + teamMember.getUser().getLastname();
        if(myUser != null && myUser.get_id().equals(teamMember.getUser().get_id())){
            username += " (" + "you" + ")";
        }
        name.setText(username);

        if(!teamMember.getRole().equals("coach")){
            whistle.setVisibility(View.GONE);
        }

        String userImage = teamMember.getUser().getImage();
        if(userImage != null){
            String imageUrl = BuildConfig.BASE_URL + "/uploads/" + userImage;
            Picasso.with(itemView.getContext())
                    .load(imageUrl)
                    .resize(Settings.USER_ICON_SIZE, Settings.USER_ICON_SIZE)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.circle)
                    .into(icon);
        }
        else{
            icon.setImageResource(R.drawable.circle);
        }

        if(!attendenceMode){
            dontAttend.setVisibility(View.GONE);
            attend.setVisibility(View.GONE);
        }
    }
}