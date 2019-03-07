/*
 * Copyright 2018 ACINQ SAS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.acinq.eclair.wallet.utils;

import android.content.Context;
import android.text.Editable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by DPA on 05/02/19.
 */
public interface TechnicalHelper {
  abstract class SimpleTextWatcher implements android.text.TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  }

  /**
   * Detects left and right swipes across a view.
   */
  public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(final Context context) {
      gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }

    /**
     * Returns true if the motion event is consumed
     */
    public boolean onClick() {
      return true;
    }

    public boolean onTouch(final View v, final MotionEvent event) {
      return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

      private static final int SWIPE_DISTANCE_THRESHOLD = 100;
      private static final int SWIPE_VELOCITY_THRESHOLD = 100;

      @Override
      public boolean onDown(final MotionEvent e) {
        return false;
      }

      @Override
      public boolean onSingleTapUp(MotionEvent e) {
        onClick();
        return super.onSingleTapUp(e);
      }

      @Override
      public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
        float distanceX = e2.getX() - e1.getX();
        float distanceY = e2.getY() - e1.getY();
        // left <-> right
        if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
          if (distanceX > 0) {
            onSwipeRight();
          } else {
            onSwipeLeft();
          }
          return true;
        }
        // top <-> bottom
        if (Math.abs(distanceY) > Math.abs(distanceX) && Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
          if (distanceY > 0) {
            onSwipeBottom();
          } else {
            onSwipeTop();
          }
          return true;
        }
        return false;
      }
    }
  }
}
