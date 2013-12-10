package com.xuhuawei.zhuanpan;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

//自定义的转盘View  
public class ZhuanpanView extends SurfaceView implements Runnable,
		SurfaceHolder.Callback {

	// 界面需要的图片
	private Bitmap bitmap = null;
	// 旋转矩阵
	private Matrix panRotate = new Matrix();
	private int startAngle = 0;
	private boolean ifRotate = false;
	private Bitmap bitmap1;
	private float centerX;
	private float centerY;
	private boolean isTouch = false;
	private boolean rotateEnabled = true;
	private boolean isRun=true;
	private float speed; // 速度
	private float acceleration; // 加速度
	private SurfaceHolder holder;
	private Context context;
	private AttributeSet attrs;

	public ZhuanpanView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public ZhuanpanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.attrs=attrs;
		init();
	}

	public ZhuanpanView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public void init() {
		Resources r = context.getResources();
		bitmap1 = BitmapFactory.decodeStream(r.openRawResource(R.drawable.clock3));
		attrs.getAttributeResourceValue(null,
				"clockImageSrc", 0);
		holder = getHolder();
		holder.addCallback(this);
		this.setFocusable(true);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		// 用线程来刷新界面
		Thread thread = new Thread(this);
		thread.start();
		updateView();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		isRun=false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(Constants.viewWidth, Constants.viewWidth);
	}

	// 重写View类的onDraw()函数
	// @Override
	// protected void onDraw(Canvas canvas) {
	private void updateView() {
		Canvas mCanvas = holder.lockCanvas();
		if(mCanvas==null){
			return;
		}
		
		if (bitmap == null) {
			bitmap = scaleBitmap(bitmap1);
		}
		// 画表盘图像
		centerX = Constants.viewWidth * 0.5f;
		centerY = Constants.viewWidth * 0.5f;
		panRotate.setRotate(startAngle, centerX, centerY);
//		 panRotate.postTranslate(0, 0);
		mCanvas.drawBitmap(bitmap, panRotate, null);
		drawButton(mCanvas);
		drawArrow(mCanvas);
		holder.unlockCanvasAndPost(mCanvas);
	}

	/**
	 * 绘制中心的圆点
	 * 
	 * @param mCanvas
	 */
	private void drawButton(Canvas mCanvas) {
		Paint localPaint2 = new Paint();
		localPaint2.setAntiAlias(true);
		localPaint2.setStrokeWidth(5);
		if (isTouch) {
			localPaint2.setColor(Color.RED);
		} else {
			localPaint2.setColor(Color.BLACK);
		}

		mCanvas.drawCircle(centerX, centerY, centerX / 3, localPaint2);
	}

	public void setDirection(float sp) {
		speed = sp;
		acceleration = Math.abs(speed) / 100;
	}

	/**
	 * 绘制箭头
	 * 
	 * @param mCanvas
	 */
	private void drawArrow(Canvas mCanvas) {
		Paint localPaint2 = new Paint();
		localPaint2.setAntiAlias(true);

		localPaint2.setStrokeWidth(5);
		localPaint2.setColor(Color.BLACK);
		// mCanvas.drawLine(CenterX, CenterY + radius / 5, CenterX, CenterY
		// - radius / 2, localPaint2);
		// 画竖线 也可以代替画图片
		mCanvas.drawLine(centerX, 0, centerX, 50, localPaint2);
		// 画圆点
		mCanvas.drawCircle(centerX, 3, 6, localPaint2);
	}

	private void checkDegree() {
			startAngle += speed;
			speed -= acceleration;
			if (speed <= 0) {
				startAngle = startAngle % 360;
				ifRotate=false;
			}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (isInRadius(x, y)) {
				isTouch = true;
				startRotate();
			}
			break;
		case MotionEvent.ACTION_UP:
			isTouch = false;
			break;
		}
		// 必须为true 不然不能接收到 MotionEvent.ACTION_UP
		return true;
	}

	private int getRandomNum() {
		Random random = new Random();
		return random.nextInt(5)+10;

	}

	/**
	 * 判断触电是否在圆点上
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isInRadius(float x, float y) {
		if ((centerX - centerX / 3) < x && (centerX + centerX / 3) > x) {
			if ((centerY - centerY / 3) < y && (centerY + centerY / 3) > y) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 图片大小缩放
	 * 
	 * @param bitmap
	 * @return
	 */
	private Bitmap scaleBitmap(Bitmap bitmap) {
		// 获取这个图片的宽和高
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 定义预转换成的图片的宽度和高度
		int newWidth = Constants.viewWidth;
		int newHeight = newWidth;
		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return resizedBitmap;
	}

	// 重写的run函数，用来控制转盘转动
	public void run() {
		while (isRun) {
			// 至于这里为什么同步这就像一块画布 你不能让两个人同时往上边画画
			if (ifRotate) {
				updateView();
				checkDegree();
			} 

		}
	}

	public void startRotate() {
		int num = getRandomNum();
		Log.v("menu", "num "+num);
		setDirection(num);
		this.ifRotate = true;
	}

	public void stopRotate() {
		this.ifRotate = false;
	}

}