package com.github.sewerina.ttrentateam;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.sewerina.ttrentateam.db.UserDao;
import com.github.sewerina.ttrentateam.db.UserEntity;
import com.github.sewerina.ttrentateam.web.Response;
import com.github.sewerina.ttrentateam.web.UserApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainViewModel extends AndroidViewModel {
    private final MutableLiveData<List<UserEntity>> mUsers = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsRefresh = new MutableLiveData<>();
    private final CompositeDisposable mDisposables = new CompositeDisposable();
    private SingleLiveEvent<String> mMessage = new SingleLiveEvent<>();
    private UserDao mUserDao;
    private UserApi mUserApi;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mIsRefresh.postValue(false);
        mMessage.postValue("");
        mUserDao = TaskApp.sUserDao;

        Gson gson = new GsonBuilder().create();
        RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(rxAdapter)
                .build();
        mUserApi = retrofit.create(UserApi.class);
    }

    public LiveData<List<UserEntity>> getUsers() {
        return mUsers;
    }

    public LiveData<Boolean> isRefresh() {
        return mIsRefresh;
    }

    public LiveData<String> getMessage() {
        return mMessage;
    }

    public void loadFromWeb() {
        Disposable subscribe = mUserApi.response()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mIsRefresh.postValue(true);
                    }
                })
                .onErrorReturn(new Function<Throwable, Response>() {
                    @Override
                    public Response apply(Throwable throwable) throws Exception {
                        mMessage.postValue(getApplication().getResources().getString(R.string.failed_load));
                        return new Response();
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mIsRefresh.postValue(false);
                    }
                })
                .flatMap(new Function<Response, SingleSource<List<UserEntity>>>() {
                    @Override
                    public SingleSource<List<UserEntity>> apply(Response response) throws Exception {
                        if (response.data == null) {
                            return Single.just((List<UserEntity>) new ArrayList<UserEntity>());
                        }
                        List<UserEntity> users = response.data;
                        return Single.just(users);
                    }
                })
                .doOnSuccess(new Consumer<List<UserEntity>>() {
                    @Override
                    public void accept(List<UserEntity> userEntities) throws Exception {
                        mUsers.postValue(userEntities);
                    }
                })
                .subscribe(new Consumer<List<UserEntity>>() {
                    @Override
                    public void accept(List<UserEntity> userEntities) throws Exception {
                        Disposable sub = mUserDao.clearTableUser()
                                .subscribeOn(Schedulers.io())
                                .andThen(mUserDao.insertAllUsers(userEntities))
                                .subscribe();
                        mDisposables.add(sub);
                    }
                });
        mDisposables.add(subscribe);
    }

    public void loadFromDb() {
        Disposable subscribe = mUserDao.getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mIsRefresh.postValue(true);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mIsRefresh.postValue(false);
                    }
                })
                .subscribe(new Consumer<List<UserEntity>>() {
                    @Override
                    public void accept(List<UserEntity> userEntities) throws Exception {
                        mUsers.postValue(userEntities);
                    }
                });
        mDisposables.add(subscribe);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposables.dispose();
    }

}
