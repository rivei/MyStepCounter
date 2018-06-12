package it.polimi.stepcount.models;

public class WalkingSession {

    private long mStartTime;
    private long mEndTime;
    private long mStepCount;
    private long mStepDetect;
    private long mDistance;
    private double mAverageSpeed;
    private long mDuration;

    public WalkingSession() {
    }

    //Constructor for reading data base
    public WalkingSession(long mStartTime, long mEndTime, long mStepCount, long mStepDetect, long mDistance, double mAverageSpeed, long mDuration) {
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mStepCount = mStepCount;
        this.mStepDetect = mStepDetect;
        this.mDistance = mDistance;
        this.mAverageSpeed = mAverageSpeed;
        this.mDuration = mDuration;
    }

    public long getmStartTime() {
        return mStartTime;
    }

    public void setmStartTime(long mStartTime) {
        this.mStartTime = mStartTime;
    }

    public long getmEndTime() {
        return mEndTime;
    }

    public void setmEndTime(long mEndTime) {
        this.mEndTime = mEndTime;
    }

    public long getmStepCount() {
        return mStepCount;
    }

    public void setmStepCount(long mStepCount) {
        this.mStepCount = mStepCount;
    }

    public long getmDistance() {
        return mDistance;
    }

    public void setmDistance(long mDistance) {
        this.mDistance = mDistance;
    }

    public double getmAverageSpeed() {
        return mAverageSpeed;
    }

    public void setmAverageSpeed(double mAverageSpeed) {
        this.mAverageSpeed = mAverageSpeed;
    }

    public long getmDuration() {
        return mDuration;
    }

    public void setmDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    public long getmStepDetect() {
        return mStepDetect;
    }

    public void setmStepDetect(long mStepDetect) {
        this.mStepDetect = mStepDetect;
    }

    @Override
    public String toString(){
        return "Start Time: "      + mStartTime    +
                ", End Time: "       + mEndTime        +
                ", Step Count: "   + mStepCount    +
                ", Distance: "      + mDistance       +
                ", Average Speed: "  + mAverageSpeed +
                ", Duration:"        + mDuration;
    }
}
