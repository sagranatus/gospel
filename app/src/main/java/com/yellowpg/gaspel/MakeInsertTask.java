package com.yellowpg.gaspel;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.List;

/**
 * Created by Saea on 2017-08-25.
 */

public class MakeInsertTask extends AsyncTask<Void, Void, List<String>> {
    private com.google.api.services.calendar.Calendar mService = null;
    private Exception mLastError = null;
    String date, date_only, contents, summ;
    MakeInsertTask(GoogleAccountCredential credential, String date, String date_only, String contents, String summ) {
        this.date = date;
        this.date_only = date_only;
        this.contents = contents;
        this.summ = summ;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
    }


    @Override
    protected List<String> doInBackground(Void... params) {


        DateTime thistime = new DateTime(date+"T00:00:00-00:00");

        try {

            // exp : 이 아래는 데이터 삽입부분
            Event event = new Event()
                    .setId(date_only)
                    .setSummary(summ)
                    .setDescription(contents);
            // DateTime startDateTime = new DateTime("2017-08-15T09:00:00-07:00");
            EventDateTime start = new EventDateTime()
                    .setDateTime(thistime)
                    .setTimeZone("Asia/Seoul");
            event.setStart(start);

            //  DateTime endDateTime = new DateTime("2017-08-15T09:00:00-07:00");
            EventDateTime end = new EventDateTime()
                    .setDateTime(thistime)
                    .setTimeZone("Asia/Seoul");
            event.setEnd(end);

            //String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=3"};
            //event.setRecurrence(Arrays.asList(recurrence));


            String calendarId = "primary";
            event = mService.events().insert(calendarId, event).execute();
            System.out.printf("Event created: %s\n", event.getHtmlLink());

        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }


        return null;
    }
    @Override
    protected void onPreExecute() {
        //	mOutputText.setText("");
        //	mProgress.show();
    }

    @Override
    protected void onPostExecute(List<String> output) {
        //mProgress.hide();
    }

    @Override
    protected void onCancelled() {
        //mProgress.hide();
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
            } else if (mLastError instanceof UserRecoverableAuthIOException) {

            } else {
                //mOutputText.setText("The following error occurred:\n"
                //		+ mLastError.getMessage());
            }
        } else {
            //mOutputText.setText("Request cancelled.");
        }
    }
}
