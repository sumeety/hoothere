package com.sumeet.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.sumeet.callback.OnFinishAnimation;
import com.sumeet.hoothere.R;


public class AnimUtils {

	public AnimUtils(){};
	
//	private static Timer timer;
//	private static TimerTaskMove task = null;
//	
//	public static class TimerTaskMove extends TimerTask{
//		
//		private View view;
//		private float offsetStep;
//		private float xTo;
//		
//		public TimerTaskMove(View view, float step, float xTo){
//			super();
//			offsetStep = step;
//			this.view = view;
//			this.xTo = xTo;
//		}
//		
//		@Override
//		public void run(){
//			view.post(new Runnable(){
//				@Override
//				public void run(){					
//					float pos = view.getX();
//					view.setX(pos + offsetStep);
//					if (offsetStep < 0 && pos + offsetStep < xTo){
//						view.setX(xTo);
//						timer.cancel();
//					}
//					if (offsetStep > 0 && pos + offsetStep > xTo){
//						view.setX(xTo);
//						timer.cancel();					
//					}
//				}
//			});
//		}
//	}
	
	public static void AnimationMoveTo(final View view, int duration, final float xTo, final float yTo/*, final LinearLayout llParent*/){
		float x = view.getX();
		float xDelta = xTo - x;
//		float offsetStep = xDelta / (float)duration;
//		offsetStep = (xDelta > 0) ? 2.0f : -2.0f;
//		task = new TimerTaskMove(view, offsetStep, xTo);
//		timer = new Timer();
//		timer.schedule(task, 0, 1);
		final float fStep = xDelta / (duration / 20.0f);
		new CountDownTimer(duration, 20) {

			@Override
			public void onTick(long millisUntilFinished) {
				view.setX(view.getX() + fStep);
			}
			@Override
			public void onFinish() {
				view.setX(xTo);
			}

		}.start();

		
//		TranslateAnimation animation = new TranslateAnimation(x, xTo, 0, 0);
//		TranslateAnimation animation = new TranslateAnimation(0, xDelta, 0, 0);
////		TranslateAnimation animation = new TranslateAnimation(x, xTo, y, y);
//		animation.setDuration(duration);
//		animation.setFillAfter(false);
//		animation.setAnimationListener(new Animation.AnimationListener() {			
//			@Override
//			public void onAnimationStart(Animation animation) {
//				// TODO Auto-generated method stub
//				view.setVisibility(View.VISIBLE);
//			}
//			
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				view.setX(xTo);				
//				view.setVisibility(View.VISIBLE);
////				LayoutParams lp = (LayoutParams) view.getLayoutParams();
////				lp.leftMargin = (int)xTo;
////				llParent.updateViewLayout(view, lp);
//			}
//		});
//
//		view.startAnimation(animation);
	}
	
	public static void startZoomOut(Context context, final View v, long delay){
		v.setVisibility(View.VISIBLE);
		final Animation zoomOut = AnimationUtils.loadAnimation(context, R.anim.zoom_exit);
		zoomOut.setDuration(delay);
		zoomOut.setAnimationListener(new Animation.AnimationListener() {			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				v.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				v.setVisibility(View.GONE);
			}
		});
		v.startAnimation(zoomOut);
	}
	
	public static void startFadeIn(Context context, final View v, long delay, long duration, final OnFinishAnimation finishAnim){		
		v.setVisibility(View.VISIBLE);		
		final Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
		fadeIn.setDuration(duration);
		fadeIn.setStartOffset(delay);
		fadeIn.setAnimationListener(new Animation.AnimationListener() {			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				v.setVisibility(View.VISIBLE);
//				v.setAlpha(0);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				v.setVisibility(View.VISIBLE);
//				v.setAlpha(1.0f);
				if (finishAnim != null) {
					finishAnim.finishAnimation(v);
				}
			}
		});	
		v.startAnimation(fadeIn);
	}

	public static void startFadeOut(Context context, final View v, long delay, long duration, final OnFinishAnimation finishAnim){		
		v.setVisibility(View.VISIBLE);		
		final Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_out);
		fadeIn.setDuration(duration);
		fadeIn.setStartOffset(delay);
		fadeIn.setAnimationListener(new Animation.AnimationListener() {			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				v.setVisibility(View.VISIBLE);
//				v.setAlpha(0);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				v.setVisibility(View.GONE);
//				v.setAlpha(1.0f);
				if (finishAnim != null) {
					finishAnim.finishAnimation(v);
				}
			}
		});	
		v.startAnimation(fadeIn);
	}

}
