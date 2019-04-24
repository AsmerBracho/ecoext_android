package ecoext.com.ecoext;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;

public class MyApolloClient {

    private static final String BASE_URL = "http://192.168.43.32:8888/api";
    private static ApolloClient myApolloClient;

    public static ApolloClient getMyApolloClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        myApolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .build();

        return myApolloClient;

    }
}
