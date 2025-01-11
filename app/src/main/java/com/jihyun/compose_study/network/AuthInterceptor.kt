package com.jihyun.compose_study.network

import okhttp3.Interceptor
import okhttp3.Response
import com.jihyun.compose_study.BuildConfig


class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "KakaoAK ${BuildConfig.KAKAO_API_KEY}")
            .build()
        return chain.proceed(request)
    }
}
