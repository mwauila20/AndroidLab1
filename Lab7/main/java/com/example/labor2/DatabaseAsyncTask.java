package com.example.labor2;
import android.os.AsyncTask;

public class DatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
    private Runnable doInBackgroundTask;

    public DatabaseAsyncTask(Runnable doInBackgroundTask) {
        this.doInBackgroundTask = doInBackgroundTask;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        doInBackgroundTask.run();
        return null;
    }
}
