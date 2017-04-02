package com.mathandoro.coachplus.data;

import android.content.Context;

import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.models.ApiResponse;
import com.mathandoro.coachplus.models.MyMembershipsResponse;
import com.mathandoro.coachplus.models.Membership;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DataLayer {

    static DataLayer instance;
    protected Context context;
    protected boolean offlineMode = false;
    protected Settings settings;

    protected DataLayerCallback<List<Membership>> getMyMembershipsCallback;


    private DataLayer(Context context){
        this.context = context;
        this.settings = new Settings(this.context);
    }

    public static DataLayer getInstance(Context context){
        if(instance == null){
            instance = new DataLayer(context.getApplicationContext());
        }
        return instance;
    }

    public void setOfflineMode(boolean offlineMode){
        this.offlineMode = offlineMode;
    }

    public void getMyMemberships(DataLayerCallback<List<Membership>> callback){
        this.getMyMembershipsCallback = callback;
        // todo load from cache
        if(this.offlineMode){
            return;
        }
        ApiClient.instance().membershipService.getMyMemberships(settings.getToken()).enqueue(new Callback<ApiResponse<MyMembershipsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MyMembershipsResponse>> call, Response<ApiResponse<MyMembershipsResponse>> response) {
                if(response.code() == 200){
                    if(DataLayer.this.getMyMembershipsCallback != null){
                        List<Membership> memberships = response.body().content.getMemberships();
                        DataLayer.this.getMyMembershipsCallback.dataChanged(memberships);
                        // todo save in cache
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MyMembershipsResponse>> call, Throwable t) {

            }
        });
    }
}
