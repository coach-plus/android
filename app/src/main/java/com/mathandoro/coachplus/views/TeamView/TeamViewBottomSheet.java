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


    private TeamViewBottomSheet(){
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
        String changeRoleText = newRole.equals(Role.COACH) ? getString(R.string.Make_Coach) : getString(R.string.Make_user);

        Button button = dialog.findViewById(R.id.confirmation_bottom_sheet_confirm_button);
        button.setText(changeRoleText);
        button.setOnClickListener(view -> listener.onChangeRole(newRole));

        Button kickButton = dialog.findViewById(R.id.confirmation_bottom_sheet_decline_button);
        kickButton.setText(R.string.Remove_from_team);
        kickButton.setOnClickListener(view -> listener.onKickUser());
    }
}