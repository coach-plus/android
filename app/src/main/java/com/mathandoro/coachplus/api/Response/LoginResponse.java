package com.mathandoro.coachplus.api.Response;

import com.mathandoro.coachplus.models.MyReducedUser;
import com.mathandoro.coachplus.models.ReducedUser;

/**
 * Created by dominik on 26.03.17.
 */

public class LoginResponse {
    public String firstname;
    public String lastname;
    public String token;
    public MyReducedUser user;
}
