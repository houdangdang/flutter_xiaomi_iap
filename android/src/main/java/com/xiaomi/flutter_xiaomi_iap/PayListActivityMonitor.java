package com.xiaomi.flutter_xiaomi_iap;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.lang.reflect.Field;

public class PayListActivityMonitor implements Application.ActivityLifecycleCallbacks {

    // 控制是否启用模拟点击
    private boolean isClickEnabled = false; // 默认不启用

    @Override
    public void onActivityCreated(Activity activity, android.os.Bundle savedInstanceState) {
        // 不需要处理
    }

    @Override
    public void onActivityStarted(Activity activity) {
        // 不需要处理
    }

    @Override
    public void onActivityResumed(Activity activity) {
        // 检测当前 Activity 是否是 PayListActivity
        if (activity.getClass().getName().equals("com.xiaomi.gamecenter.appjoint.ui.PayListActivity")) {
            // 延迟一段时间，确保视图加载完成
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    // 通过反射获取 mio_tv_pay
                    Field mioTvPayField = activity.getClass().getDeclaredField("k"); // "k" 是 mio_tv_pay 的变量名
                    mioTvPayField.setAccessible(true);
                    TextView mioTvPay = (TextView) mioTvPayField.get(activity);

                    // 模拟点击
                    if (mioTvPay != null && isClickEnabled) {
                        mioTvPay.performClick();
                        System.out.println("mio_tv_pay clicked!");
                    } else {
                        System.out.println("mio_tv_pay not found or disable click!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 1000); // 延迟 500ms，视情况调整
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // 不需要处理
    }

    @Override
    public void onActivityStopped(Activity activity) {
        // 不需要处理
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, android.os.Bundle outState) {
        // 不需要处理
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        // 不需要处理
    }

    /**
     * 启用或禁用模拟点击
     *
     * @param enabled true 表示启用，false 表示禁用
     */
    public void setClickEnabled(boolean enabled) {
        this.isClickEnabled = enabled;
    }

    /**
     * 检查当前是否启用了模拟点击
     *
     * @return true 表示启用，false 表示禁用
     */
    public boolean isClickEnabled() {
        return isClickEnabled;
    }
}

