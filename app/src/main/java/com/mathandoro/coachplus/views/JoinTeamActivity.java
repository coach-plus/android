package com.mathandoro.coachplus.views;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.views.TeamView.TeamViewActivity;

import java.util.List;

public class JoinTeamActivity extends AppCompatActivity {

    private DataLayer dataLayer;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_team_activity);

        settings = new Settings(this);
        dataLayer = new DataLayer(this);

        Intent intent = getIntent();
        String action = intent.getAction();

        if(action == null){
            finish();
            return;
        }

        List<String> pathSegments = intent.getData().getPathSegments();
        String tokenOrTeamId = pathSegments.get(pathSegments.size()-1);

        if(pathSegments.size() < 5) {
            return;
        }

        String teamType = pathSegments.get(2);
        if(settings.getToken() == null){
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra("tokenOrTeamId", tokenOrTeamId);
            loginIntent.putExtra("teamType", teamType);
            return;
        }

        if(teamType.equals("private")) {
            dataLayer.joinPrivateTeam(tokenOrTeamId).subscribe(
                    membership -> navigateToMain(membership),
                    error -> navigateToMain(null));
        }
        else{
            dataLayer.joinPublicTeam(tokenOrTeamId).subscribe(
                    membership -> navigateToMain(membership),
                    error -> navigateToMain(null));
        }

    }

    void navigateToMain(Membership membership){
        Intent intent = new Intent(this, TeamViewActivity.class);
        intent.putExtra(TeamViewActivity.PARAM_MEMBERSHIP, membership);
        startActivity(intent);
        finish();
    }
}
