package com.knoda.exercise;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Prediction {
	private String userName;
	private String prediction;
	private int agree;
	private int disagree;
	private int commentCount;
    private String imgUrl;
    private String createdAt;
    private String expiresAt;

	public Prediction() {
	}
	public Prediction(String uN, String p, String i, String cA, String eA, int a, int d, int c) {
		userName = uN;
		prediction = p;
        createdAt = cA;
        expiresAt = eA;
		agree = a;
		disagree = d;
		commentCount = c;
        imgUrl = i;
	}
    public static Prediction fromJson(JSONObject jsonObject) {
        Prediction mPrediction = new Prediction();
        // Deserialize json into object fields
        try {
            mPrediction = new Prediction(jsonObject.getString("username"),
                    jsonObject.getString("body"),
                    jsonObject.getJSONObject("user_avatar").getString("small"),
                    jsonObject.getString("created_at"),
                    jsonObject.getString("expires_at"),
                    jsonObject.getInt("agreed_count"),
                    jsonObject.getInt("disagreed_count"),
                    jsonObject.getInt("comment_count"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return mPrediction;
    }
    public static ArrayList<Prediction> fromJson(JSONArray jsonArray) {
        ArrayList<Prediction> predictionList = new ArrayList<Prediction>(jsonArray.length());
        // Process each result in json array, decode and convert to business object
        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject predictionJson = null;
            try {
                predictionJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Prediction prediction = Prediction.fromJson(predictionJson);
            if (prediction != null) {
                predictionList.add(prediction);
            }
        }

        return predictionList;
    }
    public String getUserName() {
        return this.userName;
    }
    public String getPrediction() {
        return this.prediction;
    }
    public String getImgUrl() {
        return this.imgUrl;
    }
    public int getAgrees() {
        return this.agree;
    }
    public int getDisagrees() {
        return this.disagree;
    }
    public int getCommentCount() {
        return this.commentCount;
    }
    public String getCreatedAt() {
        return this.createdAt;
    }
    public String getExpiresAt() {
        return this.expiresAt;
    }
}
