/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.instant.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Client for the PSPDFKit web preview server. In your own app, you would connect to your
 * own server backend to get Instant document identifiers and authentication tokens.
 */
public class WebPreviewClient {

    @NonNull private final WebPreviewService apiService;
    @NonNull private final BasicAuthInterceptor basicAuthInterceptor;

    public WebPreviewClient() {
        this("https://web-preview.pspdfkit.com/api/");
    }

    private WebPreviewClient(@NonNull String serverUrl) {
        basicAuthInterceptor = new BasicAuthInterceptor();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(basicAuthInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        apiService = retrofit.create(WebPreviewService.class);
    }

    /**
     * Retrieves document descriptor for an existing group.
     */
    @NonNull
    public Single<InstantExampleDocumentDescriptor> getDocument(@NonNull String url) {
        return apiService.getDocument(url).subscribeOn(Schedulers.io());
    }

    /**
     * Creates a new group on web example server. Returns document descriptor.
     */
    @NonNull
    public Single<InstantExampleDocumentDescriptor> createNewDocument() {
        return apiService.createDocument().subscribeOn(Schedulers.io());
    }

    /** Sets the basic auth credentials to use when doing API requests. */
    public void setBasicAuthCredentials(@NonNull String username, @NonNull String password) {
        basicAuthInterceptor.setCredentials(username, password);
    }

    /**
     * Interface for the web preview retrofit service.
     */
    private interface WebPreviewService {

        String ACCEPT_HEADER = "Accept: application/vnd.instant-example+json";

        @Headers(ACCEPT_HEADER)
        @GET
        Single<InstantExampleDocumentDescriptor> getDocument(@Url String url);

        @POST("instant-landing-page")
        Single<InstantExampleDocumentDescriptor> createDocument();
    }

    private static class BasicAuthInterceptor implements Interceptor {
        @Nullable private String credentials = null;

        public void setCredentials(@NonNull String user, @NonNull String password) {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (credentials != null) {
                Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build();
                return chain.proceed(authenticatedRequest);
            } else {
                return chain.proceed(request);
            }
        }
    }
}
