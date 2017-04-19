package com.artioml.githubclient.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    private String mAuthToken;

    public AuthenticationInterceptor(String token) {
        this.mAuthToken = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .header("Authorization", "token " + mAuthToken);

        Request request = builder.build();
        return chain.proceed(request);
    }
}