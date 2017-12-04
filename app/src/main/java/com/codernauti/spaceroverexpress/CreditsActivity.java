/* 
Space Rover Express
Copyright (C) 2017 Codernauti
Eduard Bicego, Federico Ghirardelli

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.codernauti.spaceroverexpress;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Eduard on 14-Nov-17.
 */

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits_act);

        ImageView astronaut = findViewById(R.id.credits_astronaut);
        astronaut.setVisibility(View.VISIBLE);

        /*astronaut.animate()
                .translationX(-500f)
                .translationY(-500f)
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(10000)
                .setInterpolator(new LinearInterpolator())
                .start();*/

        int duration = 80000;

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animY = ObjectAnimator.ofFloat(astronaut, "y", 1500f, -300f);
        animY.setInterpolator(new LinearInterpolator());

        ObjectAnimator animX = ObjectAnimator.ofFloat(astronaut, "x", 600f, -300f);
        animX.setInterpolator(new LinearInterpolator());

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(astronaut, "scaleX", 1.7f, 0f);
        scaleX.setInterpolator(new LinearInterpolator());

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(astronaut, "scaleY", 1.7f, 0f);
        scaleY.setInterpolator(new LinearInterpolator());


        animatorSet.setTarget(astronaut);
        animatorSet.setDuration(duration);
        animatorSet.play(animX)
                .with(animY)
                .with(scaleX)
                .with(scaleY);

        animatorSet.start();
    }
}
