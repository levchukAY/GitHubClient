package com.artioml.githubclient.api;

import com.artioml.githubclient.EditUserActivity;
import com.artioml.githubclient.entities.AccessToken;
import com.artioml.githubclient.entities.AuthorizedUser;
import com.artioml.githubclient.entities.Repository;
import com.artioml.githubclient.entities.UpdateData;
import com.artioml.githubclient.entities.User;
import com.artioml.githubclient.entities.UserItem;
import com.artioml.githubclient.entities.Users;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GitHubClient {

    @GET("user")
    Call<AuthorizedUser> getAuthorizedUser(@Query("access_token") String accessToken);

    //Authorization: token OAUTH-TOKEN
    //@FormUrlEncoded
    @Headers({"Authorization: token 966e5e61b22c5e3556fd5cd01d29110e31f02555"})
    @GET("user")
    Call<AuthorizedUser> getAuthorizedUser();

    //@Headers({"Authorization: token 966e5e61b22c5e3556fd5cd01d29110e31f02555"})
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
            @Body UpdateData updateData);

    @PATCH("user")
    Call<AuthorizedUser> updateUser(
            @Query("access_token") String accessToken,
            @Body Map<String, String> updateData);

    @PATCH("user")
    Call<AuthorizedUser> updateUser(
            @Query("access_token") String accessToken,
            @Body JSONObject updateData);

    @PATCH("user")
    Call<AuthorizedUser> updateUser(
            @Query("access_token") String accessToken,
            @Body String updateData);

    @PUT("user/following/{username}")
    Call<Void> followUser(
            @Path("username") String username,
            @Query("access_token") String accessToken);

    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("/login/oauth/access_token")
    Call<AccessToken> getAccessToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri);
}
