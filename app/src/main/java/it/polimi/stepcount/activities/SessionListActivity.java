package it.polimi.stepcount.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import it.polimi.stepcount.Dao.DaoWalkingSession;
import it.polimi.stepcount.R;
import it.polimi.stepcount.SessionRecyclerAdapter;
import it.polimi.stepcount.models.WalkingSession;

public class SessionListActivity extends AppCompatActivity {
    private final String TAG = SessionListActivity.class.getSimpleName();
    private final SessionListActivity self = this;

    private List<WalkingSession> walkingSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_list);

        walkingSessions = getRawData(self);

        RecyclerView sessionsListView = (RecyclerView) findViewById(R.id.sessionsList);

        SessionRecyclerAdapter sessionRecyclerAdapter = new SessionRecyclerAdapter(walkingSessions, this);
        sessionsListView.setAdapter(sessionRecyclerAdapter);
        sessionsListView.setLayoutManager(new LinearLayoutManager(this));

    }

    public List<WalkingSession> getRawData(Context context){
        return new DaoWalkingSession(context).retrieveAllSessions();
    }
}
