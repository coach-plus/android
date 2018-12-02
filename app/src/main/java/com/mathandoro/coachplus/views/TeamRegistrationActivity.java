package com.mathandoro.coachplus.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.models.RegisterTeam;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamRegistrationActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener {

    final int TEAM_IMAGE_SIZE = 512;
    final int IMAGE_QUALITY = 95;
    protected Settings settings;
    protected ImageView teamImageView;
    protected boolean imageSelected = false;
    protected ToolbarFragment toolbarFragment;
    private Bitmap teamBitmap;
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            teamBitmap = bitmap;
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_team_activity);
        settings = new Settings(this);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();


        FloatingActionButton registerTeamButton = findViewById(R.id.create_team_create_button);
        teamImageView = findViewById(R.id.teamImageView);
        teamImageView.setOnClickListener((View view) -> pickImage());
        final EditText teamNameEditText = findViewById(R.id.teamNameEditText);
        final Switch registerTeamPublicToggleButton = findViewById(R.id.registerTeamPublicToggleButton);
        final TextView registerTeamVisibilityDescription = findViewById(R.id.registerTeamVisibilityDescription);

        registerTeamPublicToggleButton.setOnCheckedChangeListener((compoundButton,isPublic) -> {
            if(isPublic){
                registerTeamVisibilityDescription.setText(R.string.team_description_public);
            }
            else{
                registerTeamVisibilityDescription.setText(R.string.team_description_private);
            }
        });

        registerTeamButton.setOnClickListener((View v) ->
                registerTeam(teamNameEditText.getText().toString(), registerTeamPublicToggleButton.isChecked(), getSelectedImageBase64())
        );
    }

    String getSelectedImageBase64(){
        if(teamBitmap == null){
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        teamBitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, bos);
        byte[] bb = bos.toByteArray();
        String imageData = Base64.encodeToString(bb, Base64.DEFAULT);
        String base64ImagePrefix = "data:image/jpeg;base64,";
        return base64ImagePrefix + imageData;
    }

    void registerTeam(String teamName, boolean isPublic, String teamImageBase64){
        RegisterTeam team = new RegisterTeam(teamName, isPublic, teamImageBase64);
        String token = settings.getToken();
        Call<ApiResponse<Team>> registerTeamCall = ApiClient.instance().teamService.registerTeam(token, team);
        registerTeamCall.enqueue(new Callback<ApiResponse<Team>>() {
            @Override
            public void onResponse(Call<ApiResponse<Team>> call, Response<ApiResponse<Team>> response) {
                if(response.code() == 201){
                    success();
                }
                else{
                    fail();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Team>> call, Throwable t) {

                fail();
            }
        });
    }

    void pickImage(){
        CropImage.activity(null)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setRequestedSize(TEAM_IMAGE_SIZE, TEAM_IMAGE_SIZE)
                .setAspectRatio(1,1)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageSelected = true;
                Uri resultUri = result.getUri();
                RequestCreator requestCreator = Picasso.with(teamImageView.getContext()).load(resultUri);

                requestCreator.into(target);
                requestCreator.transform(new CircleTransform()).into(teamImageView);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    void success(){
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    void fail(){
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onLeftIconPressed() {
        finish();
    }

    @Override
    public void onRightIconPressed() {
    }
}
