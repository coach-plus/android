package com.mathandoro.coachplus.helpers;

/**
 * Created by dominik on 02.06.18.
 */

public interface Subscriber<T> {
    void onChange(T data);
}
