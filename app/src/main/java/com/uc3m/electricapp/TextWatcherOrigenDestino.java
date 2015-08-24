package com.uc3m.electricapp;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

public class TextWatcherOrigenDestino implements TextWatcher{

    private View imagenClear;

    public TextWatcherOrigenDestino(View imagenClear){
        this.imagenClear = imagenClear;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.toString()==""){
            imagenClear.setVisibility(View.INVISIBLE);
        }
        else {
            imagenClear.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

}
