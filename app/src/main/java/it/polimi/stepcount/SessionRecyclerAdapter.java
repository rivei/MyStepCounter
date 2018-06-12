package it.polimi.stepcount;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import it.polimi.stepcount.models.WalkingSession;

public class SessionRecyclerAdapter extends RecyclerView.Adapter<SessionRecyclerAdapter.WalkingSessionViewHolder> {

    private List<WalkingSession> walkingSessions;
    private Context context;

    public SessionRecyclerAdapter(List<WalkingSession> walkingSessions, Context context){
        this.walkingSessions = walkingSessions;
        this.context = context;
    }

    @NonNull
    @Override
    public WalkingSessionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View mView = LayoutInflater.from(context).inflate(R.layout.item_session, viewGroup, false);
        return new WalkingSessionViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull WalkingSessionViewHolder holder, int position) {
        WalkingSession walkingSession = walkingSessions.get(position);
        Date ssdate = new Date(walkingSession.getmStartTime());
        String str;
        str = "Session date: " + DateFormat.getDateInstance().format(ssdate);
        holder.dateView.setText(str);

        str = String.format("Duration: %1.1f Minutes", walkingSession.getmDuration()/60000f);
        holder.durationView.setText(str);

        str = "Step Count: " + String.valueOf(walkingSession.getmStepCount());
        holder.stepsView.setText(str);
    }

    @Override
    public int getItemCount() {
        return walkingSessions.size();
    }

    static class WalkingSessionViewHolder extends RecyclerView.ViewHolder {

        TextView dateView;
        TextView durationView;
        TextView stepsView;

        WalkingSessionViewHolder(View view) {
            super(view);

            dateView = (TextView) view.findViewById(R.id.text_date);
            durationView = (TextView) view.findViewById(R.id.text_duration);
            stepsView = (TextView) view.findViewById(R.id.text_steps);
        }
    }
}
