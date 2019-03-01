package com.mathandoro.coachplus.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
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
import com.mathandoro.coachplus.views.layout.ImagePickerView;
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

public class TeamRegistrationActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener, ImagePickerView.ImagePickerListener {

    protected Settings settings;
    protected ImagePickerView imagePickerView;
    protected ToolbarFragment toolbarFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_team_activity);
        settings = new Settings(this);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();


        FloatingActionButton registerTeamButton = findViewById(R.id.create_team_create_button);
        imagePickerView = findViewById(R.id.teamImageView);
        imagePickerView.setEditable(true);
        imagePickerView.setListener(this);

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
                registerTeam(teamNameEditText.getText().toString(), registerTeamPublicToggleButton.isChecked(), imagePickerView.getSelectedImageBase64())
        );
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

    @Override
    public void onPickImage() {
        this.pickImage();
    }

    private void pickImage(){
        ImagePickerView.startImagePickerIntent(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            imagePickerView.onActivityResult(resultCode, data).subscribe();
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
