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
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // ��Ļ�����أ��磺480px��
        //		int screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // ��Ļ�ߣ����أ��磺800p��
        Constants.viewWidth = (int) (screenWidth * scale);
        Constants.viewHeight = screenWidth;
        setContentView(R.layout.activity_main);
        findViewAndButton();
    }
    private void findViewAndButton() {
        // �Զ����View
        panView= (ZhuanpanView)findViewById(R.id.zhuanpanView);
        // ��ʼ��ת�İ�ť
        Button startButton = (Button) this.findViewById(R.id.startButton);
        Button stopButton = (Button) this.findViewById(R.id.stopButton);
        startButton.setOnClickListener(this);
        // ֹͣ��ת�İ�ť
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
