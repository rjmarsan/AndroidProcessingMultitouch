package com.rj.processing.mt;

import java.util.ArrayList;

import android.util.Log;
import android.view.MotionEvent;



public class MTManager {
	
	public ArrayList<Point> points;
	public ArrayList<Cursor> cursors;
	
	private final ArrayList<TouchListener> listeners;
	
	
	public MTManager() {
		this.points = new ArrayList<Point>(8);
		this.cursors = new ArrayList<Cursor>(8);
		this.listeners = new ArrayList<TouchListener>(8);
	}

	public void addTouchListener(final TouchListener t) {
		if (t == null) {
			System.out.println("Hey jerk! Quit trying to add a null touch listener to MTManager");
			return;
		}
		listeners.add(t);
	}
	
	public void surfaceTouchEvent(final MotionEvent me) {
		synchronized (cursors) {
			final int numPointers = me.getPointerCount();
			if (numPointers == 0) {
				//callback.touchEvent(me, 0, 0,0,0,0,0);
				fireTouchAllUp(me, null);
			}
			for (int i = 0; i < numPointers; i++) {
				touchEvent(me, i);
			}
			if (me.getPointerCount() == 1 && me.getAction() == MotionEvent.ACTION_UP) {//if the final finger is lifted...
				cursors.clear();
			}
			if (me.getPointerCount() < cursors.size()) {
				try {
					//Log.d("MTManager", "Hallo ");
					for (int i = 0; i < cursors.size(); i++) {
						final int pointerId = me.getPointerId(i);
						final int index = me.findPointerIndex(pointerId);
						if (index < 0) {
							cursors.remove(i);
						}
						else if (pointerId != index && i >= 1) {
							cursors.remove(i-1);
						}
					}
				} catch (Throwable e) {
					//Log.d("MTManager", "Error! This is weird: ");
					//e.printStackTrace();
				}
			}
		}
	}
	
	
	public void touchEvent(final MotionEvent me, final int i) {		
		final int pointerId = me.getPointerId(i);
		final float x = me.getX(i);
		final float y = me.getY(i);
		
		float vx = 0;
		float vy = 0;
		
		final int index = me.findPointerIndex(pointerId);
		
		maybeAddCapacity(cursors,index);
		
		Cursor c = cursors.get(index);
		final long ctime = System.currentTimeMillis();
		if (c != null && c.curId == pointerId /**&& ctime - c.currentPoint.time < 100**/ ) {
			c.updateCursor(new Point(x,y));

		}
		else {
			c = new Cursor(new Point(x,y), pointerId);
		}
		cursors.set(index, c);
		if (me.getAction() == MotionEvent.ACTION_UP) {
			if (me.getPointerId(me.getActionIndex()) == pointerId)
				cursors.remove(index);
		}
		
		vx = c.velX;
		vy = c.velY;
		

		final float size = me.getSize(i);

		fireTouchEvent(me, c, pointerId);
	}

	public void maybeAddCapacity(final ArrayList<?> something, final int index) {
		if (something.size() < index+1) {
			//points.ensureCapacity(index+4);
			something.add(null);
			something.add(null);
			something.add(null);
			something.add(null);
		}
	}
	
	
	public void fireTouchEvent(final MotionEvent me, final Cursor c, final int index) {
		final int meaction = me.getAction();
		final int meactionmasked = me.getActionMasked();
//		Log.d("MTManager", "Motion Event: "+me);
//		Log.d("MTManager", "Cursor: "+c);
//		Log.d("MTManager", "meaction: "+meaction+" masked:"+meactionmasked);
		if (meaction == MotionEvent.ACTION_DOWN || meactionmasked == MotionEvent.ACTION_POINTER_DOWN) {
			if (index == me.getActionIndex())
				fireTouchDown(me, c);
		}
		if (meaction == MotionEvent.ACTION_MOVE) {
			fireTouchMoved(me, c);
		}
		if (meaction == MotionEvent.ACTION_UP || meactionmasked == MotionEvent.ACTION_POINTER_UP || meaction == MotionEvent.ACTION_CANCEL) {
//			Log.d("MTManager", "ACTION UP c:"+c);
			if (index == me.getActionIndex()) {
				Log.d("MTManager", "Calling fireTouchUp");
				fireTouchUp(me, c);
			}
		}
		
		if (me.getPointerCount() == 1 && meaction == me.ACTION_UP) {//if the final finger is lifted...
			fireTouchAllUp(me, c);
		}	
	}
	
	

	public void fireTouchDown(final MotionEvent e, final Cursor c) {
		for (final TouchListener l : listeners) l.touchDown(c);
	}

	public void fireTouchMoved(final MotionEvent e, final Cursor c) {
		for (final TouchListener l : listeners) l.touchMoved(c);
	}

	public void fireTouchUp(final MotionEvent e, final Cursor c) {
		for (final TouchListener l : listeners) l.touchUp(c);
	}

	public void fireTouchAllUp(final MotionEvent e, final Cursor c) {
		for (final TouchListener l : listeners) l.touchAllUp(c);
	}	
	
	
	
	
	
}
