package net.kdt.pojavlaunch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ConsentActivity extends Activity {
    private static final String TARGET = "net.kdt.pojavlaunch.TestStorageActivity";
    private static final int BG = 0xFF14161A;
    private static final int CARD = 0xFF1E2127;
    private static final int CARD_HI = 0xFF2C313A;
    private static final int TXT = 0xFFF1F3F5;
    private static final int SUB = 0xFFB4BAC2;
    private static final int OK = 0xFF3DDC97;
    private static final int OK_TXT = 0xFF06281C;
    private static final int WARN = 0xFFFFC857;

    private boolean decided;
    private final List<CheckBox> boxes = new ArrayList<>();
    private TextView yes;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (Consent.granted(this)) {
            go();
            return;
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            @SuppressWarnings("deprecation")
            public WindowInsets onApplyWindowInsets(View v, WindowInsets in) {
                v.setPadding(in.getSystemWindowInsetLeft(), in.getSystemWindowInsetTop(),
                        in.getSystemWindowInsetRight(), in.getSystemWindowInsetBottom());
                return in;
            }
        });

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(20), dp(24), dp(20), dp(16));

        for (String line : readLines()) {
            if (line.length() < 3) continue;
            char kind = line.charAt(0);
            String text = line.substring(2);
            if (kind == 'T') {
                body.addView(text(text, 20f, WARN, true, 0));
            } else if (kind == 'H') {
                body.addView(text(text, 17f, TXT, true, 22));
            } else if (kind == 'P') {
                body.addView(text(text, 14f, SUB, false, 8));
            } else if (kind == 'B') {
                body.addView(text("•  " + text, 14f, SUB, false, 5));
            } else if (kind == 'C') {
                CheckBox c = new CheckBox(this);
                c.setText(text);
                c.setTextColor(TXT);
                c.setTextSize(14f);
                c.setPadding(dp(8), dp(10), 0, dp(10));
                c.setOnCheckedChangeListener((v, on) -> refresh());
                boxes.add(c);
                body.addView(c);
            }
        }

        ScrollView sv = new ScrollView(this);
        sv.addView(body);
        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1f));

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setPadding(dp(16), dp(12), dp(16), dp(16));
        bar.setBackgroundColor(CARD);
        TextView no = button("NO ACEPTO", CARD_HI, TXT);
        no.setOnClickListener(v -> reject());
        yes = button("ACEPTO", OK, OK_TXT);
        yes.setOnClickListener(v -> accept());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1f);
        lp.rightMargin = dp(8);
        bar.addView(no, lp);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, -2, 1f);
        lp2.leftMargin = dp(8);
        bar.addView(yes, lp2);
        root.addView(bar, new LinearLayout.LayoutParams(-1, -2));

        refresh();
        setContentView(root);

        if (Build.VERSION.SDK_INT >= 33) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, this::reject);
        }
    }

    private List<String> readLines() {
        List<String> out = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(
                getResources().openRawResource(R.raw.eula_es), StandardCharsets.UTF_8))) {
            String s;
            while ((s = r.readLine()) != null) out.add(s);
        } catch (Throwable e) {
        }
        return out;
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private TextView text(String s, float size, int color, boolean bold, int topDp) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(t.getTypeface(), android.graphics.Typeface.BOLD);
        else t.setLineSpacing(0f, 1.15f);
        t.setPadding(0, dp(topDp), 0, 0);
        return t;
    }

    private TextView button(String s, int bg, int fg) {
        TextView t = text(s, 15f, fg, true, 0);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(12), dp(16), dp(12), dp(16));
        GradientDrawable d = new GradientDrawable();
        d.setColor(bg);
        d.setCornerRadius(dp(16));
        t.setBackground(d);
        return t;
    }

    private boolean allChecked() {
        if (boxes.isEmpty()) return false;
        for (CheckBox c : boxes) if (!c.isChecked()) return false;
        return true;
    }

    private void refresh() {
        if (yes != null) yes.setAlpha(allChecked() ? 1f : 0.4f);
    }

    private void accept() {
        if (decided || !allChecked()) return;
        decided = true;
        Consent.accept(this);
        go();
    }

    private void go() {
        try {
            startActivity(new Intent(this, Class.forName(TARGET)));
        } catch (ClassNotFoundException e) {
        }
        finish();
    }

    private void reject() {
        if (decided) return;
        decided = true;
        LinearLayout t = new LinearLayout(this);
        t.setOrientation(LinearLayout.VERTICAL);
        t.setBackgroundColor(BG);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(28), dp(28), dp(28), dp(28));
        TextView a = text("Gracias por su comprensión", 22f, TXT, true, 0);
        a.setGravity(Gravity.CENTER);
        TextView c = text("Al no otorgar el consentimiento, la aplicación no se conectó a nada. Se cerrará por completo y borrará sus propios datos.", 14f, SUB, false, 12);
        c.setGravity(Gravity.CENTER);
        t.addView(a);
        t.addView(c);
        setContentView(t);
        new Handler(Looper.getMainLooper()).postDelayed(() -> Consent.wipe(this), 2500);
    }

    @Override
    public void onBackPressed() {
        reject();
    }
}
