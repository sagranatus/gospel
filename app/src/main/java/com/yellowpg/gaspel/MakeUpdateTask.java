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

import java.util.List;

/**
 * Created by Saea on 2017-08-25.
 */

public class MakeUpdateTask extends AsyncTask<Void, Void, List<String>> {
    private com.google.api.services.calendar.Calendar mService = null;
    private Exception mLastError = null;
    String date, date_only, contents, summ;
    MakeUpdateTask(GoogleAccountCredential credential, String date, String date_only, String contents,  String summ) {
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

// Retrieve the event from the API
            Event event = mService.events().get("primary", date_only).execute();

// Make a change
            event.setSummary(summ);
            event.setDescription(contents);

// Update the event
            Event updatedEvent = mService.events().update("primary", event.getId(), event).execute();

            System.out.println(updatedEvent.getUpdated());

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