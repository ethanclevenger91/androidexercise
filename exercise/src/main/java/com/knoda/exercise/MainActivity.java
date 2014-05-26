package com.knoda.exercise;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class MainActivity extends Activity {

    ArrayList<Prediction> predictionList = new ArrayList<Prediction>();
    ListView predictionListView;
    PredictionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PlaceholderFragment mFragment = new PlaceholderFragment();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            predictionListView = (ListView) rootView.findViewById(R.id.listView);
            adapter = new PredictionAdapter(this.getActivity(), predictionList);
            predictionListView.setAdapter(adapter);
            new GetKnodaRecentJSON().execute();
            return rootView;
        }

        private void addPrediction(String uN, String p, String i, String cA, String eA, int a, int d, int c) {
            Prediction nPrediction = new Prediction(uN, p, i, cA, eA, a, d, c);
            predictionList.add(nPrediction);
        }
        public class GetKnodaRecentJSON extends AsyncTask<Void, Void, ArrayList<Prediction>> {
            @Override
            protected ArrayList<Prediction> doInBackground(Void... voids) {
                ArrayList<Prediction> mPredictions = new ArrayList<Prediction>();
                try {
                    URL knodaURL = new URL("http://api.knoda.com/api/predictions.json?recent=true&auth_token=Q4tgFunM22ubWqP4p4zz");
                    URLConnection kc = knodaURL.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            kc.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        JSONObject mObj = new JSONObject(line);
                        JSONArray array = mObj.getJSONArray("predictions");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jo = (JSONObject) array.get(i);
                            Prediction currPrediction = Prediction.fromJson(jo);
                            mPredictions.add(currPrediction);
                            Log.v("username", currPrediction.getUserName());
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return mPredictions;
            }
            @Override
            protected void onPostExecute(ArrayList<Prediction> result) {
                adapter.addAll(result);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class PredictionAdapter extends ArrayAdapter<Prediction> {
        public PredictionAdapter(Context context, ArrayList<Prediction> predictionList) {
            super (context, R.layout.fragment_main, predictionList);
        }
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = getLayoutInflater().inflate(R.layout.prediction_list_item, parent, false);
            }
            Prediction mPrediction = predictionList.get(position);
            TextView user = (TextView) view.findViewById(R.id.textView);
            TextView prediction = (TextView) view.findViewById(R.id.textView2);
            TextView info = (TextView) view.findViewById(R.id.textView3);
            TextView comments = (TextView) view.findViewById(R.id.textView4);
            ImageView userImg = (ImageView) view.findViewById(R.id.imageView2);
            user.setText(mPrediction.getUserName());
            prediction.setText(mPrediction.getPrediction());
            double agrees = mPrediction.getAgrees();
            double disagrees = mPrediction.getDisagrees();
            double totVotes = agrees+disagrees;
            DecimalFormat twoDec = new DecimalFormat("#.00");
            double percAgreeD = Double.parseDouble(twoDec.format(agrees / totVotes));
            percAgreeD = percAgreeD*100;
            int percAgreeI = (int)percAgreeD;
            String percAgree = String.valueOf(percAgreeI);
            DecimalFormat roundHalf = new DecimalFormat("#");
            roundHalf.setRoundingMode(RoundingMode.HALF_UP);
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat dateFor = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFor.setTimeZone(tz);
            String createdAt = mPrediction.getCreatedAt();
            String expiresAt = mPrediction.getExpiresAt();
            Date createdAtDate = new Date(), expiresAtDate = new Date();
            try {
                createdAtDate = dateFor.parse(createdAt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                expiresAtDate = dateFor.parse(expiresAt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date now = new Date();
            double madeAgoD = (((now.getTime() - createdAtDate.getTime()) / (1000 * 60 * 60 * 24)));
            double closesInD = (((expiresAtDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)));
            boolean madeAgoHours = false;
            boolean closesInHours = false;
            if(madeAgoD < 1) {
                madeAgoD = (((now.getTime() - createdAtDate.getTime()) / (1000 * 60 * 60)));
                madeAgoHours = true;
            }
            if(closesInD < 1) {
                closesInD = (((expiresAtDate.getTime() - now.getTime()) / (1000 * 60 * 60)));
                closesInHours = true;
            }
            String madeAgo = roundHalf.format(madeAgoD);
            String closesIn = roundHalf.format(closesInD);
            String timeInfo = "closes "+closesIn;
            if(closesInHours) timeInfo +="h";
            else timeInfo +="d";
            timeInfo += " | made "+madeAgo;
            if(madeAgoHours) timeInfo+="h";
            else timeInfo +="d";
            timeInfo += " ago | "+percAgree+"% agree | ";
            info.setText(timeInfo);
            comments.setText(Integer.toString(mPrediction.getCommentCount()));
            userImg.setTag(mPrediction.getImgUrl());
            new GetBitmapFromURL().execute(userImg);
            return view;
        }
    }
    class GetBitmapFromURL extends AsyncTask<ImageView, Void, Bitmap> {

        private Exception exception;

        ImageView mImageView = null;
        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.mImageView = imageViews[0];
            Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_content_placeholder);
            try {
                URL url = new URL((String)mImageView.getTag());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                mBitmap = BitmapFactory.decodeStream(input);
                Log.e("Bitmap", "returned");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Exception", e.getMessage());
                return null;
            }
            return mBitmap;
        }

        protected void onPostExecute(Bitmap result) {
            mImageView.setImageBitmap(result);
        }
    }


}

