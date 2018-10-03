package com.mathandoro.coachplus.views.viewHolders;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.Const;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.models.User;
import com.mathandoro.coachplus.views.EventDetail.ParticipationItem;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class TeamMemberViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    ImageView icon;
    ImageView whistle;
    ImageButton attend;
    ImageButton dontAttend;
    public final int MAX_NAME_LENGTH = 14;
    Context context;

    public TeamMemberViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.member_item_name);
        icon = view.findViewById(R.id.member_item_icon);
        whistle = view.findViewById(R.id.member_item_whistle);
        attend = view.findViewById(R.id.member_item_attend);
        dontAttend = view.findViewById(R.id.member_item_dont_attend);
        context = this.itemView.getContext();
    }

    public void bind(TeamMember teamMember, JWTUser myUser){
        attend.setVisibility(View.GONE);
        dontAttend.setVisibility(View.GONE);
        ReducedUser user = teamMember.getUser();
        String username = user.getFirstname() + " " + user.getLastname();
        if(username.length() > MAX_NAME_LENGTH){
            username = user.getFirstname() + " " + user.getLastname().substring(0,1) +".";
        }
        if(this.isMyUser(teamMember, myUser)){
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

    private boolean isMyUser(TeamMember teamMember, JWTUser myUser){
        return myUser != null && myUser.get_id().equals(teamMember.getUser().get_id());
    }

    public void bindParticipationMode(ParticipationItem item, JWTUser myUser, Event event, ITeamMemberViewHolderEvent attendPressed, ITeamMemberViewHolderEvent dontAttendPressed){
        int colorRed = ResourcesCompat.getColor(context.getResources(), R.color.colorRed, null);
        int colorGreen = ResourcesCompat.getColor(context.getResources(), R.color.colorGreen, null);
        this.bind(item.getTeamMember(), myUser);
        attend.setVisibility(View.VISIBLE);
        dontAttend.setVisibility(View.VISIBLE);
        itemView.setBackgroundColor(Color.WHITE);

        if(item.getParticipation() != null){
            if(item.getParticipation().WillAttend() != null && item.getParticipation().WillAttend()){
                attend.setColorFilter(colorGreen);
                dontAttend.setColorFilter(null);
            }
            else if(item.getParticipation().WillAttend() != null && !item.getParticipation().WillAttend()){
                dontAttend.setColorFilter(colorRed);
                attend.setColorFilter(null);
            }

        }
        if(event.getStart().before(new Date())){
            if(item.getParticipation() == null
                    ||
                    (item.getParticipation().WillAttend() == null
                            && (item.getParticipation().DidAttend() == null
                            || (item.getParticipation().DidAttend() != null && !item.getParticipation().DidAttend()))
                    )
                    ||
                    (item.getParticipation().DidAttend() != null
                            && item.getParticipation().WillAttend() != null
                            && item.getParticipation().WillAttend() == true
                            && item.getParticipation().DidAttend() == false)){
                itemView.setBackgroundColor(colorRed);
            }
        }

        attend.setOnClickListener(view -> {
            attendPressed.onPressed();
        });
        dontAttend.setOnClickListener(view -> {
            dontAttendPressed.onPressed();
        });
    }
}