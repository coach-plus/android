package com.mathandoro.coachplus.persistence;

import android.app.Activity;
import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.api.Request.CreateNewsRequest;
import com.mathandoro.coachplus.api.Request.DidAttendRequest;
import com.mathandoro.coachplus.api.Request.ResetPasswordRequest;
import com.mathandoro.coachplus.api.Request.UpdatePasswordRequest;
import com.mathandoro.coachplus.api.Request.UpdateRoleRequest;
import com.mathandoro.coachplus.api.Request.UpdateUserRequest;
import com.mathandoro.coachplus.api.Request.WillAttendRequest;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.api.Response.CreateEventResponse;
import com.mathandoro.coachplus.api.Response.GetEventResponse;
import com.mathandoro.coachplus.api.Response.GetNewsResponse;
import com.mathandoro.coachplus.api.Response.MembershipResponse;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.api.Response.ParticipationResponse;
import com.mathandoro.coachplus.api.Response.UpdateUserInformationResponse;
import com.mathandoro.coachplus.api.Response.EventsResponse;
import com.mathandoro.coachplus.api.Response.MyMembershipsResponse;
import com.mathandoro.coachplus.helpers.ApiErrorResolver;
import com.mathandoro.coachplus.helpers.Navigation;
import com.mathandoro.coachplus.helpers.ResourceHelper;
import com.mathandoro.coachplus.helpers.SnackbarHelper;
import com.mathandoro.coachplus.models.Device;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.Participation;
import com.mathandoro.coachplus.models.RegisterTeam;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.api.Response.TeamMembersResponse;
import com.mathandoro.coachplus.models.UserImageUpload;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;


public class DataLayer {

    static DataLayer instance;
    protected Context context;
    protected Cache cache;
    protected Settings settings;
    final String MY_MEMBERSHIPS = "myMemberships";
    final String EVENTS = "events";
    private Gson gson;
    private View rootView;
    private Activity contextActivity;


    private DataLayer(Activity contextActivity, Context context){
        this.contextActivity = contextActivity;
        this.context = context;
        try{
            this.rootView = contextActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        }
        catch(Exception error){
            this.rootView = null;
        }
        this.settings = new Settings(this.context);
        this.cache = new Cache(this.context);
        this.gson = new Gson();
    }


    public DataLayer(Activity contextActivity){
       this(contextActivity, contextActivity.getApplicationContext());
    }

    public DataLayer(Context context){
        this(null, context);
    }

    public Observable<GetNewsResponse> getNews(String teamId, String eventId){
        Call<ApiResponse<GetNewsResponse>> apiCall = ApiClient.instance().teamService.getNews(settings.getToken(), teamId, eventId);
        return this.apiCall(apiCall, false);
    }

    public Observable<Object> updatePassword(UpdatePasswordRequest updatePasswordRequest){
        Call<ApiResponse<Object>> updateUserPassword = ApiClient.instance().userService.updateUserPassword(settings.getToken(), updatePasswordRequest);
        return this.apiCall(updateUserPassword, false);
    }

    public Observable<Object> resetPassword(ResetPasswordRequest resetPasswordRequest){
        Call<ApiResponse<Object>> updateUserPassword = ApiClient.instance().userService.resetPassword(settings.getToken(), resetPasswordRequest);
        return this.apiCall(updateUserPassword, false);
    }

    public Observable<UpdateUserInformationResponse> updateUserInformation(UpdateUserRequest updateUserInformationRequest){
        Call<ApiResponse<UpdateUserInformationResponse>> updateUserInformation = ApiClient.instance().userService.updateUserInformation(settings.getToken(), updateUserInformationRequest);
        return this.apiCall(updateUserInformation, false);
    }

    public Observable<Object> sendReminder(String teamId, String eventId){
        // todo error: response contains author as userId instead of reduced user (in CreateNewsResponse)
        Call<ApiResponse<Object>> sendReminder = ApiClient.instance().teamService.createReminder(settings.getToken(), teamId, eventId);
        return this.apiCall(sendReminder, false);
    }

    public Observable<Object> createNews(String teamId, String eventId, String title, String text){
        // todo error: response contains author as userId instead of reduced user (in CreateNewsResponse)
        Call<ApiResponse<Object>> news1 = ApiClient.instance().teamService.createNews(settings.getToken(), teamId, eventId, new CreateNewsRequest(title, text));
        return this.apiCall(news1, false);
    }

