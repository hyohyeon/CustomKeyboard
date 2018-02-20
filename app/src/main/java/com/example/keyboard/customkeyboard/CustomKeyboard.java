/**
 * Copyright 2013 Maarten Pennings
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 */

package com.example.keyboard.customkeyboard;

import java.util.List;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


class CustomKeyboard {

    private KeyboardView mKeyboardView;
    private Activity mHostActivity;

    private OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {

        public final static int CodeDelete = -5;
        public final static int CodeCancel = -3;
        public final static int CodePrev = 55000;
        public final static int CodeAllLeft = 55001;
        public final static int CodeLeft = 55002;
        public final static int CodeRight = 55003;
        public final static int CodeAllRight = 55004;
        public final static int CodeNext = 55005;
        public final static int CodeClear = 55006;


        @Override
        public void onKey(int primaryCode, int[] keyCodes) {


            //현재 포커스 가져오기
            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
            if (focusCurrent == null || focusCurrent.getClass() != EditText.class) return;
            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();
            int start = edittext.getSelectionStart();
            if (primaryCode == CodeCancel) {
                hideCustomKeyboard();
            } else if (primaryCode == CodeDelete) {
                if (editable != null && start > 0) editable.delete(start - 1, start);
                if (edittext.hasSelection() == true) {
                    edittext.setText(Character.toString((char) primaryCode));
                }
            } else if (primaryCode == CodeClear) {
                if (editable != null) editable.clear();
            } else if (primaryCode == CodeLeft) {
                if (start > 0) edittext.setSelection(start - 1);
            } else if (primaryCode == CodeRight) {
                if (start < edittext.length()) edittext.setSelection(start + 1);
            } else if (primaryCode == CodeAllLeft) {
                edittext.setSelection(0);
            } else if (primaryCode == CodeAllRight) {
                edittext.setSelection(edittext.length());
            } else if (primaryCode == CodePrev) {
                View focusNew = edittext.focusSearch(View.FOCUS_BACKWARD);
                if (focusNew != null) focusNew.requestFocus();
            } else if (primaryCode == CodeNext) {
                View focusNew = edittext.focusSearch(View.FOCUS_FORWARD);
                if (focusNew != null) focusNew.requestFocus();
            } else { // insert character
                if (edittext.hasSelection() == true) {
                    edittext.setText(Character.toString((char) primaryCode));
                    edittext.setSelection(edittext.length());
                } else {
                    editable.insert(start, Character.toString((char) primaryCode));
                }

            }
        }

        @Override
        public void onPress(int arg0) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeUp() {
        }
    };


    public List<Keyboard.Key> myKeys = null;

    public CustomKeyboard(Activity host, int viewid, int layoutid) {
        mHostActivity = host;
        mKeyboardView = (KeyboardView) mHostActivity.findViewById(viewid);
        mKeyboardView.setKeyboard(new Keyboard(mHostActivity, layoutid));
        mKeyboardView.setPreviewEnabled(false); // NOTE Do not show the preview balloons
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        myKeys = mKeyboardView.getKeyboard().getKeys();

    }

    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    public void showCustomKeyboard(View v) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);

        if (v != null)
            ((InputMethodManager) mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    public void registerEditText(int resid) {
        EditText edittext = (EditText) mHostActivity.findViewById(resid);
        edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom keyboard when the edit box gets focus, but also hide it when the edit box loses focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showCustomKeyboard(v);
                } else {
                    hideCustomKeyboard();
                }
            }
        });
        edittext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });
        edittext.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();
                edittext.setInputType(InputType.TYPE_NULL);
                edittext.onTouchEvent(event);
                edittext.setInputType(inType);
                edittext.selectAll();

                return true;
            }
        });
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

}


