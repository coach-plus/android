package com.mathandoro.coachplus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.models.RegisterTeam;
import com.mathandoro.coachplus.models.Response.ApiResponse;
import com.mathandoro.coachplus.models.Team;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterTeamActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener {

    final int TEAM_IMAGE_SIZE = 512;
    final int IMAGE_QUALITY = 95;
    protected Settings settings;
    protected ImageView teamImageView;
    protected boolean imageSelected = false;
    protected ToolbarFragment toolbarFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);
        settings = new Settings(this);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.my_memberships_fragment_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();


        Button registerTeamButton = (Button) findViewById(R.id.createTeamButton);
        teamImageView = (ImageView) findViewById(R.id.teamImageView);
        teamImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
        final EditText teamNameEditText = (EditText) findViewById(R.id.teamNameEditText);
        final CheckBox registerTeamPublicToggleButton = (CheckBox)findViewById(R.id.registerTeamPublicToggleButton);

        registerTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTeam(teamNameEditText.getText().toString(), registerTeamPublicToggleButton.isActivated(), getSelectedImageBase64());
            }
        });
    }

    String getSelectedImageBase64(){
        if(!imageSelected){
            return null;
        }
        BitmapDrawable bitmapDrawable = (BitmapDrawable)teamImageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, bos);
        byte[] bb = bos.toByteArray();
        String imageData = Base64.encodeToString(bb, Base64.DEFAULT);
        String base64ImagePrefix = "data:image/png;base64,";
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
                Picasso.with(teamImageView.getContext()).load(resultUri).transform(new CircleTransform()).into(teamImageView);
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
}
