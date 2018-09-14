package com.example.benny.apptest2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Pair;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

/**
 * Created by Steve Buscemi's left nut on 3/22/2018.
 */

public class Util {
    public static final String baseConnection = "http://vps186949.vps.ovh.ca:8080";
    //public static final String baseConnection = "http://10.0.2.2:8080";
    public static final String registerConnection = baseConnection + "/register";
    public static final String loginConnection = baseConnection + "/login";
    public static final String exerciseConnection = baseConnection + "/exercise-info";
    public static final String feedbackConnection = baseConnection + "/exercise-feedback";
    public static final String calibrationConnection = baseConnection + "/exercise-calibration";

    public static boolean writeToFile(String path, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(path, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static String readFromFile(String path, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(path);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            return "";
        }

        return ret;
    }




    private static String userEmail = null;
    private static String userPasswordEmailHash = null;
    private static final String credentialsFilepath = "login.txt";

    public static void deleteUserCredentials(Context ctx) {
        try {
            userEmail = null;
            userPasswordEmailHash = null;
            ctx.deleteFile(credentialsFilepath);
        } catch (Exception ignored) { }
    }
    public static void setUserCredentials(String userEmail, String userPasswordEmailHash, Context ctx) {
        Util.userEmail = userEmail;
        Util.userPasswordEmailHash = userPasswordEmailHash;
        writeToFile(credentialsFilepath, userEmail + " " + userPasswordEmailHash, ctx);
    }
    public static Pair<String, String> getUserCredentials(Context ctx) {
        if(userEmail == null || userPasswordEmailHash == null) {
            boolean good = true;
            String loadedCredentials = Util.readFromFile(credentialsFilepath, ctx);
            if(!loadedCredentials.isEmpty()) {
                String[] parts = new String[0];
                for(int i = 0; i < loadedCredentials.length(); i++) {
                    if(loadedCredentials.charAt(i) == ' ') {
                        parts = new String[2];
                        parts[0] = loadedCredentials.substring(0, i);
                        parts[1] = loadedCredentials.substring(i + 1, loadedCredentials.length());
                        break;
                    }
                }
                if(parts.length == 2) {
                    Util.userEmail = parts[0];
                    Util.userPasswordEmailHash = parts[1];
                } else {
                    good = false;
                }
            } else {
                good = false;
            }
            if(!good) {
                userEmail = null;
                userPasswordEmailHash = null;
                return null;
            }
        }
        return new Pair(userEmail, userPasswordEmailHash);
    }

    //public static String getCurrentUserEmail() {
    //
    //}
    //public static String getCurrentUserPasswordEmailHash() {
    //
    //}

    //NO backslashes
    public static String JSONToString(JSONObject obj) {
        return  obj.toString().replaceAll("\\\\", "");
    }

    private static MessageDigest hasher = null;
    public static String hash(String val) {
        try {
            if(hasher == null) hasher = MessageDigest.getInstance("SHA-256");
            try {
                hasher.reset();
                byte[] rawBytes = val.getBytes("UTF-8");
                hasher.update(rawBytes);
                return Base64.encodeToString(hasher.digest(), Base64.NO_WRAP);
            } catch(UnsupportedEncodingException ex) {
                return "can't encode";
            }
        } catch (NoSuchAlgorithmException ex) {
            return "can't hash";
        }
    }
    private static Random rand = null;
    public static String randhomHash() {
        if(rand == null) rand = new Random(System.currentTimeMillis());
        String res = "";
        for(int i = 0; i < 64; i++) {
            res = res + rand.nextInt(9);
        }
        return hash(res);
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void accountResetEmail() {

    }

    public static int roundNearestFive(int weight) {
        return ((weight + 4) / 5) * 5;
    }

    public static Date parseDate(String date){
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5,7)) - 1;
        int day = Integer.parseInt(date.substring(8,10));

        Date res = new Date(year, month, day);
        return res;
    }
    public static String dateToString(Date date){
        return date.getYear() + "-" + String.format("%02d", date.getMonth() + 1) + "-" + String.format("%02d", date.getDay());
    }

    public static void finishAll(Activity child) {
        while(true) {
            Activity next = child.getParent();
            child.finish();
            if(next == null) {
                break;
            }
            child = next;
        }
    }

    public static <FromT extends Activity> void pageSwap(FromT instance, Class next){
        Intent intent = new Intent(instance.getBaseContext(), next);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        instance.startActivity(intent);
        instance.finish();
    }

    public static double calculate1RM(double weight, int repsCompleted){
        if(repsCompleted == 0) repsCompleted++;
        return ((1 + (repsCompleted/30.0))*weight);
    }

    public static int roundToNearest5(int value) {
        return ((value + 4) / 5) * 5;
    }
}
