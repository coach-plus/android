package com.mathandoro.coachplus.persistence;

public interface DataLayerSuccessCallback<T> {
    void dataChanged(T data);
}
