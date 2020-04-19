package net.mccode.surveyhelper;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import net.mccode.surveyhelper.data.Angle;

public class TraverseVerticalFragment extends BaseCalculateFragment {
    private double mDeviationStandard;

    private EditText mAngleLeftDu;
    private EditText mAngleLeftFen;
    private EditText mAngleLeftMiao;
    private EditText mAngleRightDu;
    private EditText mAngleRightFen;
    private EditText mAngleRightMiao;
    private TextView mAngelResultLeft;
    private TextView mAngelResultRight;
    private TextView mResultInfo;
    private TextView mResultDifferent;
    private TextView mResultArg;
    private Button mCalcButton;
    private Button mResetButton;
    private ToggleButton mDirectionToggle;

    static Fragment newInstance() {
        TraverseVerticalFragment fragment = new TraverseVerticalFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_traverse_vertical, container, false);
        init(v);
        initButtonListener();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDeviationStandard = getStandard("traverse_vertical_standard", 60) / 3600;
    }

    @Override
    protected void initGetView(View v) {
        mAngleLeftDu = v.findViewById(R.id.angle_left_du);
        mAngleLeftFen = v.findViewById(R.id.angle_left_fen);
        mAngleLeftMiao = v.findViewById(R.id.angle_left_miao);
        mAngleRightDu = v.findViewById(R.id.angle_right_du);
        mAngleRightFen = v.findViewById(R.id.angle_right_fen);
        mAngleRightMiao = v.findViewById(R.id.angle_right_miao);
        mCalcButton = v.findViewById(R.id.btn_calc);
        mResetButton = v.findViewById(R.id.btn_reset);
        mAngelResultLeft = v.findViewById(R.id.angle_result_left);
        mAngelResultRight = v.findViewById(R.id.angle_result_right);
        mResultInfo = v.findViewById(R.id.result_info);
        mResultDifferent = v.findViewById(R.id.result_different);
        mResultArg = v.findViewById(R.id.result_arg);
        mDirectionToggle = v.findViewById(R.id.traverse_switch);
    }

    @Override
    protected void initEditTextList() {
        mEditTextList.add(mAngleLeftDu);
        mEditTextList.add(mAngleLeftFen);
        mEditTextList.add(mAngleLeftMiao);
        mEditTextList.add(mAngleRightDu);
        mEditTextList.add(mAngleRightFen);
        mEditTextList.add(mAngleRightMiao);
    }

    private void initButtonListener() {
        // 计算按钮
        mCalcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
        // 清空按钮
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    /**
     * 计算水平角观测值
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void calculate() {
        // 获取读数，计算半测回角值
        Angle angleLeft = new Angle(mAngleLeftDu.getText().toString(),
                mAngleLeftFen.getText().toString(),
                mAngleLeftMiao.getText().toString());
        // 判断顺时针逆时针度盘
        Angle resultLeft = mDirectionToggle.isChecked() ?
                new Angle(90).subtract(angleLeft)
                : angleLeft.copy().subtract(new Angle(90));
        mAngelResultLeft.setText(resultLeft.toString());

        Angle angleRight = new Angle(mAngleRightDu.getText().toString(),
                mAngleRightFen.getText().toString(),
                mAngleRightMiao.getText().toString());
        Angle resultRight = mDirectionToggle.isChecked() ?
                angleRight.copy().subtract(new Angle(270))
                : new Angle(270).subtract(angleRight);
        mAngelResultRight.setText(resultRight.toString());

        // 误差分析，计算一测回平均角值
        Angle different = resultRight.copy().subtract(resultLeft).divide(2);
        mResultDifferent.setText(getString(R.string.survey_vertical_different)
                + " "
                + different.toString());
        if (Math.abs(different.getRealDu()) < mDeviationStandard) {
            mResultInfo.setText(getString(R.string.text_success));
            mResultInfo.setTextColor(getResources().getColor(R.color.success));
            mResultArg.setText(getString(R.string.survey_difference_arg)
                    + " "
                    + resultLeft.add(resultRight).divide(2).toString());
        } else {
            mResultInfo.setText(getString(R.string.text_fail));
            mResultInfo.setTextColor(getResources().getColor(R.color.fail));
            mResultArg.setText("");
        }
    }
}
