package com.achimala.util;

public interface Callback<T> {
    public void onCompletion(T result);
    public void onError(Exception ex);
}