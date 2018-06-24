package com.mathandoro.coachplus.helpers;

public class Subscription {
    private SuccessSubscriber subscriber;
    private ErrorSubscriber errorSubscriber;
    private Observable observable;

    public Subscription(Observable observable, SuccessSubscriber subscriber, ErrorSubscriber errorSubscriber) {
        this.subscriber = subscriber;
        this.errorSubscriber = errorSubscriber;
        this.observable = observable;
    }

    public void unsubscribe(){
        if(this.subscriber != null){
            this.observable.unsubscribe(this.subscriber);
        }
        if(this.errorSubscriber != null){
            this.observable.unsubscribeError(this.errorSubscriber);
        }
        this.subscriber = null;
        this.errorSubscriber = null;
        this.observable = null;
    }
}
