package com.sxt.chat.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.sxt.chat.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by izhaohu on 2018/3/21.
 */

public class SimpleEditText extends EditText {
    private float[] size;

    public SimpleEditText(Context context) {
        super(context);
        init(context);
    }

    public SimpleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SimpleEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!isOnlyPointNumber(SimpleEditText.this.getText().toString())) {
                    //删除多余输入的字（不会显示出来）
                    String trim = editable.toString().trim();
                    int indexOf = trim.indexOf(".");
                    if (indexOf + 2 < trim.length()) {
                        editable.delete(indexOf + 2, trim.length());
                        SimpleEditText.this.setText(editable);
                        SimpleEditText.this.setSelection(editable.length());//将光标移动到末尾
                    }
                }

                try {
                    String trim = SimpleEditText.this.getText().toString().trim();
                    float parseFloat = Float.parseFloat(trim);
                    if (size[0] <= parseFloat && parseFloat <= size[1]) {
                        SimpleEditText.this.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color_1));
                    } else {
                        SimpleEditText.this.setTextColor(ContextCompat.getColor(getContext(), R.color.main_orange));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean isOnlyPointNumber(String number) {//保留两位小数正则
        Pattern pattern = Pattern.compile("^\\d+\\.?\\d{0,1}$");
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    public void setSize(float[] bo_exceptionSize) {
        this.size = bo_exceptionSize;
    }
}
