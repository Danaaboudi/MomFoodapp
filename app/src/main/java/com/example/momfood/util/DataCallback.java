package com.example.momfood.util;

public interface DataCallback<T> {
    void onSuccess(T data);
    void onFailure(Exception e);
}
