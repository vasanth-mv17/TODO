package com.crud.todoapplication.api;

import androidx.annotation.NonNull;

import com.crud.todoapplication.model.Project;
import com.crud.todoapplication.model.TodoItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TodoItemService {

    private final TodoApiService apiService;

    public TodoItemService(final String baseUrl, final String accessToken) {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new TodoInterceptor(accessToken));
        final OkHttpClient client = httpClient.build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(TodoApiService.class);
    }

    public void create(final String todoItem, final String projectId,
                       final AuthenticationService.ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.createItem(todoItem, projectId);

        executeRequest(call, callBack);
    }

    public void getAllItem(final AuthenticationService.ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.getAllItem();

        executeRequest(call, callBack);
    }

    public void delete(final String itemId, final AuthenticationService.ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.deleteItem(itemId);

        executeRequest(call, callBack);
    }

    public void update(final TodoItem todoItem, final AuthenticationService.ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.updateOrder(todoItem.getId(), Math.toIntExact(todoItem.getOrder()), todoItem.getParentId());

        executeRequest(call, callBack);
    }

    private void executeRequest(final Call<ResponseBody> call,
                                final AuthenticationService.ApiResponseCallBack callBack) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    try {
                        callBack.onSuccess(response.body().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        assert response.errorBody() != null;
                        final String errorBody = response.errorBody().string();
                        final JSONObject jsonObject = new JSONObject(errorBody);
                        final String message = jsonObject.getString("message");

                        callBack.onFailure(message);
                    } catch (IOException | JSONException message) {
                        throw new RuntimeException(message);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callBack.onFailure(t.getMessage());
            }
        });
    }
}
