package com.example.benny.apptest2;

import android.app.DownloadManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Date;


public class ExerciseFragment extends Fragment {
    WorkoutDay.WorkoutItem item;

    View parent;

    ConstraintLayout exerciseViewContainer;
    Button doneButton;
    TextView exerciseName;
    TextView exerciseWeight;
    TextView exerciseReps;

    ConstraintLayout restViewContainer;
    Button uprepsButton;
    Button downrepsButton;
    Button nextSetButton;
    TextView restViewReps;
    TextView restViewTime;
    ProgressBar restViewSpinner;

    ConstraintLayout calibrationViewContainer;
    Button calibrationViewUpweightButton;
    Button calibrationViewDownweightButton;
    Button calibrationViewStartButton;
    TextView calibrationViewWeight;
    TextView calibrationViewExercise;


    ConstraintLayout completedViewContainer;
    Button tooHardButton;
    Button tooEasyButton;
    Button justRightButton;

    TimerRunnable timer;

    private void setCorrectView() {
        if(!calibrationComplete()) {
            if(!hasBeenCalibrated()) {
                switchToCalibrationView();
                renderCurrentCalibration();
            } else {
                if(!calibrationRepsComplete()) {
                    switchToExerciseView();
                    renderCurrentExercise();
                } else {
                    switchToRestView();
                    renderCurrentRest();
                }
            }
        } else {
            WorkoutDay.WorkoutSet currentSet = getCurrentSet();
            if(currentSet == null) {
                switchToCompletedView();
                renderCurrentComplete();
            } else {
                if(currentSet.restStarted != null) {
                    switchToRestView();
                    renderCurrentRest();
                } else {
                    switchToExerciseView();
                    renderCurrentExercise();
                }
            }
        }
    }

    private boolean calibrationComplete() {
        return !(item.onerepmax <= 0);
    }
    private boolean hasBeenCalibrated() {
        return item.exercise.hasBeenCalibrated;
    }
    private boolean calibrationRepsComplete() {
        return !(item.exercise.calibrationReps < 0);
    }

    //returns null if all sets are completed
    WorkoutDay.WorkoutSet getCurrentSet() {
        for(int i = 0; i < item.sets.size(); i++) {
            WorkoutDay.WorkoutSet set = item.sets.get(i);
            if(!set.doneWithRest) {
                return set;
            }
        }
        return null;
    }

    private void renderCurrentCalibration() {
        calibrationViewExercise.setText(item.exercise.name);
        calibrationViewWeight.setText(item.exercise.calibrationWeight + "");
    }
    private void renderCurrentExercise() {
        if(!calibrationComplete()) {
            exerciseName.setText(item.exercise.name);
            exerciseWeight.setText(item.exercise.calibrationWeight + " lbs");
            exerciseReps.setText("as many full reps as possible");
        } else {
            WorkoutDay.WorkoutSet currentSet = getCurrentSet();
            if(currentSet == null) {
                exerciseName.setText("Should've Switched to Completed View");
                exerciseWeight.setText("ER lbs");
                exerciseReps.setText("ER reps");
            } else {
                exerciseName.setText(item.exercise.name);
                exerciseWeight.setText(calculateWeightForCurrentSet() + " lbs");
                exerciseReps.setText(getCurrentSet().reps + " reps");
            }
        }
    }
    private void renderCurrentRest() {
        if(!calibrationComplete()){
            restViewTime.setText("60");
            restViewReps.setText(item.exercise.calibrationReps + "");
        }else{
            WorkoutDay.WorkoutSet currentSet = getCurrentSet();
            restViewReps.setText(currentSet.repsCompleted + "");
            if(currentSet.restStarted != null) {
                int timeLeft = currentSet.restTime - (int) ((new Date()).getTime() - currentSet.restStarted.getTime()) / 1000;
                if (timeLeft < 0) timeLeft = 0;
                restViewTime.setText(timeLeft + "");
            }
        }
    }
    private void renderCurrentComplete(){
        tooEasyButton.setBackgroundColor((item.difficulty == 3) ? (Color.parseColor("#88ff88")) : (Color.parseColor("#888888")));
        justRightButton.setBackgroundColor((item.difficulty == 2) ? (Color.parseColor("#88ff88")) : (Color.parseColor("#888888")));
        tooHardButton.setBackgroundColor((item.difficulty == 1) ? (Color.parseColor("#88ff88")) : (Color.parseColor("#888888")));
    }

