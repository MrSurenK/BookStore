package com.example.bookstore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Res<T> {

    private boolean success;
    private String message;
    private T data;
    private String error;

    //Constructor for response dto
    public Res(boolean success, String message, T data, String error){
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    public static <T> Res<T> success(T data, String message){
        return new Res<>(true,message, data, null);
    }

    public static <T> Res<T> error(String message, String error){
        return new Res<>(false, message, null, error);
    }
}

