package com.jihyun.compose_test.network

import com.jihyun.compose_test.model.ImageItem
import com.jihyun.compose_test.model.KakaoResponse
import com.jihyun.compose_test.model.VideoItem
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoApi {
    // 이미지 검색 요청
    @GET("/v2/search/image")
    suspend fun searchImages(
        @Header("Authorization") apiKey: String, // REST API 키
        @Query("query") query: String,           // 검색어
        @Query("size") size: Int = 30            // 검색 결과 개수 (기본값: 30개)
    ): KakaoResponse<ImageItem> // 제네릭을 사용하여 이미지 데이터 반환

    // 동영상 검색 추가
    @GET("/v2/search/vclip")
    suspend fun searchVideos(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("size") size: Int = 30
    ): KakaoResponse<VideoItem> // 제네릭을 사용하여 동영상 데이터 반환
}