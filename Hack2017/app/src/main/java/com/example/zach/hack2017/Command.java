package com.example.zach.hack2017;


public interface Command<T> {
    void execute(T data);
}
