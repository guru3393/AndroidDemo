package com.contest.androiddemo.model;

public class NetworkResponse {
    private LoginResponse postData;
    private Throwable t;

    public NetworkResponse(LoginResponse postData){
        this.postData = postData;
        this.t = null;
    }

    public NetworkResponse(Throwable t){
        this.t = t;
        this.postData = null;
    }

    public LoginResponse getPostData() {
        return postData;
    }

    public Throwable getError() {
        return t;
    }

    public void setPostData(LoginResponse postData) {
        this.postData = postData;
    }

    public void setError(Throwable t) {
        this.t = t;
    }
}
