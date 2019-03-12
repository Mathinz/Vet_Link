package com.vet.link.rest;


import com.vet.link.model.ReportModel;
import com.vet.link.pojo.SearchPojo;
import com.vet.link.pojo.ServerResponse;
import com.vet.link.pojo.SymptomsPojo;
import com.vet.link.pojo.UploadPojo;


import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @GET("androidAP_farmerSearch.php")
    Call<List<SearchPojo>> getSearchData();

    @GET("androidAP_sym_search.php")
    Call<List<SymptomsPojo>> getSymptomsData();

    @Multipart
    @POST("androidAP_uploadImage.php")
    Call<ServerResponse> uploadImage(@Part MultipartBody.Part file1, @Part("file1") RequestBody name1
            , @Part MultipartBody.Part file2, @Part("file2") RequestBody name2);

    @FormUrlEncoded
    @POST("androidAP_writeDB.php")
    Call<ResponseBody> uploadData(
            @Field("ReportID") String ReportID,
            @Field("Latitude") String Latitude,
            @Field("Longitude") String Longitude,
            @Field("Keywords") String Keywords,
            @Field("Description") String Description,
            @Field("Media1") String Media1,
            @Field("Media2") String Media2
    );
}
