package xyz.codeme.surveyhelper;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;


public class LevelingFragment extends BaseCalculateFragment {
    private static final int GENERAL_LEVELING = 2;
    private static final int FOUR_LEVELING = 1;
    private int mLevelingType = GENERAL_LEVELING;
    private String[] paramK;
    private double mDeltaHStandard;

    private ToggleButton mLevelingSwitch;
    private LinearLayout mLayoutFour;
    private Button mCalcButton;
    private Button mResetButton;

    private EditText mEditBackDown; // 后尺下丝
    private EditText mEditBackUp;
    private EditText mEditFrontDown;
    private EditText mEditFrontUp;
    private TextView mDistanceBack; // 后距
    private TextView mDistanceFront;
    private TextView mDistanceDifferent; // 视距差d
    private TextView mDistanceInfo; // 上半部检验信息

    private EditText mEditBlackBack; // 黑面后
    private EditText mEditRedBack;
    private EditText mEditBlackFront;
    private EditText mEditRedFront;
    private TextView mBrDifferentBack; // 黑减红 后
    private TextView mBrDifferentFront;
    private TextView mBfDifferentBlack; // 后减前 黑
    private TextView mBfDifferentRed;
    private TextView mHeightDifferent; // 高差中数
    private TextView mLevelingDeltaH; // ΔH
    private TextView mLevelingInfo; // 水准测试误差信息
    private Spinner mParamKBack; // 后面K
    private Spinner mParamKFront;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paramK = getResources().getStringArray(R.array.param_k);
        mDeltaHStandard = getStandard("leveling_standard", 5);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_leveling, container, false);
        init(v);
        initButtonListener();
        return v;
    }

    @Override
    protected void initGetView(View v) {
        mLevelingSwitch = (ToggleButton) v.findViewById(R.id.leveling_switch);
        mLayoutFour  = (LinearLayout) v.findViewById(R.id.layout_leveling_four);
        mCalcButton  = (Button) v.findViewById(R.id.btn_calc);
        mResetButton = (Button) v.findViewById(R.id.btn_reset);
        mParamKBack  = (Spinner) v.findViewById(R.id.param_k_back);
        mParamKFront = (Spinner) v.findViewById(R.id.param_k_front);
        mEditBackDown  = (EditText) v.findViewById(R.id.edit_back_down);
        mEditBackUp    = (EditText) v.findViewById(R.id.edit_back_up);
        mEditFrontDown = (EditText) v.findViewById(R.id.edit_front_down);
        mEditFrontUp   = (EditText) v.findViewById(R.id.edit_front_up);
        mEditBlackBack = (EditText) v.findViewById(R.id.edit_black_back);
        mEditRedBack   = (EditText) v.findViewById(R.id.edit_red_back);
        mEditBlackFront= (EditText) v.findViewById(R.id.edit_black_front);
        mEditRedFront  = (EditText) v.findViewById(R.id.edit_red_front);
        mDistanceBack      = (TextView) v.findViewById(R.id.distance_back);
        mDistanceFront     = (TextView) v.findViewById(R.id.distance_front);
        mDistanceDifferent = (TextView) v.findViewById(R.id.distance_different);
        mDistanceInfo      = (TextView) v.findViewById(R.id.distance_info);
        mBrDifferentBack   = (TextView) v.findViewById(R.id.br_different_back);
        mBrDifferentFront  = (TextView) v.findViewById(R.id.br_different_front);
        mBfDifferentBlack  = (TextView) v.findViewById(R.id.bf_different_black);
        mBfDifferentRed    = (TextView) v.findViewById(R.id.bf_different_red);
        mHeightDifferent   = (TextView) v.findViewById(R.id.height_different);
        mLevelingDeltaH    = (TextView) v.findViewById(R.id.leveling_delta_h);
        mLevelingInfo      = (TextView) v.findViewById(R.id.leveling_info);
    }

    @Override
    protected void initEditTextList() {
        mEditTextList.add(mEditBackDown);
        mEditTextList.add(mEditBackUp);
        mEditTextList.add(mEditFrontDown);
        mEditTextList.add(mEditFrontUp);
        mEditTextList.add(mEditBlackBack);
        mEditTextList.add(mEditRedBack);
        mEditTextList.add(mEditBlackFront);
        mEditTextList.add(mEditRedFront);
    }

    private void initButtonListener() {
        // CalcButton Listener
        mCalcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
        // ResetButton Listener
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        // ToggleButton Listener
        mLevelingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                if (isChecked) {
                    mLevelingType = FOUR_LEVELING;
                    mLayoutFour.setVisibility(View.VISIBLE);
                } else {
                    mLevelingType = GENERAL_LEVELING;
                    mLayoutFour.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void calculate() {
        if (mLevelingType == FOUR_LEVELING)
            calculateFour();
        calculateGeneral();
    }

    private void calculateGeneral() {
        double blackBack   = getValue(mEditBlackBack),
                blackFront = getValue(mEditBlackFront),
                redBack    = getValue(mEditRedBack),
                redFront   = getValue(mEditRedFront),
                paramKBack = getValue(mParamKBack),
                paramKFront= getValue(mParamKFront);
        double bfDifferentBlack = blackBack - blackFront,
                bfDifferentRed  = redBack - redFront;
        showValue(mBfDifferentBlack, bfDifferentBlack);
        showValue(mBfDifferentRed, bfDifferentRed);
        double brDifferentBack = paramKBack + blackBack - redBack,
                brDifferentFront  = paramKFront + blackFront - redFront;
        showValue(mBrDifferentBack, brDifferentBack);
        showValue(mBrDifferentFront, brDifferentFront);
        int param100 = paramKBack == 4787 ? -100 : 100;
        double heightDifferent = (bfDifferentBlack + bfDifferentRed + param100) / 2;
        showValue(mHeightDifferent, heightDifferent);
        double deltaH = bfDifferentBlack - param100 - bfDifferentRed;
        showValue(mLevelingDeltaH, deltaH);
        if(Math.abs(deltaH) < mDeltaHStandard) {
            mLevelingInfo.setText(getString(R.string.text_success));
            mLevelingInfo.setTextColor(getResources().getColor(R.color.success));
        } else {
            mLevelingInfo.setText(getString(R.string.text_fail));
            mLevelingInfo.setTextColor(getResources().getColor(R.color.fail));
        }
    }

    private void calculateFour() {
        double backDown   = getValue(mEditBackDown),
                backUp    = getValue(mEditBackUp),
                frontDown = getValue(mEditFrontDown),
                frontUp   = getValue(mEditFrontUp);
        double distanceBack   = backDown - backUp,
                distanceFront = frontDown - frontUp;
        showValue(mDistanceBack, distanceBack);
        showValue(mDistanceFront, distanceFront);
        double different = distanceBack - distanceFront;
        showValue(mDistanceDifferent, different);
        if(Math.abs(different) < 3000) {
            mDistanceInfo.setText(getString(R.string.text_success));
            mDistanceInfo.setTextColor(getResources().getColor(R.color.success));
        } else {
            mDistanceInfo.setText(getString(R.string.text_fail));
            mDistanceInfo.setTextColor(getResources().getColor(R.color.fail));
        }
    }

    private double getValue(EditText edit) {
        String value = edit.getText().toString();
        if(value.length() < 1)
            return 0;
        return Double.parseDouble(value);
    }

    private double getValue(Spinner spinner) {
        return Double.parseDouble(paramK[(int)spinner.getSelectedItemId()]);
    }

    private void showValue(TextView text, double value) {
        text.setText(Double.toString(value));
    }

}
