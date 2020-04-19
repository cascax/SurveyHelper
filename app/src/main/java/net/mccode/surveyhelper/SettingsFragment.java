package net.mccode.surveyhelper;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.Preference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import net.mccode.surveyhelper.utils.CommonUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "SettingsTAG";
    private ProgressDialog progressDialog;
    private Handler updateHandler;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        updateHandler = new SettingsFragment.UpdateHandler(this);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case "check_update":
                progressDialog = ProgressDialog.show(getActivity(),
                        "", getString(R.string.checking_update), true, true);
                new Thread(new CheckUpdate(updateHandler)).start();
                return true;
            case "about_me":
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(String.format(
                                getResources().getString(R.string.text_now_version),
                                CommonUtils.getVersionName(getActivity())
                        ))
                        .setMessage(R.string.text_about_me)
                        .setPositiveButton(R.string.btn_ok, null)
                        .show();
                return true;
            default:
                return false;
        }
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void getLatestVersionFailed() {
        dismissProgress();
        Toast.makeText(getActivity(), R.string.text_latest_version_err, Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * 对比版本号，提示是否更新
     *
     * @param newVersionCode 最新版本号
     * @param newVersionName 最新版本名
     */
    private void update(int newVersionCode, String newVersionName) {
        dismissProgress();
        int versionCode = CommonUtils.getVersionCode(getActivity());
        if (versionCode < newVersionCode) {
            String text = String.format(
                    getResources().getString(R.string.text_need_update),
                    newVersionName
            );
            DialogInterface.OnClickListener okListener; // 确定按钮点击监听器
            okListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(getResources().getString(R.string.update_url))
                    );
                    startActivity(i);
                }
            };
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.text_title_update)
                    .setMessage(text)
                    .setPositiveButton(R.string.btn_ok, okListener)
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show();
        } else {
            Toast.makeText(getActivity(), R.string.text_latest_version, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    static private class UpdateHandler extends Handler {
        private WeakReference<SettingsFragment> mFragment;

        UpdateHandler(SettingsFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            SettingsFragment fragment = mFragment.get();
            if (fragment == null) {
                return;
            }
            if (msg.getData().getBoolean("ok")) {
                fragment.update(
                        msg.getData().getInt("version"),
                        msg.getData().getString("name")
                );
            } else {
                fragment.getLatestVersionFailed();
            }
        }
    }

    private class CheckUpdate implements Runnable {
        private static final String URL = "https://app.mccode.net/version/SurveyHelper";
        private Handler handler;
        private OkHttpClient client;

        CheckUpdate(Handler handler) {
            this.handler = handler;
            client = new OkHttpClient();
        }

        public void run() {
            if (!getVersion()) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("ok", false);
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }

        private boolean getVersion() {
            Request request = new Request.Builder()
                    .url(URL)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.w(TAG, "版本号获取响应错误");
                    return false;
                }

                JSONObject json = new JSONObject(response.body().string());
                int code = json.getInt("code");
                if (code == 0) {
                    // 获取json中版本发送消息给主线程
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("ok", true);
                    bundle.putInt("version", json.getInt("version"));
                    bundle.putString("name", json.getString("versionName"));
                    Message msg = new Message();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    return true;
                } else {
                    Log.w(TAG, "服务器端版本查询错误"
                            + code
                            + json.getString("msg"));
                }
            } catch (IOException e) {
                Log.w(TAG, "版本号获取连接失败", e);
            } catch (JSONException e) {
                Log.w(TAG, "JSON解析失败", e);
            } catch (NullPointerException e) {
                Log.w(TAG, "版本号接口返回错误", e);
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return false;
        }
    }
}
