package com.example.client.view.myWidgets;

import android.content.Context; 
import android.graphics.Canvas; 
import android.graphics.Color; 
import android.graphics.Paint; 
import android.view.SurfaceHolder; 
import android.view.SurfaceHolder.Callback; 
import android.view.SurfaceView; 
import android.view.View; 
import android.view.inputmethod.BaseInputConnection; 
import android.view.inputmethod.CompletionInfo; 
import android.view.inputmethod.EditorInfo; 
import android.view.inputmethod.InputConnection; 
import android.view.inputmethod.InputMethodManager; 
  
public class SurfaceInput extends SurfaceView implements SurfaceHolder.Callback , Runnable { 
    SurfaceHolder holder=null; 
    String inputString="xyz"; 
    InputMethodManager input=null; 
    public SurfaceInput(Context context) { 
        super(context); 
        holder=this.getHolder(); 
        holder.addCallback(this); 
        this.setFocusable(true); 
        this.setFocusableInTouchMode(true); 
        
        input=(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE); 
        
        // TODO Auto-generated constructor stub 
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, 
            int height) { 
        // TODO Auto-generated method stub 
        new Thread(this).start(); 
        input.showSoftInput(this, 0); 
    }

    public void surfaceCreated(SurfaceHolder holder) { 
        // TODO Auto-generated method stub 
        
    }

    public void surfaceDestroyed(SurfaceHolder holder) { 
        // TODO Auto-generated method stub 
        
    }

    class MyInputConnection extends BaseInputConnection{

        public MyInputConnection(View targetView, boolean fullEditor) { 
            super(targetView, fullEditor); 
            // TODO Auto-generated constructor stub 
        } 
        public boolean commitText(CharSequence text, int newCursorPosition){ 
            inputString=inputString+(String) text; 
            return true; 
        } 
        
    }

    @Override 
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) { 
        // TODO Auto-generated method stub 
        return new MyInputConnection(this,false);//super.onCreateInputConnection(outAttrs); 
    }

    public void run() { 
        // TODO Auto-generated method stub 
        while(true){ 
            Canvas c=holder.lockCanvas(); 
            Paint p=new Paint(); 
            p.setColor(Color.RED); 
            c.drawColor(Color.WHITE); 
            c.drawText(inputString, 100, 100, p); 
            holder.unlockCanvasAndPost(c); 
            
        } 
    } 
}