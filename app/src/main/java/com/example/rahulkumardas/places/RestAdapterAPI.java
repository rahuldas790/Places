package com.example.rahulkumardas.places;

import android.support.v7.widget.CardView;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Rahul Kumar Das on 18-06-2017.
 * All network api collection
 */

public interface RestAdapterAPI {
    String END_POINT = "https://maps.googleapis.com/";
    String PLACE_PHOTO_URL = "maps/api/place/photo?maxwidth=40&maxheight=40";
    String PLACE_PHOTO_URL_LARGE = "maps/api/place/photo?maxwidth=400&maxheight=400";

    @GET("/maps/api/place/autocomplete/json")
    Call<JsonObject> getSuggestion(@Query("key") String key, @Query("components") String component,
                                   @Query("input") String input, @Query("types") String type);

    @GET("/maps/api/geocode/json")
    Call<JsonObject> getLocationForLatLng(@Query("latlng") String latLng,
                                          @Query("sensor") boolean sensor);

    @GET("/maps/api/place/textsearch/json")
    Call<JsonObject> getPlaces(@Query("location") String latLong, @Query("radius") int radius,
                               @Query("type") String type, @Query("key") String key,
                               @Query("pagetoken") String nextPageToken);

    @GET("/maps/api/place/textsearch/json?type=bar,bakery,meal_delivery,meal_takeaway," +
            "cafe,museum,night_club,restaurant,spa&inputtype=textquery")
    Call<JsonObject> searchPlaces(@Query("location") String latLong, @Query("radius") int radius,
                                  @Query("input") String input, @Query("key") String key,
                                  @Query("pagetoken") String nextPageToken);

    @GET("/maps/api/place/details/json?fields=geometry,photos")
    Call<JsonObject> getPlaceDetails(@Query("placeid") String placeId, @Query("key") String key);
}
