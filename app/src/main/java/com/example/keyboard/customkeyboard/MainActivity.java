package com.example.keyboard.customkeyboard;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends Activity {

    CustomKeyboard mCustomKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCustomKeyboard = new CustomKeyboard(this, R.id.keyboardview, R.xml.custom_keyboard);
        mCustomKeyboard.registerEditText(R.id.edittext0);
        mCustomKeyboard.registerEditText(R.id.edittext1);

    }

    @Override
    public void onBackPressed() {
        if (mCustomKeyboard.isCustomKeyboardVisible())
            mCustomKeyboard.hideCustomKeyboard();
        else
            this.finish();
    }

}