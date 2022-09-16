package com.microport.rxhttp.rxjava;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.microport.rxhttp.rxjava.callback.CallBack;
import com.microport.rxhttp.rxjava.callback.RxStreamCallBackImp;
import com.microport.rxhttp.utils.ResultException;
import com.microport.rxhttp.utils.RxLoadingDialog;
import com.microport.rxhttp.utils.RxUtils;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.disposables.DisposableContainer;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * Created by bhm on 2022/9/15.
 */

public class RxBuilder {

    private Builder builder;
    private CallBack callBack;
    private RxStreamCallBackImp listener;
    private long currentRequestDateTamp = 0;

    public RxBuilder(@NonNull Builder builder){
        this.builder = builder;
    }

    public RxAppCompatActivity getActivity() {
        return builder.activity;
    }

    public boolean isShowDialog() {
        return builder.isShowDialog;
    }

    public boolean isCancelable() {
        return builder.cancelable;
    }

    public CallBack getCallBack() {
        return callBack;
    }

    public boolean isLogOutPut() {
        return builder.isLogOutPut;
    }

    public RxLoadingDialog getDialog() {
        return builder.dialog;
    }

    public boolean isDefaultToast() {
        return builder.isDefaultToast;
    }

    public RxManager getRxManager() {
        return builder.rxManager;
    }

    public int getReadTimeOut() {
        return builder.readTimeOut;
    }

    public int getConnectTimeOut() {
        return builder.connectTimeOut;
    }

    public OkHttpClient getOkHttpClient() {
        return builder.okHttpClient;
    }

    public RxStreamCallBackImp getListener() {
        return listener;
    }

    public String getFilePath(){
        return builder.filePath;
    }

    public String getFileName(){
        return builder.fileName;
    }

    public long writtenLength(){
        return builder.writtenLength;
    }

    public boolean isAppendWrite(){
        return builder.appendWrite;
    }

    public String getLoadingTitle(){
        return builder.loadingTitle;
    }

    public boolean isDialogDismissInterruptRequest(){
        return builder.dialogDismissInterruptRequest;
    }

    public HashMap<String, String> getDefaultHeader(){
        return builder.defaultHeader;
    }

    public long getDelaysProcessLimitTime(){
        return builder.delaysProcessLimitTime;
    }

    public <T> T createApi(Class<T> cla, String host){
        if(builder.isShowDialog && null != builder.dialog){
            builder.dialog.showLoading(this);
        }
        return new RetrofitCreateHelper(this)
                .createApi(cla, host);
    }

    /** 上传请求
     * @param cla
     * @param host 请求地址
     * @param listener
     * @return
     */
    public <T> T createApi(Class<T> cla, String host, RxStreamCallBackImp listener){
        if(null == listener){
            throw new NullPointerException("RxStreamCallBackImp(listener) can not be null!");
        }
        if(builder.isShowDialog && null != builder.dialog){
            builder.dialog.showLoading(this);
        }
        this.listener = listener;
        return new RetrofitCreateHelper(this)
                .createApi(cla, host);
    }

    public <T> Disposable setCallBack(Observable<T> observable, final CallBack<T> callBack){
        this.callBack = callBack;
        Disposable disposable = observable.compose(builder.activity.bindToLifecycle())//管理生命周期
                .compose(RxManager.rxSchedulerHelper())//发布事件io线程
                .subscribe(getBaseConsumer(),
                        getThrowableConsumer(),
                        getDefaultAction(),
                        getDisposableContainer());
        currentRequestDateTamp = System.currentTimeMillis();
        //做准备工作
        if(null != getCallBack()){
            getCallBack().onStart(disposable);
        }
        if(builder.rxManager != null){
            builder.rxManager.subscribe(disposable);
        }
        return disposable;
    }

