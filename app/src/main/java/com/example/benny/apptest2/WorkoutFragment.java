package com.example.benny.apptest2;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONObject;

public class WorkoutFragment extends Fragment {

    public SplashScreen2 splashScreen;

    View parent;
    ViewPager pager = null;
    SectionsPagerAdapter adapter;

    ConstraintLayout generateWorkoutFrame;
    Button generateWorkoutButton;
    Button generateWorkoutResumeButton;
    SeekBar generateWorkoutTimeSlider;
    TextView generateWorkoutTextNumber;
    ProgressBar generateWorkoutSpinner;

    private void assignMembersAndSetup() {
        pager = (ViewPager) parent.findViewById(R.id.exercise_pager);
        adapter = new SectionsPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);

        generateWorkoutFrame = (ConstraintLayout) parent.findViewById(R.id.generate_workout_frame);
        generateWorkoutButton = (Button) parent.findViewById(R.id.generate_workout_button);
        generateWorkoutResumeButton = (Button) parent.findViewById(R.id.generate_workout_resume_button);
        generateWorkoutTimeSlider = (SeekBar) parent.findViewById(R.id.generate_workout_time_slider);
        generateWorkoutTextNumber = (TextView) parent.findViewById(R.id.generate_workout_text_number);
        generateWorkoutSpinner = (ProgressBar) parent.findViewById(R.id.generate_workout_spinner);

        generateWorkoutTimeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                generateWorkoutTextNumber.setText(("" + (i + 2) * 15));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        generateWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateWorkoutSpinner.setVisibility(View.VISIBLE);

                loadNewWorkout(generateWorkoutTimeSlider.getProgress() + 2);
            }
        });
        generateWorkoutResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_workout, container, false);

        assignMembersAndSetup();

        return parent;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(splashScreen.workout != null) {
            generateWorkoutResumeButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ExerciseFragment.newInstance(splashScreen.workout.workoutItems.get(position));
        }

        @Override
        public int getCount() {
            return (splashScreen.workout == null) ? 1 : splashScreen.workout.workoutItems.size();
        }

        @Override
        public int getItemPosition(Object frag) {
            return POSITION_NONE;
        }
    }

    private void updateUI() {
        adapter.notifyDataSetChanged();
        if(splashScreen.workout == null) {
            pager.setVisibility(View.GONE);
            generateWorkoutFrame.setVisibility(View.VISIBLE);
        } else {
            pager.setVisibility(View.VISIBLE);
            generateWorkoutFrame.setVisibility(View.GONE);
        }
    }

    private void loadNewWorkout(int numItems) {
        ExercisePostReceive receiver = new ExercisePostReceive();

        RequestData request = new RequestData();

        request.message = "{\"email\":\"" + splashScreen.email + "\"," +
        "\"passwordEmailHash\":\"" + splashScreen.passwordEmailHash + "\"," +
        "\"numItems\":" + numItems + "}";
        request.url = Util.exerciseConnection;

        receiver.execute(request);
    }


    private class ExercisePostReceive extends POSTer {
        @Override
        protected void onFinish() {
            if(jsonGood()){
                try {
                    splashScreen.workout = new WorkoutDay(response);
                    generateWorkoutSpinner.setVisibility(View.INVISIBLE);
                    WorkoutFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WorkoutFragment.this.updateUI();
                        }
                    });
                } catch (Exception ex) {
                    splashScreen.workout = null;
                }
            } else {
                splashScreen.workout = null;
            }
        }
    }

}
