package com.mathandoro.coachplus.persistence;

/**
 * Created by dominik on 31.03.17.
 */


public interface DataLayerCallback<T> {
    void dataChanged(T data);
    void error();
}
