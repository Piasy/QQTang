package com.example.client.view.myWidgets;

import android.view.View;
import android.view.inputmethod.BaseInputConnection;

public class MyInputConnection extends BaseInputConnection{

    String inputString="";

    public MyInputConnection(View targetView, boolean fullEditor) { 
        super(targetView, fullEditor); 
        // TODO Auto-generated constructor stub 
    } 
    public boolean commitText(CharSequence text, int newCursorPosition){ 
        inputString=inputString+(String) text; 
        return true; 
    } 
    
}