package com.mathandoro.coachplus.helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 02.06.18.
 */

public class Observable<T> {
    protected List<Subscriber<T>> subscribers = new ArrayList<>();
    protected T cache;
    private boolean cacheEnabled = false;

    public Observable(){
    }

    public Observable(boolean cacheEnabled){
        this.cacheEnabled = cacheEnabled;
    }


    public void publish(T data){
        if(cacheEnabled){
            this.cache = data;
        }
        for(Subscriber<T> subscriber: this.subscribers){
            subscriber.onChange(data);
        }
    }

    public void subscribe(Subscriber<T> subscriber){
        this.subscribers.add(subscriber);
        if(cacheEnabled && cache != null){
            subscriber.onChange(cache);
        }
    }
}
