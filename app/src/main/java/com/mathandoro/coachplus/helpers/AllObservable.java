package com.mathandoro.coachplus.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AllObservable<T> extends Observable {
    protected List<Subscription> subscriptions = new ArrayList<>();
    AtomicInteger successCounter = new AtomicInteger();
    Object[] results;
    Observable[] observables;


    public AllObservable(Observable[] observables) {
        this.observables = observables;
        results = new Object[observables.length];
        for (int i = 0; i < observables.length; i++) {
            Observable o = observables[i];
            int finalI = i;
            Subscription subscription = o.subscribe((Object data) -> {
                results[finalI] = data;
                successCounter.getAndIncrement();
                if (successCounter.get() == observables.length) {
                    this.emit(results);
                    successCounter.set(0);
                }
            }, (Error error) -> {
                successCounter.set(0);
                for (int j = 0; j < observables.length; j++) {
                    results[j] = null;
                }
                this.emitError(error);
            });
            subscriptions.add(subscription);
        }
    }



    @Override
    public void dispose() {
        super.dispose();
        successCounter = null;
        results = null;
        observables = null;
        for(Subscription subscription: subscriptions){
            subscription.unsubscribe();
        }
    }
}