    public Observable<Object> deleteNews(String teamId, String eventId, String newsId){
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().teamService.deleteNews(settings.getToken(), teamId, eventId, newsId);
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<ParticipationResponse> getParticipationOfEvent(boolean useCache, String teamId, String eventId){
        Call<ApiResponse<ParticipationResponse>> participationCall = ApiClient.instance().teamService.getParticipationOfEvent(settings.getToken(), teamId, eventId);
        return this.apiCall(participationCall, useCache);
    }

    public Observable<Participation> setWillAttend(String teamId, String eventId, String userId, boolean willAttend){
        Call<ApiResponse<Participation>> participationCall = ApiClient.instance().teamService.setWillAttend(settings.getToken(), teamId, eventId, userId, new WillAttendRequest(willAttend));
        return this.apiCall(participationCall, false);
    }

    // todo return type correct ?
    public Observable<MyUserResponse> uploadUserImage(String imageBase64){
        Call<ApiResponse<MyUserResponse>> apiResponseCall = ApiClient.instance().userService.updateUserImage(settings.getToken(), new UserImageUpload(imageBase64));
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Object> removeUserFromTeam(String membershipId){
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().membershipService.deleteMembership(settings.getToken(), membershipId);
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Membership> joinPrivateTeam(String invitationToken) {
        Call<ApiResponse<Membership>> apiResponseCall = ApiClient.instance().teamService.joinPrivateTeam(settings.getToken(), invitationToken);
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Membership> joinPublicTeam(String teamId){
        Call<ApiResponse<Membership>> apiResponseCall = ApiClient.instance().teamService.joinPublicTeam(settings.getToken(), teamId);
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Object> leaveTeam(String teamId){
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().teamService.leaveTeam(settings.getToken(), teamId);
        return this.apiCallWithSnackbar(apiResponseCall, false);
    }

    public Observable<Object> deleteTeam(String teamId){
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().teamService.deleteTeam(settings.getToken(), teamId);
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Team> updateTeam(String teamId, String teamName, boolean isPublic, String selectedImageBase64){
        Team updatedTeam = new Team(teamId, teamName, isPublic, selectedImageBase64);
        Call<ApiResponse<Team>> apiResponseCall = ApiClient.instance().teamService.updateTeam(settings.getToken(), teamId, updatedTeam);
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Object> updateRole( String membershipId, String role){
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().membershipService.setRole(settings.getToken(), membershipId, new UpdateRoleRequest((role)));
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Participation> setDidAttend(String teamId, String eventId, String userId, boolean didAttend){
        Call<ApiResponse<Participation>> participationCall = ApiClient.instance().teamService.setDidAttend(settings.getToken(), teamId, eventId, userId, new DidAttendRequest(didAttend));
        return this.apiCall(participationCall, false);
    }

    public Observable<MyMembershipsResponse> getMyMembershipsV2(boolean useCache){
        Call<ApiResponse<MyMembershipsResponse>> myUserCall = ApiClient.instance().membershipService.getMyMemberships(settings.getToken()); //.userService.getMyUser(settings.getToken());
        return this.apiCall(myUserCall, useCache);
    }

    public Observable<TeamMembersResponse> getTeamMembersV2(boolean useCache, Team team){
        Call<ApiResponse<TeamMembersResponse>> teamMembersCall = ApiClient.instance().teamService.getTeamMembers(settings.getToken(), team.get_id());
        return this.apiCall(teamMembersCall, useCache);
    }

    public void getMembershipsOfUser(String userId, final DataLayerCallback<List<Membership>> callback){
        ApiClient.instance().userService.getMembershipsOfUser(settings.getToken(), userId)
                .enqueue(new Callback<ApiResponse<MyMembershipsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MyMembershipsResponse>> call,
                                   Response<ApiResponse<MyMembershipsResponse>> response) {
                if(response.code() == 200){
                    if(callback != null){
                        List<Membership> memberships = response.body().content.getMemberships();
                        callback.dataChanged(memberships);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MyMembershipsResponse>> call, Throwable t) {
                callback.error();
            }
        });
    }

    public Observable<Object> registerOrUpdateDevice(String deviceId, String pushId){
        Device device = new Device(pushId, "android", deviceId);
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().userService.registerOrUpdateDevice(settings.getToken(), settings.getUser().get_id(), device);
        return apiCall(apiResponseCall, false);
    }

    public Observable<MyUserResponse> getMyUserV2(boolean useCache){
        Call<ApiResponse<MyUserResponse>> myUserCall = ApiClient.instance().userService.getMyUser(settings.getToken());
        return this.apiCall(myUserCall, useCache);
    }

    public Observable<MembershipResponse> getMyMembership(String membershipId, boolean useCache){
        Call<ApiResponse<MembershipResponse>> membership = ApiClient.instance().membershipService.getMembership(settings.getToken(), membershipId);
        return this.apiCall(membership, useCache);
    }

    public Observable<TeamMembersResponse> getTeamMembersV2(Team team, boolean useCache){
        String token = settings.getToken();
        Call<ApiResponse<TeamMembersResponse>> teamMembersCall = ApiClient.instance()
                .teamService.getTeamMembers(token, team.get_id());
        return this.apiCall(teamMembersCall, useCache);
    }

    public Observable<EventsResponse> getEvents(final Team team, boolean useCache) {
        Call<ApiResponse<EventsResponse>> eventsOfTeam = ApiClient.instance().teamService.getEventsOfTeam(settings.getToken(), team.get_id());
        return this.apiCall(eventsOfTeam, useCache);
    }

    public Observable<GetEventResponse> getEvent(String teamId, String eventId, boolean useCache) {
        Call<ApiResponse<GetEventResponse>> eventRequest = ApiClient.instance().teamService.getEvent(settings.getToken(), teamId, eventId);
        return this.apiCall(eventRequest, useCache);
    }

    public Observable<Object> deleteEvent(String teamId, String eventId){
        Call<ApiResponse<Object>> apiCall = ApiClient.instance().teamService.deleteEvent(settings.getToken(), teamId, eventId);
        return this.apiCall(apiCall, false);
    }

    public Observable<CreateEventResponse> createEvent(String teamId, Event event){
        Call<ApiResponse<CreateEventResponse>> apiCall = ApiClient.instance().teamService.createEvent(settings.getToken(), teamId, event);
        return this.apiCall(apiCall, false);
    }

    public Observable<CreateEventResponse> updateEvent(String teamId, Event event){
        Call<ApiResponse<CreateEventResponse>> apiCall = ApiClient.instance().teamService.updateEvent(settings.getToken(), teamId, event.get_id(), event);
        return this.apiCall(apiCall, false);
    }

    public Observable<Membership> registerTeam(String teamName, boolean isPublic, String teamImageBase64){
        RegisterTeam team = new RegisterTeam(teamName, isPublic, teamImageBase64);
        String token = settings.getToken();
        Call<ApiResponse<Membership>> registerTeamCall = ApiClient.instance().teamService.registerTeam(token, team);
        return this.apiCall(registerTeamCall, false);
    }



    private <T> Observable<T> apiCall(Call<ApiResponse<T>> t, boolean useCache){
        return this.apiCall(t, false, null);
    }


    private <T> Observable<T> apiCallWithSnackbar(Call<ApiResponse<T>> t, boolean useCache) {
        ApiErrorResolver apiErrorResolver = (errorCode) -> {
            SnackbarHelper.showText(rootView, errorCode);
        };
        if(rootView == null){
            // snackbar needs a rootView! Not possible in background tasks e.g. broadcast receiver
            return apiCall(t, useCache);
        }
        return apiCall(t,useCache, apiErrorResolver);
    }

    private <T> Observable<T> apiCall(Call<ApiResponse<T>> t, boolean useCache, ApiErrorResolver errorResolver){
        Observable<T> resultObservable = Observable.create(emitter -> {
            t.enqueue(new Callback<ApiResponse<T>>() {
                @Override
                public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                    if(response.code() >= 200 && response.code() < 300){
                        emitter.onNext(response.body().content);
                    }
                    else if(response.code() >= 400 && response.code() <= 599) {
                        if(response.code() == 403 && contextActivity != null){
                            Navigation.navigateToMembership(contextActivity, null);
                            contextActivity.finish();
                            return;
                        }
                        Converter<ResponseBody, ApiResponse<String>> converter =
                                ApiClient.instance().retrofit.responseBodyConverter(ApiResponse.class, new Annotation[0]);
                        try {
                            ApiResponse<String> error = converter.convert(response.errorBody());
                            if(errorResolver != null){
                                String textResource = ResourceHelper.getStringResourceByName(context, error.message);
                                errorResolver.resolve(textResource == null ? error.message : textResource);
                            }
                            else {
                                try{
                                    emitter.onError(new Throwable(error.message));
                                }
                                catch(Exception e){
                                    Log.d("error", "unhandled error: " + e.toString());
                                }
                            }
                        }catch(IOException ioError){
                            emitter.onError(new Throwable(""+response.code()));
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<T>> call, Throwable throwable) {
                    if(errorResolver != null){
                        errorResolver.resolve(throwable.getMessage());
                    }
                    if(emitter != null){
                        try{
                            emitter.onError(throwable);
                        }
                        catch(Exception e){
                            Log.d("error", "unhandled error: " + e.toString());
                        }
                    }
                }
            });
        });
        return resultObservable;
    }
}
