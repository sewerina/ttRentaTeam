package com.github.sewerina.ttrentateam.web;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface UserApi {

    @GET("api/users")
    Single<Response> response();

}
