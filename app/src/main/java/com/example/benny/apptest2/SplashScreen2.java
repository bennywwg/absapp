package com.example.benny.apptest2;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

public class SplashScreen2 extends FragmentActivity {

    private FrameLayout frame;

    private FragmentManager manager;
    Fragment frag = null;

    public String email;
    public String passwordEmailHash;

    public WorkoutDay workout = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen2);

        manager = getSupportFragmentManager();

        frame = (FrameLayout) findViewById(R.id.splash_frame);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        frame.setLayoutParams(new ConstraintLayout.LayoutParams(frame.getWidth(), frame.getHeight() - navigation.getHeight()));
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final FragmentTransaction swap = manager.beginTransaction();
        swap.replace(R.id.splash_frame, new HomeFragment()).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String workoutJsonText = Util.readFromFile("savedWorkout.txt", getBaseContext());
        JSONObject unparsedWorkout = null;
        if(!workoutJsonText.isEmpty()) {
            try {
                unparsedWorkout = new JSONObject(workoutJsonText);
            } catch (Exception ignored) {
                unparsedWorkout = null;
            }
        }
        if(unparsedWorkout != null) {
            try {
                workout = new WorkoutDay(unparsedWorkout);
            } catch (Exception ignored) {
                workout = null;
            }
        }

        String loginInfo = Util.readFromFile("login.txt", getBaseContext());
        if(!loginInfo.isEmpty()) {
            String[] parts = loginInfo.split(" ");
            if(parts.length == 2) {
                email = parts[0];
                passwordEmailHash = parts[1];
                Util.userEmail = email;
                Util.userPasswordEmailHash = passwordEmailHash;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(workout != null) {
            try {
                Util.writeToFile("savedWorkout.txt", workout.toJSON().toString(), getBaseContext());
            } catch (Exception ignored) {
                Util.writeToFile("savedWorkout.txt", "{}", getBaseContext());
            }
        } else {
            Util.writeToFile("savedWorkout.txt", "{}", getBaseContext());
        }
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    frag = new HomeFragment();
                    break;
                case R.id.navigation_workout:
                    frag = new WorkoutFragment();
                    ((WorkoutFragment)frag).splashScreen = SplashScreen2.this; //bug city (maybe)
                    break;
                case R.id.navigation_macros:
                    frag = new MacrosFragment();
                    break;
                case R.id.navigation_statistics:
                    frag = new StatisticsFragment();
                    break;
            }

            if(frag != null) {
                final FragmentTransaction swap = manager.beginTransaction();
                swap.replace(R.id.splash_frame, frag).commit();
                return true;
            } else {
                return false;
            }
        }
    };
}
