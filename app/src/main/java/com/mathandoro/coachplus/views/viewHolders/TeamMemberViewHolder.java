package com.mathandoro.coachplus.views.viewHolders;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.Const;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.models.User;
import com.mathandoro.coachplus.views.EventDetail.ParticipationItem;
import com.squareup.picasso.Picasso;

public class TeamMemberViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    ImageView icon;
    ImageView whistle;
    ImageButton attend;
    ImageButton dontAttend;
    public final int MAX_NAME_LENGTH = 14;

    public TeamMemberViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.member_item_name);
        icon = view.findViewById(R.id.member_item_icon);
        whistle = view.findViewById(R.id.member_item_whistle);
        attend = view.findViewById(R.id.member_item_attend);
        dontAttend = view.findViewById(R.id.member_item_dont_attend);
    }

    public void bind(TeamMember teamMember, JWTUser myUser){
        attend.setVisibility(View.GONE);
        dontAttend.setVisibility(View.GONE);
        ReducedUser user = teamMember.getUser();
        String username = user.getFirstname() + " " + user.getLastname();
        if(username.length() > MAX_NAME_LENGTH){
            username = user.getFirstname() + " " + user.getLastname().substring(0,1) +".";
        }
        if(myUser != null && myUser.get_id().equals(user.get_id())){
            username += " (" + itemView.getContext().getString(R.string.you) + ")";
        }
        name.setText(username);

        if(!teamMember.getRole().equals(Const.Role.Coach.toString())){
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
    }

    public void bindParticipationMode(ParticipationItem item, JWTUser myUser, ITeamMemberViewHolderEvent attendPressed, ITeamMemberViewHolderEvent dontAttendPressed){
        this.bind(item.getTeamMember(), myUser);
        attend.setVisibility(View.VISIBLE);
        dontAttend.setVisibility(View.VISIBLE);

        if(item.getParticipation() != null){
            if(item.getParticipation().WillAttend()){
                attend.setColorFilter(Color.rgb(0,255,0));
                dontAttend.setColorFilter(null);
            }
            else{
                dontAttend.setColorFilter(Color.rgb(255,0,0));
                attend.setColorFilter(null);
            }
        }
        // todo
        // if event has not started -> willAttend for myself
        // if event has started
        attend.setOnClickListener(view -> {
            attendPressed.onPressed();
        });
        dontAttend.setOnClickListener(view -> {
            dontAttendPressed.onPressed();
        });
    }
}