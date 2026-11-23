package com.kdt.mcgui;

import android.content.Context;
import android.util.AttributeSet;

import net.kdt.pojavlaunch.R;

public class MineEditText extends androidx.appcompat.widget.AppCompatEditText {
        public MineEditText(Context ctx) {
                super(ctx);
                init();
        }

        public MineEditText(Context ctx, AttributeSet attrs) {
                super(ctx, attrs);
                init();
        }

        public void init() {
                setBackgroundResource(R.drawable.lx_input);
                int p = (int) (16 * getResources().getDisplayMetrics().density);
                setPadding(p, p, p, p);
                setTextColor(0xFFEFE6D2);
                setHintTextColor(0xFFA39B8B);
        }
}
