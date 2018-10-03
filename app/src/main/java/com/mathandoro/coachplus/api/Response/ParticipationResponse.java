package com.mathandoro.coachplus.api.Response;

import com.mathandoro.coachplus.models.Participation;
import com.mathandoro.coachplus.models.User;

import java.util.List;

public class ParticipationResponse {
    public List<ParticipationAndMembership> participation;

    public class ParticipationAndMembership {
        public User user;
        public Participation participation;
    }
}
