package xyz.codeme.surveyhelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseCalculateFragment extends Fragment {
    protected SharedPreferences mPreferences;
    protected List<EditText> mEditTextList;

    abstract protected void calculate();
    abstract protected void initGetView(View view);
    abstract protected void initEditTextList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    protected void init(View view) {
        initGetView(view);
        mEditTextList = new ArrayList<>();
        initEditTextList();
        initEditorActionListener();
    }

    /**
     * 编辑框回车切换下一个
     */
    private void initEditorActionListener() {
        int size = mEditTextList.size();
        for(int i=0; i<size; i++) {
            mEditTextList.get(i).setOnEditorActionListener(new EditorNextActionListener(i));
        }
    }

    /**
     * 情况输入框
     */
    protected void reset() {
        int listSize = mEditTextList.size();
        for(int i=0; i<listSize; i++) {
            mEditTextList.get(i).setText("");
        }
    }

    /**
     * 获取保存的标准值
     * @param key           preference key
     * @param defaultValue  默认值
     * @return  标准值
     */
    protected double getStandard(String key, double defaultValue) {
        return Double.parseDouble(
                mPreferences.getString(key, Double.toString(defaultValue)));
    }

    public class EditorNextActionListener implements TextView.OnEditorActionListener {
        private int index;

        public EditorNextActionListener(int i) {
            this.index = i;
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            // 点击下一项到下一项输入
            if(actionId == EditorInfo.IME_ACTION_NEXT) {
                if(index + 1 < mEditTextList.size()) {
                    mEditTextList.get(index + 1).requestFocus();
                }
                return true;
            }
            // 最后一项回车自动计算并隐藏键盘
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                calculate();
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null)
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0) ;
                return true;
            }
            return false;
        }
    }
}
