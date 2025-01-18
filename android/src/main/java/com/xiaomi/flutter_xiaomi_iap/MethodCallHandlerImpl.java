package com.xiaomi.flutter_xiaomi_iap;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.xiaomi.gamecenter.appjoint.MiCode;
import com.xiaomi.gamecenter.appjoint.MiCommplatform;
import com.xiaomi.gamecenter.appjoint.OnAlertListener;
import com.xiaomi.gamecenter.appjoint.OnInitProcessListener;
import com.xiaomi.gamecenter.appjoint.entry.MiAppInfo;
import com.xiaomi.gamecenter.appjoint.OnLoginProcessListener;
import com.xiaomi.gamecenter.appjoint.entry.MiAccountInfo;
import com.xiaomi.gamecenter.appjoint.OnPayProcessListener;
import com.xiaomi.gamecenter.appjoint.entry.MiBuyInfo;

import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;

public class MethodCallHandlerImpl implements MethodCallHandler, OnAlertListener, OnLoginProcessListener, OnPayProcessListener {

    private static final String TAG = "MethodCallHandlerImpl";

    private final Gson mGson;

    private final Activity mActivity;

    private final PayListActivityMonitor monitor;

    private Result mLoginResult;

    private Result mPayResult;

    public MethodCallHandlerImpl(Activity activity, PayListActivityMonitor monitor) {
        this.mActivity = activity;
        this.monitor = monitor;
        mGson = new GsonBuilder().create();
    }

