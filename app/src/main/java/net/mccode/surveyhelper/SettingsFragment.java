package net.mccode.surveyhelper;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import net.mccode.surveyhelper.utils.CommonUtils;

public class SettingsFragment extends PreferenceFragment {
    private static final String TAG = "SettingsTAG";
    private ProgressDialog progressDialog;
    private Handler updateHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setHasOptionsMenu(true);
        updateHandler = new UpdateHandler();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         @NonNull Preference preference) {
        switch(preference.getKey()) {
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

    /**
     * 对比版本号，提示是否更新
     * @param newVersionCode 最新版本号
     * @param newVersionName 最新版本名
     */
    private void update(int newVersionCode, String newVersionName) {
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        int versionCode = CommonUtils.getVersionCode(getActivity());
        if(versionCode < newVersionCode) {
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

    private class UpdateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            update(msg.getData().getInt("code"),
                    msg.getData().getString("name")
            );
        }
    };

    private class CheckUpdate implements Runnable {
        private static final String URL = "http://mccode.net/api/surveyVersion.php";
        private Handler handler;
        public CheckUpdate(Handler handler) {
            this.handler = handler;
        }
        public void run() {
            HttpGet get = new HttpGet(URL);
            HttpClient httpClient = new DefaultHttpClient();

            try {
                HttpResponse response = httpClient.execute(get);
                if(response.getStatusLine().getStatusCode() != 200) {
                    Log.w(TAG, "版本号获取响应错误");
                    return;
                }
                JSONObject json = new JSONObject(EntityUtils.toString(
                        response.getEntity(), "utf-8"));
                int code = json.getInt("code");
                if(code == 0) {
                    // 获取json中版本发送消息给主线程
                    Bundle bundle = new Bundle();
                    bundle.putInt("code", json.getInt("surveyVersionCode"));
                    bundle.putString("name", json.getString("surveyVersionName"));
                    Message msg = new Message();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } else {
                    Log.w(TAG, "服务器端版本查询错误"
                            + Integer.toString(code)
                            + json.getString("msg"));
                }
            } catch (IOException e) {
                Log.w(TAG, "版本号获取连接失败");
            } catch (JSONException e) {
                Log.w(TAG, "JSON解析失败");
            }
        }
    }
}
