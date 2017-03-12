package io.hefuyi.zhihudaily.mvp;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.robolectric.RuntimeEnvironment;

import java.util.concurrent.TimeUnit;

import io.hefuyi.zhihudaily.Constants;
import io.hefuyi.zhihudaily.interceptor.CacheInterceptor;
import io.hefuyi.zhihudaily.respository.RepositoryImpl;
import io.hefuyi.zhihudaily.respository.interfaces.Repository;
import io.hefuyi.zhihudaily.util.FileUtils;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by hefuyi on 16/9/5.
 */
public class TestUtils {

    private static Repository mRepository;

    public static Repository getRepository() {
        if (mRepository != null) {
            return mRepository;
        } else {
            String endpointUrl = "http://news.at.zhihu.com/api/4/";
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(new Cache(FileUtils.getHttpCacheDir(RuntimeEnvironment.application), Constants.Config.HTTP_CACHE_SIZE))
                    .connectTimeout(Constants.Config.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(Constants.Config.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .addInterceptor(new CacheInterceptor())
                    .build();
            OkHttpClient newClient = client.newBuilder().addInterceptor(loggingInterceptor).build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(endpointUrl)
                    .client(newClient)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            mRepository=new RepositoryImpl(retrofit);
            return mRepository;
        }
    }

    public static Context getContext() {
        return RuntimeEnvironment.application;
    }
}
