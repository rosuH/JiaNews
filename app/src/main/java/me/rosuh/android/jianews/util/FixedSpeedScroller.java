package me.rosuh.android.jianews.util;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * 功能：重写 Scroller 类，然后在调用时通过反射的方式修改 ViewPager 切换速度
 * @author https://www.jianshu.com/p/44f6e3502412
 */
public class FixedSpeedScroller extends Scroller {
    public int mDuration=1500;

    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override public void startScroll(int startX, int startY, int dx, int dy) {
        startScroll(startX,startY,dx,dy,mDuration);
    }

    @Override public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    public int getmDuration() {
        return mDuration;
    }

    public void setmDuration(int duration) {
        mDuration = duration;
    }
}
