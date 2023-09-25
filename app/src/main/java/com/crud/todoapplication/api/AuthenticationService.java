package com.crud.todoapplication.api;

import androidx.annotation.NonNull;

import com.crud.todoapplication.model.SignUp;
import com.crud.todoapplication.model.User;

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

public class AuthenticationService {

    private final ApiService apiService;

    public AuthenticationService(final String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public AuthenticationService(final String baseUrl, final String token) {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new AuthenticationInterceptor(token));
        final OkHttpClient client = httpClient.build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public void login(final String email, final String password, final ApiResponseCallBack callBack) {
        Call<ResponseBody> call = apiService.loginUser(email, password);
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

    public void signUp(final SignUp sign, final ApiResponseCallBack callBack) {
        Call<ResponseBody> call = apiService.createUser(sign);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert  response.body() != null;

                    callBack.onSuccess(response.body().toString());
                } else {
                    callBack.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callBack.onFailure(t.getMessage());
            }
        });
    }

    public void resetPassword(final String email, final String password, final String oldHint, final String newHint, final ApiResponseCallBack callBack) {
        Call<ResponseBody> call = apiService.resetPassword(email, password, oldHint, newHint);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert  response.body() != null;

                    callBack.onSuccess(response.body().toString());
                } else {
                    callBack.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callBack.onFailure(t.getMessage());
            }
        });
    }

    public void getUserDetail(final ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.getUserDetail();

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

    public void UpdateUserDetail(final User user, final ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.updateUserSetting(user);

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

    public void getSettingDetail(final ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.getSystemSetting();

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

    public void updateSystemSetting(String selectedFontFamily, String selectedColor, int fontSize, ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.updateSystemSetting();

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

    public interface ApiResponseCallBack {
        void onSuccess(String response);
        void onFailure(String response);
    }
}
