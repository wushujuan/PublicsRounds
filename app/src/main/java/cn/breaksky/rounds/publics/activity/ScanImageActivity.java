package cn.breaksky.rounds.publics.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;
import cn.breaksky.rounds.publics.R;

/**
 * 实现左右滑动浏览图片效果
 * @author 吴淑娟 2017-7-20
 *
 */
public class ScanImageActivity extends Activity implements OnGestureListener{
	/**<b>存放图片的view</b>*/
	private ViewFlipper flipper;
	/**<b>左右滑动手势</b>*/
	private GestureDetector detector;
	
	/**<b>播放图片张数</b>*/
	private int image_size = 0;

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        
        detector = new GestureDetector(this);
		flipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper1);
		 
        //从EventProcessActivity获取图片保存路径参数
        Intent intent = getIntent();
        String[] path = intent.getStringExtra("FILEPATH").split(";");
        
		for (int i = 0; i < path.length; i++) {
			if (path[i] != null && path[i].trim().length() > 0) {
				File file = new File(path[i]);
				flipper.addView(addTextView(file));
				image_size++;
			}
		}
        Toast.makeText(this, "请左右滑动浏览图片！", Toast.LENGTH_SHORT).show(); 
    }
    private View addTextView(File file) {
		ImageView iv = new ImageView(this);
		iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		iv.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
		return iv;
	}
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	return this.detector.onTouchEvent(event);
    }
    
    @Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
    /**
     * 左右滑动时触发
     */
    @Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 120) {//左滑
			if (flipper.getDisplayedChild() == image_size - 1) {
				flipper.stopFlipping();
                Toast.makeText(this, "当前为最后一张图片", Toast.LENGTH_SHORT).show(); 
                return false;
            } else {
           	    this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
    			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
    			this.flipper.showNext(); 
    			return true;
            }
		} else if (e1.getX() - e2.getX() < -120) {//右滑
			if (flipper.getDisplayedChild() == 0) {
                flipper.stopFlipping();
                Toast.makeText(this, "当前为第一张图片", Toast.LENGTH_SHORT).show(); 
                return false;
            } else {
				this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
				this.flipper.showPrevious();
				return true;
            }
		}
		
		return false;
	}
    
    @Override
    public void onLongPress(MotionEvent e) {
    	// TODO Auto-generated method stub
    	
    }
    
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
    		float distanceY) {
    	// TODO Auto-generated method stub
    	return false;
    }
    
    @Override
    public void onShowPress(MotionEvent e) {
    	// TODO Auto-generated method stub
    	
    }
    
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
    	// TODO Auto-generated method stub
    	return false;
    }
}
