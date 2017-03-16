package com.androidjp.traffichelper.login;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidjp.traffichelper.R;
import com.androidjp.traffichelper.THApplication;
import com.androidjp.traffichelper.data.Constants;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 登录、注册界面
 * Created by androidjp on 2016/10/31.
 */

public class LoginActivity extends SwipeBackActivity implements LoginContract.View {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.id_login_card)
    CardView idLoginCard;
    @Bind(R.id.layout_et_username_login)
    TextInputLayout layoutEtUsernameLogin;
    @Bind(R.id.layout_et_password_login)
    TextInputLayout layoutEtPasswordLogin;
    @Bind(R.id.layout_et_password_re)
    TextInputLayout layoutEtPasswordRe;
    @Bind(R.id.layout_et_email)
    TextInputLayout layoutEtEmail;
    @Bind(R.id.layout_et_phone)
    TextInputLayout layoutEtPhone;
    @Bind(R.id.tv_sex)
    TextView tvSex;
    @Bind(R.id.rg_sex)
    RadioGroup rgSex;
    @Bind(R.id.rbtn_male)
    RadioButton rbtnMale;
    @Bind(R.id.rbtn_female)
    RadioButton rbtnFemale;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.btn_register)
    Button btnResgister;

    private LoginContract.Presenter mPresenter;
    private SweetAlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        ButterKnife.bind(this);
        initView();
        setPresenter(new LoginPresenter(THApplication.getContext(), this));
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                showLoginPage();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mPresenter = null;
    }

    private void initView() {
        this.mToolbar.setTitle(THApplication.getContext().getString(R.string.title_activity_login));
        this.mToolbar.setNavigationIcon(R.drawable.back);
        this.mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mPresenter.start();
    }

    @OnClick(R.id.btn_login)
    void onBtnLoginClick() {
        Logger.i("LoginActivity, onBtnLoginClick()");
        this.mPresenter.login(layoutEtUsernameLogin.getEditText().getText().toString()
                , layoutEtPasswordLogin.getEditText().getText().toString());
    }


    @OnClick(R.id.btn_register)
    void onBtnRegisterClick() {
        Logger.i("LoginActivity, onBtnRegisterClick()");
        this.mPresenter.register(layoutEtUsernameLogin.getEditText().getText().toString()
                , layoutEtPasswordLogin.getEditText().getText().toString()
                , layoutEtEmail.getEditText().getText().toString()
                , layoutEtPhone.getEditText().getText().toString()
                , (rbtnMale.isChecked() ? 0 : 1));
    }


    @Override
    public void showPwd(String password) {
        //TODO: 找回登录密码
    }

    @Override
    public void showLoginPage() {
        mToolbar.setTitle(getResources().getString(R.string.login));
        layoutEtPhone.setVisibility(View.GONE);
        layoutEtPasswordRe.setVisibility(View.GONE);
        layoutEtEmail.setVisibility(View.GONE);
        tvSex.setVisibility(View.GONE);
        rgSex.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
        btnResgister.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRegisterPage() {
        mToolbar.setTitle(getResources().getString(R.string.register));
        layoutEtPhone.setVisibility(View.VISIBLE);
        layoutEtPasswordRe.setVisibility(View.VISIBLE);
        layoutEtEmail.setVisibility(View.VISIBLE);
        tvSex.setVisibility(View.VISIBLE);
        rgSex.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
    }

    @Override
    public void showErrorMsg(String msg) {
        ///显示错误信息
        SweetAlertDialog dialog = new SweetAlertDialog(this);
        dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
        dialog.setTitleText(msg).show();

    }

    @Override
    public void showProgress(String msg) {
        //显示加载中提示框
        if (mDialog == null)
            mDialog = new SweetAlertDialog(this);
        mDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        mDialog.setTitleText(msg).show();
    }

    @Override
    public void hideProgress(boolean isOk, String msg) {
        if (mDialog != null)
            if (isOk) {
                mDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                mDialog.setTitleText(msg).dismissWithAnimation();
                setResult(Constants.FINISH_LOGIN);
                LoginActivity.this.finish();
            } else {
                mDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                mDialog.setTitleText(msg);
                mDialog.setCancelable(true);
                mDialog.setConfirmText("重试").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if (mDialog!=null)
                        mDialog.dismissWithAnimation();
                    }
                });
            }
        mDialog = null;
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.mPresenter = presenter;
        this.mPresenter.start();
    }

    @Override
    public void onBackPressed() {
        this.mPresenter.back();
    }
}
