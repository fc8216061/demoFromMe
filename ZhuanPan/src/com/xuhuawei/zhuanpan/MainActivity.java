package com.xuhuawei.zhuanpan;


import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{
    private float scale = 0.8f;
    private ZhuanpanView panView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
        //		int screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）
        Constants.viewWidth = (int) (screenWidth * scale);
        Constants.viewHeight = screenWidth;
        setContentView(R.layout.activity_main);
        findViewAndButton();
    }
    private void findViewAndButton() {
        // 自定义的View
        panView= (ZhuanpanView)findViewById(R.id.zhuanpanView);
        // 开始旋转的按钮
        Button startButton = (Button) this.findViewById(R.id.startButton);
        Button stopButton = (Button) this.findViewById(R.id.stopButton);
        startButton.setOnClickListener(this);
        // 停止旋转的按钮
        stopButton.setOnClickListener(this);

    }
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.startButton:
      panView.startRotate();
      break;
  case R.id.stopButton:
      panView.stopRotate();
      break;
  }
 }
}
