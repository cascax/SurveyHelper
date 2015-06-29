package xyz.codeme.surveyhelper;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import xyz.codeme.surveyhelper.data.Angle;


public class TraverseFragment extends Fragment {
    private EditText mAngleA1Du;
    private EditText mAngleA1Fen;
    private EditText mAngleA1Miao;
    private EditText mAngleB1Du;
    private EditText mAngleB1Fen;
    private EditText mAngleB1Miao;
    private EditText mAngleA2Du;
    private EditText mAngleA2Fen;
    private EditText mAngleA2Miao;
    private EditText mAngleB2Du;
    private EditText mAngleB2Fen;
    private EditText mAngleB2Miao;
    private TextView mAngelResult1;
    private TextView mAngelResult2;
    private TextView mResultInfo;
    private TextView mResultDifferent;
    private TextView mResultArg;
    private Button   mCalcButton;
    private Button   mResetButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_traverse, container, false);

        mAngleA1Du   = (EditText) v.findViewById(R.id.angle_1a_du);
        mAngleA1Fen  = (EditText) v.findViewById(R.id.angle_1a_fen);
        mAngleA1Miao = (EditText) v.findViewById(R.id.angle_1a_miao);
        mAngleB1Du   = (EditText) v.findViewById(R.id.angle_1b_du);
        mAngleB1Fen  = (EditText) v.findViewById(R.id.angle_1b_fen);
        mAngleB1Miao = (EditText) v.findViewById(R.id.angle_1b_miao);
        mAngleA2Du   = (EditText) v.findViewById(R.id.angle_2a_du);
        mAngleA2Fen  = (EditText) v.findViewById(R.id.angle_2a_fen);
        mAngleA2Miao = (EditText) v.findViewById(R.id.angle_2a_miao);
        mAngleB2Du   = (EditText) v.findViewById(R.id.angle_2b_du);
        mAngleB2Fen  = (EditText) v.findViewById(R.id.angle_2b_fen);
        mAngleB2Miao = (EditText) v.findViewById(R.id.angle_2b_miao);
        mCalcButton  = (Button) v.findViewById(R.id.btn_calc);
        mResetButton = (Button) v.findViewById(R.id.btn_reset);
        mAngelResult1    = (TextView) v.findViewById(R.id.angle_result1);
        mAngelResult2    = (TextView) v.findViewById(R.id.angle_result2);
        mResultInfo      = (TextView) v.findViewById(R.id.result_info);
        mResultDifferent = (TextView) v.findViewById(R.id.result_different);
        mResultArg       = (TextView) v.findViewById(R.id.result_arg);

        mCalcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });

        return v;
    }

    /**
     * 计算水平角观测值
     */
    public void calculate() {
        // 获取上半测回角A、B，计算差
        Angle angleA1 = new Angle(mAngleA1Du.getText().toString(),
                mAngleA1Fen.getText().toString(),
                mAngleA1Miao.getText().toString());
        Angle angleB1 = new Angle(mAngleB1Du.getText().toString(),
                mAngleB1Fen.getText().toString(),
                mAngleB1Miao.getText().toString());
        Angle different1 = angleA1.different(angleB1);
        mAngelResult1.setText(different1.toString());

        // 获取下半测回角A、B，计算差
        Angle angleA2 = new Angle(mAngleA2Du.getText().toString(),
                mAngleA2Fen.getText().toString(),
                mAngleA2Miao.getText().toString());
        Angle angleB2 = new Angle(mAngleB2Du.getText().toString(),
                mAngleB2Fen.getText().toString(),
                mAngleB2Miao.getText().toString());
        Angle different2 = angleA2.different(angleB2);
        mAngelResult2.setText(different2.toString());

        // 误差分析，计算一测回平均角值
        Angle different = different1.different(different2);
        mResultDifferent.setText(getString(R.string.survey_difference)
                + different.toString());
        if(different.getRealDu() < 18.0/3600) {
            mResultInfo.setText(getString(R.string.text_success));
            mResultArg.setText(getString(R.string.survey_difference_arg)
                    + different1.add(different2).divide(2).toString());
        } else {
            mResultInfo.setText(getString(R.string.text_fail));
            mResultArg.setText("");
        }
    }


}