    private void switchToCalibrationView() {
        calibrationViewContainer.setVisibility(View.VISIBLE);
        exerciseViewContainer.setVisibility(View.GONE);
        restViewContainer.setVisibility(View.GONE);
        completedViewContainer.setVisibility(View.GONE);
    }
    private void switchToExerciseView() {
        calibrationViewContainer.setVisibility(View.GONE);
        exerciseViewContainer.setVisibility(View.VISIBLE);
        restViewContainer.setVisibility(View.GONE);
        completedViewContainer.setVisibility(View.GONE);
    }
    private void switchToRestView() {
        calibrationViewContainer.setVisibility(View.GONE);
        exerciseViewContainer.setVisibility(View.GONE);
        restViewContainer.setVisibility(View.VISIBLE);
        completedViewContainer.setVisibility(View.GONE);
    }
    private void switchToCompletedView() {
        calibrationViewContainer.setVisibility(View.GONE);
        exerciseViewContainer.setVisibility(View.GONE);
        restViewContainer.setVisibility(View.GONE);
        completedViewContainer.setVisibility(View.VISIBLE);
    }

    private int calculateWeightForCurrentSet() {
        return Util.roundNearestFive((int)(getCurrentSet().percent1RM * item.onerepmax * 0.01));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setupCalibrationView(){
        calibrationViewContainer = parent.findViewById(R.id.calibration_view_container);

        calibrationViewUpweightButton = parent.findViewById(R.id.calibration_view_upweight);
        calibrationViewUpweightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.exercise.calibrationWeight += 5;
                renderCurrentCalibration();
            }
        });

        calibrationViewDownweightButton = parent.findViewById(R.id.calibration_view_downweight);
        calibrationViewDownweightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.exercise.calibrationWeight > 5){
                    item.exercise.calibrationWeight -= 5;
                    renderCurrentCalibration();
                }
            }
        });

        calibrationViewStartButton = parent.findViewById(R.id.calibration_view_start);
        calibrationViewStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.exercise.hasBeenCalibrated = true;
                setCorrectView();
            }
        });

        calibrationViewWeight = parent.findViewById(R.id.calibration_view_weight);
        calibrationViewExercise = parent.findViewById(R.id.calibtation_view_exercise);
    }
    public void setupExerciseView() {
        exerciseViewContainer = parent.findViewById(R.id.exercise_view_container);
        doneButton = parent.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!calibrationComplete()) {
                    item.exercise.calibrationReps = 10;
                    setCorrectView();
                } else {
                    WorkoutDay.WorkoutSet currentSet = getCurrentSet();
                    currentSet.repsCompleted = currentSet.reps;
                    currentSet.restStarted = new Date();
                    setCorrectView();
                }
            }
        });
        exerciseName = parent.findViewById(R.id.exercise_view_name);
        exerciseWeight = parent.findViewById(R.id.exercise_view_weight);
        exerciseReps = parent.findViewById(R.id.exercise_view_reps);
    }
    public void setupRestView() {
        restViewContainer = parent.findViewById(R.id.rest_view_container);
        nextSetButton = parent.findViewById(R.id.rest_view_nextset);
        nextSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!calibrationComplete()) {
                    RequestData data = new RequestData();
                    data.url = Util.calibrationConnection;
                    JSONObject obj = new JSONObject();
                    Pair<String, String> credentials = Util.getUserCredentials(getContext());
                    try {
                        obj.put("email", credentials.first);
                        obj.put("passwordEmailHash", credentials.second);
                        obj.put("exercise", item.exercise.name);
                        obj.put("reps", item.exercise.calibrationReps);
                        obj.put("weight", item.exercise.calibrationWeight);
                    } catch (Exception ex) { }
                    data.message = obj.toString();

                    CalibrationPoster poster = new CalibrationPoster();
                    poster.owner = ExerciseFragment.this;
                    poster.execute(data);

                    ExerciseFragment.this.restViewSpinner.setVisibility(View.VISIBLE);
                }else{
                    getCurrentSet().doneWithRest = true;
                    setCorrectView();
                }
            }
        });
        restViewReps = parent.findViewById(R.id.rest_view_reps);
        restViewTime = parent.findViewById(R.id.rest_view_time);
        uprepsButton = parent.findViewById(R.id.rest_view_upreps);
        uprepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!calibrationComplete()){
                    if(item.exercise.calibrationReps != 420) item.exercise.calibrationReps++;
                } else{
                    if(getCurrentSet().repsCompleted != 420) getCurrentSet().repsCompleted++;
                }
                renderCurrentRest();
            }
        });
        downrepsButton = parent.findViewById(R.id.rest_view_downreps);
        downrepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!calibrationComplete()){
                    if(item.exercise.calibrationReps != 0) item.exercise.calibrationReps--;
                }else{
                    if(getCurrentSet().repsCompleted != 0) getCurrentSet().repsCompleted--;
                }
                renderCurrentRest();
            }
        });
        restViewSpinner = parent.findViewById(R.id.rest_view_spinner);

    }
    public void setupCompletedView() {
        completedViewContainer = parent.findViewById(R.id.completed_view_container);
        tooHardButton = parent.findViewById(R.id.rest_view_toohard);
        tooHardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.difficulty = WorkoutDay.WorkoutItem.TOOHARD;
                sendFeedback();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        renderCurrentComplete();
                    }
                });
            }
        });
        justRightButton = parent.findViewById(R.id.rest_view_justright);
        justRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.difficulty = WorkoutDay.WorkoutItem.JUSTRIGHT;
                sendFeedback();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        renderCurrentComplete();
                    }
                });
            }
        });
        tooEasyButton = parent.findViewById(R.id.rest_view_tooeasy);
        tooEasyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.difficulty = WorkoutDay.WorkoutItem.TOOEASY;
                sendFeedback();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        renderCurrentComplete();
                    }
                });
            }
        });
    }

    private void sendFeedback() {
        RequestData data = new RequestData();
        data.url = Util.feedbackConnection;
        try {
            JSONObject response = new JSONObject();
            response.put("email", Util.getUserCredentials(getContext()).first);
            response.put("passwordEmailHash", Util.getUserCredentials(getContext()).second);
            response.put("feedback", item.toJSON());
            data.message = response.toString();
        } catch (Exception ex) { }
        POSTer p = new FeedbackPoster();
        p.execute(data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_exercise, container, false);

        setupExerciseView();
        setupRestView();
        setupCompletedView();
        setupCalibrationView();

        setCorrectView();

        return parent;
    }

    @Override
    public void onStart() {
        super.onStart();
        //timer = new TimerRunnable(this);
        //AsyncTask.execute(timer);
    }
    @Override
    public void onStop() {
        super.onStop();
        /*timer.running = false;
        while(!timer.finished) {
            try {
                Thread.sleep(25);
            } catch (Exception ignored) {
            }
        }
        timer = null;*/
    }

    public static ExerciseFragment newInstance(WorkoutDay.WorkoutItem item) {
        ExerciseFragment fragment = new ExerciseFragment();
        fragment.item = item;
        return fragment;
    }
    public ExerciseFragment() {
    }

    public static class TimerRunnable implements Runnable {
        ExerciseFragment owner;
        boolean running = true;
        boolean finished = false;

        @Override
        public void run() {
            Log.e("runnable", "started");
            while(running) {
                try {
                    Thread.sleep(100);
                } catch (Exception ignored) {
                }
                if(owner.getActivity() != null) {
                    owner.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            owner.renderCurrentRest();
                        }
                    });
                } else {
                    running = false;
                }
            }
            finished = true;
            Log.e("runnable", "finished");
        }

        TimerRunnable(ExerciseFragment owner) {
            this.owner = owner;
        }
    }

    public class FeedbackPoster extends POSTer {

        @Override
        protected void onFinish() {

        }
    }

    public class CalibrationPoster extends POSTer {
        public ExerciseFragment owner;

        @Override
        protected  void onFinish() {
            owner.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        owner.item.onerepmax = (float)response.getDouble("oneRepMax");
                    } catch (Exception ex) { }
                    owner.restViewSpinner.setVisibility(View.INVISIBLE);
                    owner.setCorrectView();
                }
            });
        }
    }
}