    @SuppressLint("CheckResult")
    private <T> Consumer<T> getBaseConsumer(){
        return new Consumer<T>(){
            @Override
            public void accept(T t) throws Exception {
                if(System.currentTimeMillis() - currentRequestDateTamp <= getDelaysProcessLimitTime()){
                    Observable.timer(getDelaysProcessLimitTime(), TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    doBaseConsumer(t);
                                }
                            });
                }else {
                    doBaseConsumer(t);
                }
            }
        };
    }

    private <T> void doBaseConsumer(T t){
        if (null != getCallBack()) {
            getCallBack().onSuccess(t);
        }
        if (isShowDialog() && null != getDialog()) {
            getDialog().dismissLoading(getActivity());
        }
    }

    @SuppressLint("CheckResult")
    private Consumer<Throwable> getThrowableConsumer(){
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                if(null == e){
                    return;
                }
                RxUtils.logger(RxBuilder.this, "ThrowableConsumer-> ", e.getMessage());//抛异常
                if(System.currentTimeMillis() - currentRequestDateTamp <= getDelaysProcessLimitTime()){
                    Observable.timer(getDelaysProcessLimitTime(), TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    doThrowableConsumer(e);
                                }
                            });
                }else{
                    doThrowableConsumer(e);
                }
            }
        };
    }

    private void doThrowableConsumer(Throwable e){
        if(null != getCallBack()){
            getCallBack().onFail(e);
        }
        if(isShowDialog() && null != getDialog()){
            getDialog().dismissLoading(getActivity());
        }
        if(isDefaultToast()) {
            if (e instanceof HttpException) {
                if (((HttpException) e).code() == 404) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (((HttpException) e).code() == 504) {
                    Toast.makeText(getActivity(), "请检查网络连接！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "请检查网络连接！", Toast.LENGTH_SHORT).show();
                }
            } else if (e instanceof IndexOutOfBoundsException
                    || e instanceof NullPointerException
                    || e instanceof JsonSyntaxException
                    || e instanceof IllegalStateException
                    || e instanceof ResultException) {
                Toast.makeText(getActivity(), "数据异常，解析失败！", Toast.LENGTH_SHORT).show();
            } else if (e instanceof TimeoutException) {
                Toast.makeText(getActivity(), "连接超时，请重试！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "请求失败，请稍后再试！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** 最终结果的处理
     * @return 和getThrowableConsumer互斥
     */
    private Action getDefaultAction(){
        return new Action() {
            @Override
            public void run() throws Exception {
                if(null != getCallBack()){
                    getCallBack().onComplete();
                }
            }
        };
    }

    /** 做准备工作
     * @return
     */
    private DisposableContainer getDisposableContainer(){
        return new CompositeDisposable();
    }

    public Disposable beginDownLoad(@androidx.annotation.NonNull Observable<ResponseBody> observable){
        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(@NonNull ResponseBody responseBody) throws Exception {
                        return responseBody.byteStream();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(null != listener) {
                            listener.onFail(throwable.getMessage());
                            if(null != builder.dialog && builder.isShowDialog) {
                                builder.dialog.dismissLoading(builder.activity);
                            }
                        }
                        if(builder.rxManager != null) {
                            builder.rxManager.removeObserver();
                        }
                    }
                });
    }

    public static Builder newBuilder(RxAppCompatActivity activity) {
        return new Builder(activity);
    }

    public static final class Builder {

        private RxAppCompatActivity activity;
        private RxManager rxManager;
        private boolean isShowDialog = RxConfig.isShowDialog();
        private boolean cancelable = RxConfig.cancelable();
        private RxLoadingDialog dialog = RxConfig.getRxLoadingDialog();
        private boolean isDefaultToast = RxConfig.isDefaultToast();
        private int readTimeOut = RxConfig.getReadTimeOut();
        private int connectTimeOut = RxConfig.getConnectTimeOut();
        private OkHttpClient okHttpClient = RxConfig.getOkHttpClient();
        private boolean isLogOutPut = RxConfig.isLogOutPut();
        private String filePath = RxConfig.getFilePath();
        private String fileName = RxConfig.getFileName();
        private long writtenLength = RxConfig.writtenLength();
        private boolean appendWrite = RxConfig.isAppendWrite();
        private String loadingTitle = RxConfig.getLoadingTitle();
        private boolean dialogDismissInterruptRequest = RxConfig.isDialogDismissInterruptRequest();
        private HashMap<String, String> defaultHeader = RxConfig.getDefaultHeader();
        private long delaysProcessLimitTime = RxConfig.getDelaysProcessLimitTime();

        public Builder(RxAppCompatActivity activity) {
            this.activity = activity;
        }

        public Builder setLoadingDialog(RxLoadingDialog dialog){
            this.dialog = dialog;
            return this;
        }

        public Builder setDialogAttribute(boolean isShowDialog, boolean cancelable, boolean dialogDismissInterruptRequest){
            this.isShowDialog = isShowDialog;
            this.cancelable = cancelable;
            this.dialogDismissInterruptRequest = dialogDismissInterruptRequest;
            return this;
        }

        public Builder setIsDefaultToast(boolean isDefaultToast, RxManager rxManager){
            this.isDefaultToast = isDefaultToast;
            this.rxManager = rxManager;
            return this;
        }

        public Builder setRxManager(RxManager rxManager){
            this.rxManager = rxManager;
            return this;
        }

        public Builder setHttpTimeOut(int readTimeOut, int connectTimeOut){
            this.readTimeOut = readTimeOut;
            this.connectTimeOut = connectTimeOut;
            return this;
        }
        /** 不推荐使用，使用此方法，将取消默认的设置，包括但不限于日志，缓存，下载，上传，网络，SSL。
         * @param okHttpClient
         * @return
         */
        @Deprecated
        public Builder setOkHttpClient(OkHttpClient okHttpClient){
            this.okHttpClient = okHttpClient;
            return this;
        }

        public Builder setIsLogOutPut(boolean isLogOutPut){
            this.isLogOutPut = isLogOutPut;
            return this;
        }

        public Builder setLoadingTitle(String loadingTitle){
            this.loadingTitle = loadingTitle;
            return this;
        }

        public Builder setDefaultHeader(HashMap<String, String> defaultHeader){
            defaultHeader = defaultHeader;
            return this;
        }

        public Builder setDownLoadFileAtr(String mFilePath, String mFileName, boolean mAppendWrite, long mWrittenLength){
            filePath = mFilePath;
            fileName = mFileName;
            appendWrite = mAppendWrite;
            writtenLength = mWrittenLength;
            return this;
        }

        public Builder setDelaysProcessLimitTime(long delaysProcessLimitTime1){
            delaysProcessLimitTime = delaysProcessLimitTime1;
            return this;
        }

        public RxBuilder bindRx(){
            return new RxBuilder(this);
        }
    }
}
