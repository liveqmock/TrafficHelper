package com.androidjp.traffichelper.login;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.androidjp.lib_common_util.pojo.network.Result;
import com.androidjp.traffichelper.R;
import com.androidjp.traffichelper.THApplication;
import com.androidjp.traffichelper.data.Constants;
import com.androidjp.traffichelper.data.ServiceAPI;
import com.androidjp.traffichelper.data.model.SharedPrefManager;
import com.androidjp.traffichelper.data.model.UserManager;
import com.androidjp.traffichelper.data.model.retrofit.ServiceGenerator;
import com.androidjp.traffichelper.data.pojo.User;
import com.orhanobut.logger.Logger;

import java.lang.ref.SoftReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Login界面 Presenter实现类
 * Created by androidjp on 2016/12/27.
 */

public class LoginPresenter implements LoginContract.Presenter {
    SoftReference<LoginContract.View> mView;
    Context mContext;
    private boolean isCurrentLoginPage = true;//当前是登录界面么？初始是 true

    /**
     * 相关数据
     */
    private User mUser;


    public LoginPresenter( Context mContext,LoginContract.View view) {
        this.mView = new SoftReference<LoginContract.View>(view);
        this.mContext = mContext;
    }


    @Override
    public void login(String userId, String password) {

        //检查合法性
        if (TextUtils.isEmpty(userId)||TextUtils.isEmpty(password)){
            this.mView.get().showErrorMsg(THApplication.getContext().getString(R.string.null_username_pwd));
            return;
        }

        this.mView.get().showProgress("正在登录。。");

        ///使用Retrofit2进行登录请求
//        Retrofit loginRetro = new Retrofit.Builder()
//                .baseUrl((ServiceAPI.IS_DEBUG?ServiceAPI.SERVER_HOST:ServiceAPI.REMOTE_SERVER_HOST))
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        ServiceAPI.LoginAPI loginAPI = loginRetro.create(ServiceAPI.LoginAPI.class);
//        Call<Result<User>> call = loginAPI.login(userId,password);
        ServiceAPI.LoginAPI loginAPI = ServiceGenerator.createService(ServiceAPI.LoginAPI.class);
        Call<Result<User>> call = loginAPI.login(userId, password);
        Logger.i("获取Call对象， 开始登录请求");
        call.enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                Result<User> res = response.body();
                boolean ok = false;
                Log.e("res是否为null？","NULL？："+(res==null));
                Logger.d("onResponse() 内部：res = " + (res==null?null:res.toString()));
                if (res != null) {
                    User user = res.data;
                    if (user!=null){
                        ///TODO: 存储进SPF文件中，并保存UserManager
                        UserManager.getInstance(THApplication.getContext()).refreshUser(user);
                        if (mView != null)
                            mView.get().hideProgress(Constants.FINISH_LOGIN, "登录完成");
                        ok = true;
                    }
                }
                if (!ok && mView != null)
                    mView.get().hideProgress(Constants.FAIL_LOGIN, "登录失败");
            }

            @Override
            public void onFailure(Call<Result<User>> call, Throwable t) {
                Logger.e(t.toString());
                if (mView != null)
                    mView.get().hideProgress(Constants.FAIL_LOGIN, "登录异常");
//                if (mView!=null)
//                    mView.get().hideProgress(Constants.FINISH_LOGIN,"登录成功");
            }
        });

    }


    @Override
    public void register(String userName, String password, String email, String phone, int sex , String age) {

        if (this.isCurrentLoginPage){
            this.mView.get().showRegisterPage();
            this.isCurrentLoginPage = false;
        }else{
            if (TextUtils.isEmpty(age)){
                //显示加载中提示框
                mView.get().showErrorMsg("年龄未填！！");
                return;
            }
            this.mView.get().showProgress("正在注册。。");
            User user = new User();
            user.setEmail(email);
            user.setPhone(phone);
            user.setSex(sex);
            user.setUser_name(userName);
            user.setUser_pwd(password);
            user.setAge(Integer.valueOf(age));
            //TODO：访问服务器，注册请求，并让界面显示进度条
            ///使用Retrofit2进行登录请求
            ServiceAPI.LoginAPI loginAPI = ServiceGenerator.createService(ServiceAPI.LoginAPI.class);
            Call<Result<String>> call = loginAPI.register(user.getFieldMap());

            call.enqueue(new Callback<Result<String>>() {

                @Override
                public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                    Logger.d("注册 onResponse()");
                    Result<String> res = response.body();
                    boolean ok = false;
                    if (res != null) {
                        if (res.code==200 && res.msg.equals("success")){
                            if (mView != null)
                                mView.get().hideProgress(Constants.FINISH_REGISTER, "注册完成");
                            ok = true;
                        }
                    }
                    if (!ok && mView != null)
                        mView.get().hideProgress(Constants.FAIL_REGUSTER, "注册失败");
                }

                @Override
                public void onFailure(Call<Result<String>> call, Throwable t) {
//                    Logger.e("注册 onFailure(),"+ t.getMessage());
//                    if (mView != null)
//                        mView.get().hideProgress(Constants.FAIL_REGUSTER, "注册异常");
                    if (mView!=null)
                        mView.get().hideProgress(Constants.FINISH_REGISTER,"注册成功");
                }
            });
        }
    }

    @Override
    public void findPwd(String userId) {

    }

    @Override
    public void back() {
        if (this.isCurrentLoginPage){
            ((Activity)this.mView.get()).finish();
        }else{
            this.mView.get().showLoginPage();
            this.isCurrentLoginPage = true;
        }
    }

    @Override
    public void start() {
        ////TODO：找到spf，自动登录
        if (this.mUser==null){
            this.mUser = SharedPrefManager.getInstance().getUserMsg(mContext);
            if (mView!=null);
        }
        if (this.mUser.getUser_id()!=null&&this.mUser.getUser_pwd()!=null){
            login(this.mUser.getUser_id() ,this.mUser.getUser_pwd());
        }
    }
}
