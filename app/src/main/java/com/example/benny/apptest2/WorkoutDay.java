package com.example.benny.apptest2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class WorkoutDay {

    public final ArrayList<WorkoutItem> workoutItems = new ArrayList<WorkoutItem>();
    public final String primaryGroup;
    public final String secondaryGroup;
    public final Date date;
    public final String uuid;

    public JSONObject toJSON() throws JSONException {
        JSONObject res = new JSONObject();
        res.put("uuid", uuid);
        res.put("primaryGroup", primaryGroup);
        res.put("secondaryGroup", secondaryGroup);
        res.put("date", Util.dateToString(date));

        JSONArray itemsArray = new JSONArray();
        for(int i = 0; i < workoutItems.size(); i++) {
            itemsArray.put(workoutItems.get(i).toJSON());
        }
        res.put("items", itemsArray);

        return res;
    }
    public WorkoutDay(JSONObject json) throws JSONException {
            uuid = json.getString("uuid");
            primaryGroup = json.getString("primaryGroup");
            secondaryGroup = json.getString("secondaryGroup");
            date = Util.parseDate(json.getString("date"));

            JSONArray items = json.getJSONArray("items");
            for(int i = 0; i < items.length(); i++){
                workoutItems.add(new WorkoutItem(items.getJSONObject(i)));
            }
    }

    public static class WorkoutItem {
        public static final int TOOHARD = 1;
        public static final int JUSTRIGHT = 2;
        public static final int TOOEASY = 3;

        public final String uuid;
        public final Exercise exercise;
        public final ArrayList<WorkoutSet> sets = new ArrayList<>();

        public int difficulty;

        public JSONObject toJSON() throws JSONException {
            JSONObject res = new JSONObject();
            res.put("uuid", uuid);
            res.put("exercise", exercise.toJSON());
            res.put("difficulty", difficulty);

            JSONArray setsArray = new JSONArray();
            for(int i = 0; i < sets.size(); i++) {
                setsArray.put(sets.get(i).toJSON());
            }
            res.put("sets", setsArray);

            return res;
        }
        public WorkoutItem(JSONObject json) throws JSONException {
            uuid = json.getString("uuid");
            exercise = new Exercise(json.getJSONObject("exercise"));
            difficulty = json.getInt("difficulty");
            JSONArray setsArray = json.getJSONArray("sets");
            for(int i = 0; i < setsArray.length(); i++){
                sets.add(new WorkoutSet(setsArray.getJSONObject(i)));
            }
        }
    }

    public static class Exercise {
        public final String name;
        public final String video;
        public double user1RM;
        public final int recommendedCalibrationWeight;
        public int calibrationWeight;
        public int calibrationReps;
        public boolean hasBeenCalibrated;

        public JSONObject toJSON() throws JSONException {
            JSONObject res = new JSONObject();
            res.put("name", name);
            res.put("video", video);
            res.put("user1RM", user1RM);
            res.put("recommendedCalibrationWeight", recommendedCalibrationWeight);
            res.put("calibrationWeight", calibrationWeight);
            res.put("calibrationReps", calibrationReps);
            res.put("hasBeenCalibrated", hasBeenCalibrated);

            return res;
        }
        public Exercise(JSONObject json) throws JSONException {
            name = json.getString("name");
            video = json.getString("video");
            user1RM = json.getDouble("user1RM");
            recommendedCalibrationWeight = json.getInt("recommendedCalibrationWeight");
            calibrationWeight = json.getInt("calibrationWeight");
            calibrationReps = json.getInt("calibrationReps");
            hasBeenCalibrated = json.getBoolean("hasBeenCalibrated");
        }

    }

    public static class WorkoutSet {
        public final int reps;
        public final int percent1RM;
        public final int restTime;
        public Date restStarted; //used for the timer but not serialized
        public boolean doneWithRest;
        public int repsCompleted;

        public JSONObject toJSON() throws JSONException {
            JSONObject res = new JSONObject();
            res.put("reps", reps);
            res.put("percent1RM", percent1RM);
            res.put("restTime", restTime);
            res.put("doneWithRest", doneWithRest);
            res.put("repsCompleted", repsCompleted);

            return res;
        }
        public WorkoutSet(JSONObject json) throws JSONException {
            reps = json.getInt("reps");
            percent1RM = json.getInt("percent1RM");
            restTime = json.getInt("restTimeSeconds");
            doneWithRest = json.getBoolean("doneWithRest");
            repsCompleted = json.getInt("repsCompleted");
        }
    }
}
