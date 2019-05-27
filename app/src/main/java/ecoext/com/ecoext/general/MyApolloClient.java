package ecoext.com.ecoext.general;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;

/**
 * Class that contain the Apollo Client with the API credential to
 * get the Info From
 *
 * Set the BASE_URL whit the url where your API is hosted
 */
public class MyApolloClient {

    private static final String BASE_URL = "http://192.168.0.143:8888/api";
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
