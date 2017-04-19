package com.artioml.githubclient.api;

import com.artioml.githubclient.entities.AccessToken;
import com.artioml.githubclient.entities.AuthorizedUser;
import com.artioml.githubclient.entities.Repository;
import com.artioml.githubclient.entities.User;
import com.artioml.githubclient.entities.UserItem;
import com.artioml.githubclient.entities.Users;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GitHubClient {

    @GET("user")
    Call<AuthorizedUser> getAuthorizedUser(@Query("access_token") String accessToken);

    @GET("users/{user}")
    Call<User> getUser(@Path("user") String user);

    @GET("users")
    Call<List<UserItem>> getUsers();

    @GET("search/users")
    Call<Users> searchUsers(
            @Query("q") String q,
            @Query("page") int page);

    @GET("users/{user}/repos")
    Call<List<Repository>> reposForUser(
            @Path("user") String user,
            @Query("page") int page,
            @Query("access_token") String accessToken);

    @PATCH("user")
    Call<AuthorizedUser> updateUser(
            @Query("access_token") String accessToken,
            @Query("name") String name,
            @Query("email") String email,
            @Query("company") String company);

    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("/login/oauth/access_token")
    Call<AccessToken> getAccessToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri);
}
