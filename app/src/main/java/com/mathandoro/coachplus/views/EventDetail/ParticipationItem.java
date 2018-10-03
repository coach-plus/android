package com.mathandoro.coachplus.views.EventDetail;

import com.mathandoro.coachplus.models.Participation;
import com.mathandoro.coachplus.models.TeamMember;

public class ParticipationItem {
    protected TeamMember teamMember;
    protected Participation participation;

    public ParticipationItem(TeamMember teamMember, Participation participation) {
        this.teamMember = teamMember;
        this.participation = participation;
    }

    public TeamMember getTeamMember() {
        return teamMember;
    }

    public Participation getParticipation() {
        return participation;
    }
}
