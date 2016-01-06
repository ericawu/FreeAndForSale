package com.spe.luke.freeandgood;

/**
 * Created by Luke on 7/14/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;


public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener
{

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private Context m_context;
    private ViewFlipper m_ViewFlipper;

    public SwipeGestureDetector (Context context, ViewFlipper viewFlipper)
    {
        this.m_context = context;
        this.m_ViewFlipper = viewFlipper;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        try
        {
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                m_ViewFlipper.setInAnimation(AnimationUtils.loadAnimation(m_context, R.anim.left_in));
                m_ViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(m_context, R.anim.left_out));
                m_ViewFlipper.showNext();
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                m_ViewFlipper.setInAnimation(AnimationUtils.loadAnimation(m_context, R.anim.right_in));
                m_ViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(m_context, R.anim.right_out));
                m_ViewFlipper.showPrevious();
                return true;
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

}