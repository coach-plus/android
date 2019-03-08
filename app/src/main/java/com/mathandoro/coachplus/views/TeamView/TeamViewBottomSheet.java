package com.mathandoro.coachplus.views.TeamView;

import android.app.Dialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.View;
import android.widget.Button;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Role;

public class TeamViewBottomSheet extends BottomSheetDialogFragment {

    ITeamViewBottomSheetEvent listener;
    String newRole;


    public TeamViewBottomSheet(){
    }

    public static TeamViewBottomSheet newInstance(String newRole) {
        TeamViewBottomSheet fragment = new TeamViewBottomSheet();
        fragment.init(newRole);
        return fragment;
    }

    public void init(String newRole){
        this.newRole = newRole;
    }

    public interface ITeamViewBottomSheetEvent {
        void onKickUser();
        void onChangeRole(String newRole);
    }

    public void setListener(ITeamViewBottomSheetEvent listener) {
        this.listener = listener;
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.team_view_bottom_sheet, null);
        dialog.setContentView(contentView);
        String changeRoleText = newRole.equals(Role.COACH) ? getString(R.string.make_coach) : getString(R.string.make_user);

        Button button = dialog.findViewById(R.id.team_view_bottom_sheet_change_role);
        button.setText(changeRoleText);
        button.setOnClickListener(view -> listener.onChangeRole(newRole));

        Button kickButton = dialog.findViewById(R.id.team_view_bottom_sheet_kick_button);
        kickButton.setOnClickListener(view -> listener.onKickUser());
    }
}