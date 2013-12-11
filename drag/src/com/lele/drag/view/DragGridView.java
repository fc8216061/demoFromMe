package com.lele.drag.view;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lele.drag.R;
import com.lele.drag.adapter.GridViewAdapter;
import com.lele.drag.util.MyAnimationListener;

public class DragGridView extends GridView {
    
        private String TAG = "DragGridView";
        private Context mContext;
        private int dragPosition;
        private int dropPosition;
        
        //移动图片参数
        private int halfBitmapWidth,halfBitmapHeight;
        private ImageView dragImageView = null;
        private WindowManager windowManager = null;
        private WindowManager.LayoutParams windowParams = null;
        
        //计算偏移量
        private boolean isCountDeviation = false;
        private int mLongClickX,mLongClickY;
        private int DeviationX,DeviationY;//setOnItemLongClickListener与onTouchEvent偏移量
    
        //创建动画后若已松手操作判断
        private boolean isActionUp = false;
        
        private int margin_left,margin_top;
        
        public DragGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
                init(context);
        }
    
        public DragGridView(Context context) {
            super(context);
                init(context);
        }
    
        private void init(Context context) {
            mContext = context;
        }
    
	public boolean setOnItemLongClickListener(final MotionEvent ev) {
		this.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Log.e(TAG, "---setOnItemLongClickListener--");
				mLongClickX = (int) ev.getX();
				mLongClickY = (int) ev.getY();
				dragPosition = dropPosition = arg2;
				isActionUp = false;
				((GridViewAdapter) getAdapter()).setMovingState(true);

				ViewGroup itemView = (ViewGroup) getChildAt(dragPosition
						- getFirstVisiblePosition());
				LinearLayout.LayoutParams l = ((LinearLayout.LayoutParams) itemView
						.findViewById(R.id.g_one).getLayoutParams());
				margin_left = l.leftMargin;
				margin_top = l.topMargin;

				itemView.destroyDrawingCache();
				itemView.setDrawingCacheEnabled(true);
				itemView.setDrawingCacheBackgroundColor(0x000000);
				Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache(true));
				Bitmap bitmap = Bitmap.createBitmap(bm, l.leftMargin,
						l.topMargin, bm.getWidth() - l.leftMargin
								- l.rightMargin, bm.getHeight() - l.topMargin
								- l.bottomMargin);

				showCreateDragImageAnimation(itemView, bitmap);
				return false;
			};
		});
		return super.onInterceptTouchEvent(ev);
	}
    
        /**
         * 生成图片过程
         * @param itemView
         * @param bitmap
         */
	private void showCreateDragImageAnimation(final ViewGroup itemView,
			final Bitmap bitmap) {
		Log.e(TAG, "    showCreateDragImageAnimation    ");
		halfBitmapWidth = bitmap.getWidth() / 2;
		halfBitmapHeight = bitmap.getHeight() / 2;

		TranslateAnimation animation = new TranslateAnimation(0, mLongClickX
				- halfBitmapWidth - itemView.getLeft(), 0, mLongClickY
				- halfBitmapHeight - getContentVieTop()
				- (itemView.getTop() + getContentVieTop()));
		animation.setFillAfter(false);
		animation.setDuration(300);

		animation.setAnimationListener(new MyAnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if (!isActionUp) {
					createBitmapInWindow(bitmap, mLongClickX, mLongClickY);
					itemView.setVisibility(View.GONE);
				}
			}
		});
		itemView.startAnimation(animation);
	}
    /**
     * 创建图片
     * @param bm
     * @param x
     * @param y
     */
	private void createBitmapInWindow(Bitmap bm, int x, int y) {
		Log.e(TAG, "   createBitmapInWindow  ");
		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
		windowParams.x = x - halfBitmapWidth;
		windowParams.y = y - getContentVieTop() - halfBitmapHeight;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.alpha = 0.2f;
		ImageView iv = new ImageView(getContext());
		iv.setImageBitmap(bm);
		windowManager = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			Log.e(TAG, "   createBitmapInWindow   111 ");
		}
		windowManager.addView(iv, windowParams);
		dragImageView = iv;
	}
    
        private int contentViewTop = 0;
        /**
         * 计算状态栏高度
         * @return
         */
	private int getContentVieTop() {
		if (contentViewTop == 0) {

			Window win = ((Activity) mContext).getWindow();
			contentViewTop = win.findViewById(Window.ID_ANDROID_CONTENT)
					.getTop();
			if (contentViewTop == 0) {
				Rect rect = new Rect();
				win.getDecorView().getWindowVisibleDisplayFrame(rect);
				contentViewTop = rect.top;
			}
		}
		return contentViewTop;
	}
    
        /**
         * 拖动图片
         * @param x
         * @param y
         */
	private void onDrag(int x, int y) {
		Log.e(TAG, "----onDrag-----");
		if (dragImageView != null) {
			windowParams.alpha = 0.8f;
			windowParams.x = x - DeviationX - halfBitmapWidth;
			windowParams.y = y - DeviationY - getContentVieTop()
					- halfBitmapHeight;
			windowManager.updateViewLayout(dragImageView, windowParams);
		}
	}
    
        @Override
	public boolean onTouchEvent(MotionEvent ev) {
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (dragImageView != null) {
				if (!isCountDeviation) {
					DeviationX = x - mLongClickX;
					DeviationY = y - mLongClickY;

					isCountDeviation = true;
				}
				onDrag(x, y);
				onItemsMove(x, y);
			}
			break;
		case MotionEvent.ACTION_UP:
			isActionUp = true;
			if (dragImageView != null) {
				animationMap.clear();
				showDropAnimation(x, y);
			}
			Log.e(TAG, "  MotionEvent.ACTION_UP  ");
			break;
		}
		return super.onTouchEvent(ev);
	}
    
        /**
         * 放手动画
         * @param x
         * @param y
         */
	private void showDropAnimation(int x, int y) {
		Log.e(TAG, "     showDropAnimation    ");
		final ViewGroup moveView = (ViewGroup) getChildAt(dragPosition);
		TranslateAnimation go = new TranslateAnimation(x - halfBitmapWidth
				- moveView.getLeft(), 0, y - halfBitmapHeight
				- moveView.getTop(), 0);
		go.setFillAfter(false);
		go.setDuration(300);
		go.setAnimationListener(new MyAnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				moveView.setVisibility(View.VISIBLE);
				if (dragImageView != null) {
					windowManager.removeView(dragImageView);
					dragImageView = null;
				}
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				GridViewAdapter adapter = (GridViewAdapter) getAdapter();
				adapter.setMovingState(false);
				adapter.notifyDataSetChanged();
			}
		});
		moveView.startAnimation(go);
	}
    
        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
        	Log.e(TAG, "--onInterceptTouchEvent----");
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                Log.e(TAG, "onInterceptTouchEvent   111");
                    return setOnItemLongClickListener(ev);
            }
            return super.onInterceptTouchEvent(ev);
        }
    /**
     * 多item移动
     * @param x
     * @param y
     */
	public void onItemsMove(int x, int y) {
		Log.e(TAG, "      onItemsMove      ");
		dropPosition = pointToPosition(x, y);
		if (dropPosition == AdapterView.INVALID_POSITION)
			return;

		int MoveNum = dropPosition - dragPosition;
		Log.e(TAG, "  dropPosition = " + dropPosition + "dragPosition = "
				+ dragPosition);
		Log.e(TAG, "      MoveNum = " + MoveNum);
		if (MoveNum != 0 && !isMovingFastConflict(MoveNum)) {
			int itemMoveNum = Math.abs(MoveNum);
			for (int i = 0; i < itemMoveNum; i++) {
				int holdPosition;
				if (MoveNum > 0) {
					holdPosition = dragPosition + 1;
					Log.e(TAG, "   holdPosition = " + holdPosition);
				} else {
					holdPosition = dragPosition - 1;
				}
				((GridViewAdapter) getAdapter()).exchange(holdPosition,
						dragPosition, dropPosition);
				ViewGroup moveView = (ViewGroup) getChildAt(holdPosition);
				Animation animation = getMoveAnimation(getChildAt(holdPosition)
						.getLeft(), getChildAt(holdPosition).getTop(),
						getChildAt(dragPosition).getLeft(),
						getChildAt(dragPosition).getTop());

				animation.setAnimationListener(new listener(holdPosition));
				dragPosition = holdPosition;
				moveView.startAnimation(animation);
			}
		}
	}
    
        /**
         * 滑动太快冲突
         * @param MoveNum
         * @return
         */
        boolean isMovingFastConflict(int MoveNum){
        	Log.e(TAG, "----isMovingFastConflict-----");
            int itemMoveNum = Math.abs(MoveNum);
                int dragP = dragPosition;
                for (int i = 0; i < itemMoveNum; i++) {
                    int holdPosition;
                        if(MoveNum > 0){
                            holdPosition = dragP + 1;
                        }else{
                            holdPosition = dragP - 1;
                        }
                    if(animationMap.containsKey(holdPosition)){
                        return true;
                    }
                    dragP = holdPosition;
                }
            return false;
        }
    
        private class listener extends MyAnimationListener{
            int holdPosition;
                public listener(int primaryKey){
                    this.holdPosition = primaryKey;// System.nanoTime();
                }
            
                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub
                    super.onAnimationStart(animation);
                        animationMap.put(holdPosition, true);
                }
            
                @Override
                public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub
                    if (animationMap.containsKey(holdPosition)) {
                        animationMap.remove(holdPosition);
                    }
                    if (animationMap.isEmpty()){
                        ((GridViewAdapter) getAdapter()).notifyDataSetChanged();
                    }
                }
        }
    /**
     * 动画记录map
     */
    private HashMap<Integer, Boolean> animationMap = new HashMap<Integer, Boolean>();
        
        
        /**
         * 获取移动动画
         * @param x
         * @param y
         * @param toX
         * @param toY
         * @return
         */
        public Animation getMoveAnimation(float x, float y,float toX,float toY) {
            TranslateAnimation go = new TranslateAnimation(0,toX - x, 0,toY -y);
                go.setFillAfter(true);
                go.setDuration(300);
                return go;
        }
    
}
