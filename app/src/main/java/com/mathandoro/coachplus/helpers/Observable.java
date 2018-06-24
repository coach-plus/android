package com.mathandoro.coachplus.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dominik on 02.06.18.
 */

public class Observable<T> {
    protected List<SuccessSubscriber<T>> successSubscribers = new ArrayList<>();
    protected List<ErrorSubscriber> errorSubscribers = new ArrayList<>();
    protected T cache;
    private boolean cacheEnabled = false;

    public Observable() {
    }

    public Observable(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }


    public void emit(T data) {
        if (cacheEnabled) {
            this.cache = data;
        }
        for (SuccessSubscriber<T> subscriber : this.successSubscribers) {
            subscriber.onEvent(data);
        }
    }

    public void emitError(Error error) {
        for (ErrorSubscriber subscriber : this.errorSubscribers) {
            subscriber.onError(error);
        }
    }

    public Subscription subscribe(SuccessSubscriber<T> subscriber) {
        return this.subscribe(subscriber, null);
    }

    public Subscription subscribe(SuccessSubscriber<T> subscriber, ErrorSubscriber errorSubscriber) {
        if (subscriber != null) {
            this.successSubscribers.add(subscriber);
            if (cacheEnabled && cache != null) {
                subscriber.onEvent(cache);
            }
        }

        if (errorSubscriber != null) {
            this.errorSubscribers.add(errorSubscriber);
        }
        return new Subscription(this, subscriber, errorSubscriber);
    }

    public void unsubscribe(SuccessSubscriber<T> subscriber) {
        this.successSubscribers.remove(subscriber);
    }


    public void unsubscribeError(ErrorSubscriber subscriber) {
        this.errorSubscribers.remove(subscriber);
    }

    public void dispose() {
        this.successSubscribers = null;
        this.errorSubscribers = null;
        this.cache = null;
    }

    public static Observable<Object[]> all(Observable[] observables) {
        List<Subscription> subscriptions = new ArrayList<>();
        final AtomicInteger successCounter = new AtomicInteger();
        final Object[] results = new Object[observables.length];

        Observable<Object[]> observable = new Observable() {};

        for (int i = 0; i < observables.length; i++) {
            Observable o = observables[i];
            int finalI = i;
            Subscription subscription = o.subscribe((Object data) -> {
                results[finalI] = data;
                successCounter.getAndIncrement();
                if (successCounter.get() == observables.length) {
                    observable.emit(results);
                    successCounter.set(0);
                }
            }, (Error error) -> {
                successCounter.set(0);
                for (int j = 0; j < observables.length; j++) {
                    results[j] = null;
                }
                observable.emitError(error);
            });
            subscriptions.add(subscription);
        }
        return observable;
    }

}