    @Override
    public void onMethodCall(@NonNull final MethodCall call, @NonNull final Result result) {
        switch (call.method) {
            case "init":
                init(call, result);
                break;
            case "miLogin":
                miLogin(call, result);
                break;
            case "getLoginInfo":
                getLoginInfo(call, result);
                break;
            case "createPurchase":
                createPurchase(call, result);
                break;
            case "setClickEnabled":
                setClickEnabled(call, result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void setClickEnabled(@NonNull final MethodCall call, @NonNull final Result result) {
        if (monitor != null) {
            final boolean enable = ValueGetter.getBoolean("enable", call);
            monitor.setClickEnabled(enable);
            result.success(true);
        }
    }

    /// 初始化IAP SDK
    private void init(@NonNull final MethodCall call, @NonNull final Result result) {
        final String appId = ValueGetter.getString("appId", call);
        final String appKey = ValueGetter.getString("appKey", call);

        MiAppInfo appInfo = new MiAppInfo();
        /**
         * AppId,AppKey在开发者站获取
         * 请确保AppId,AppKey,包名,应用签名和开发者站的信息一致
         */
        appInfo.setAppId(appId);
        appInfo.setAppKey(appKey);
        /**
         * 初始化
         */
        MiCommplatform.Init(mActivity, appInfo, new OnInitProcessListener() {
            /**
             * 初始化结果
             * @param returnCode 返回码
             * @param returnInfo 返回信息
             */
            @Override
            public void finishInitProcess(int returnCode, @Nullable String returnInfo) {
                switch (returnCode) {
                    case MiCode.MI_INIT_SUCCESS:
                        Log.d(TAG, "小米联运初始化成功");
                        result.success(true);
                        break;
                    default:
                        Log.e(TAG, "初始化失败(请检查是否已配计费):" + returnInfo);
                        result.error(String.valueOf(returnCode), "初始化失败(请检查是否已配计费):" + returnInfo, null);
                        break;
                }
            }
        });

        /**
         * 关闭额外错误信息的Toast提示
         */
        MiCommplatform.getInstance().setToastDisplay(false);
        MiCommplatform.getInstance().setAlertDialogDisplay(false);
        /**
         * 监听额外错误信息
         */
        MiCommplatform.getInstance().setOnAlertListener(this);
    }

    /// 获取小米账号/自有账号登录信息
    private void getLoginInfo(@NonNull final MethodCall call, @NonNull final Result result) {
        try {
            final MiAccountInfo miAccountInfo = MiCommplatform.getInstance().getLoginInfo();
            if (miAccountInfo != null) {
                result.success(mGson.toJson(miAccountInfo));
            } else {
                result.success(null);
            }
        } catch (Exception e) {
            Log.e(TAG, "获取小米账号/自有账号登录错误信息:" + e.getMessage());
            // result.error(String.valueOf(MiCode.MI_ERROR_ACTION_EXECUTED), e.getMessage(), null);
            result.success(null);
        }
    }

    /// 小米账号/自有账号登录
    private void miLogin(@NonNull final MethodCall call, @NonNull final Result result) {
        mLoginResult = result;
        final int loginType = ValueGetter.getInt("loginType", call);
        final String accountType = ValueGetter.getString("accountType", call);
        final String anonId = ValueGetter.getString("anonId", call);
        MiCommplatform.getInstance().miLogin(mActivity, this,
                loginType, accountType, anonId);
    }

    /// 创建支付订单
    private void createPurchase(@NonNull final MethodCall call, @NonNull final Result result) {
        mPayResult = result;
        final int productType = ValueGetter.getInt("productType", call);
        final int createType = ValueGetter.getInt("createType", call);
        final String cpOrderId = ValueGetter.getString("cpOrderId", call);
        final String paymentType = ValueGetter.getString("paymentType", call);
        final String productCode = ValueGetter.getString("productCode", call);
        final int quantity = ValueGetter.getInt("quantity", call);
        final int feeValue = ValueGetter.getInt("feeValue", call);

        Log.d(TAG, "创建支付订单："
                + "\nproductType:" + productType
                + "\ncreateType:" + createType
                + "\ncpOrderId:" + cpOrderId
                + "\npaymentType:" + paymentType
                + "\nproductCode:" + productCode
                + "\nquantity:" + quantity
                + "\nfeeValue:" + feeValue);

        MiBuyInfo miBuyInfo = new MiBuyInfo();
        miBuyInfo.setCpOrderId(cpOrderId);

        if (productType == 2) { // 订阅类型商品
            miBuyInfo.setProductCode(productCode);
            miBuyInfo.setQuantity(quantity);
            try {
                MiCommplatform.getInstance().miSubscribe(mActivity, miBuyInfo, this);
            } catch (Exception e) {
                result.error(String.valueOf(MiCode.MI_ERROR_PAY_FAILURE), e.getMessage(), null);
            }
        } else { // 消耗型、非消耗型商品
            if (createType == 0) { // 按金额计费
                miBuyInfo.setFeeValue(feeValue);
            } else { // 按计费代码计费
                miBuyInfo.setProductCode(productCode);
                miBuyInfo.setQuantity(quantity);
            }
            if (TextUtils.isEmpty(paymentType)) {
                try {
                    MiCommplatform.getInstance().miUniPay(mActivity, miBuyInfo, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    MiCommplatform.getInstance().miUniPay(mActivity, miBuyInfo, this, paymentType, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 监听额外错误信息
     * 此接口为辅助接口可不接
     *
     * @param returnCode         返回代码
     * @param returnInfo         返回信息
     * @param returnDetailedInfo 返回详细信息
     */
    @Override
    public void onAlert(int returnCode, @Nullable String returnInfo, @Nullable String returnDetailedInfo) {
        final String msg = returnInfo + (TextUtils.isEmpty(returnDetailedInfo) ? "" : "|" + returnDetailedInfo);
        Log.e(TAG, "返回代码:" + returnCode + "\n" + "返回信息:" + msg);
    }

    /**
     * 登录结果
     *
     * @param returnCode    返回码
     * @param miAccountInfo 账号信息
     */
    @Override
    public void finishLoginProcess(int returnCode, @Nullable MiAccountInfo miAccountInfo) {
        switch (returnCode) {
            /**
             * 登录成功
             */
            case MiCode.MI_LOGIN_SUCCESS:
                Log.d(TAG, "登录成功" + mGson.toJson(miAccountInfo));
                mLoginResult.success(mGson.toJson(miAccountInfo));
                break;
            /**
             * 有未完成的登录/支付流程
             */
            case MiCode.MI_ERROR_ACTION_EXECUTED:
                mLoginResult.error(String.valueOf(MiCode.MI_ERROR_ACTION_EXECUTED), "登录太频繁,请休息一下", null);
                break;
            /**
             * 登录参数错误
             */
            case MiCode.MI_ERROR_PAY_INVALID_PARAMETER:
                mLoginResult.error(String.valueOf(MiCode.MI_ERROR_PAY_INVALID_PARAMETER), "参数错误", null);
                break;
            /**
             * 登录错误
             */
            default:
                mLoginResult.error(String.valueOf(returnCode), "返回码:" + returnCode, null);
                break;
        }
    }

    /**
     * 支付结果
     *
     * @param returnCode 返回码
     * @param returnInfo 错误信息
     */
    @Override
    public void finishPayProcess(int returnCode, @Nullable String returnInfo) {
        //Log.e(TAG, "返回代码::::::" + returnCode + "\n" + "返回信息::::::" + returnInfo);
        switch (returnCode) {
            /**
             * 支付成功
             */
            case MiCode.MI_PAY_SUCCESS:
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("info", returnInfo);
                resultMap.put("msg", "支付成功");
                resultMap.put("code", String.valueOf(returnCode));
                mPayResult.success(mGson.toJson(resultMap));
                break;
            /**
             * 支付取消
             */
            case MiCode.MI_ERROR_PAY_CANCEL:
                mPayResult.error(String.valueOf(MiCode.MI_ERROR_PAY_CANCEL), "支付取消", null);
                break;
            /**
             * 订阅成功
             */
            case MiCode.MI_SUB_SUCCESS:
                Map<String, String> resultMap1 = new HashMap<>();
                resultMap1.put("info", returnInfo);
                resultMap1.put("msg", "订阅成功");
                resultMap1.put("code", String.valueOf(returnCode));
                mPayResult.success(mGson.toJson(resultMap1));
                break;
            /**
             * 订阅取消
             */
            case MiCode.MI_ERROR_SUB_CANCEL:
                mPayResult.error(String.valueOf(MiCode.MI_ERROR_SUB_CANCEL), "订阅取消", null);
                break;
            /**
             * 有未完成的登录/支付流程
             */
            case MiCode.MI_ERROR_ACTION_EXECUTED:
                mPayResult.error(String.valueOf(MiCode.MI_ERROR_ACTION_EXECUTED), "点太快了,请休息一下", null);
                break;
            /**
             * 支付错误
             */
            case MiCode.MI_ERROR_PAY_INVALID_PARAMETER:
                mPayResult.error(String.valueOf(MiCode.MI_ERROR_PAY_INVALID_PARAMETER), "参数错误", null);
                break;
            /**
             * 订阅取消
             */
            case MiCode.MI_ERROR_BIND_CANCEL:
                mPayResult.error(String.valueOf(MiCode.MI_ERROR_BIND_CANCEL), "订阅取消", null);
                break;
            default:
                mPayResult.error(String.valueOf(returnCode), returnInfo, null);
                break;
        }

    }
}