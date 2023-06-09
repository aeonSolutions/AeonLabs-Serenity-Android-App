package aeonlabs.serenity.managers;

import android.content.Context;
import android.content.SharedPreferences;

import aeonlabs.serenity.models.Track;
import aeonlabs.serenity.models.TrackDelegate;
import aeonlabs.serenity.models.TrackTemplate;
import aeonlabs.serenity.models.User;

public class SerenityManager {

    public static SerenityManager singleton = new SerenityManager();
    private SharedPreferences settings;
    private final TrackTemplateFactory trackTemplateFactory = TrackTemplateFactory.singleton;
    private final User user;
    private Track activeTrack;
    private int activeTrackLevel;
    private Boolean isInMeditation;
    private Boolean isTrackCompleted;

    private SerenityManager() {
        this.user = new User();
    }

    public void initTrackAtLevel(int trackLevel, Context context) {
        this.clearCurrentTrack();
        this.activeTrackLevel = trackLevel;
        TrackTemplate trackTemplate = trackTemplateFactory.getTrackTemplate(trackLevel);
        this.activeTrack = new Track(trackTemplate, context);
    }

    public boolean isMultiPart() {
        return activeTrack.isMultiPart();
    }

    public void playActiveTrackFromBeginning(int gapDuration) {
        this.activeTrack.setGapDuration(gapDuration);
        this.activeTrack.playFromBeginning();
    }

    public void pauseOrResume() {
        if (activeTrack != null) {
            activeTrack.pauseOrResume();
        }
    }

    public void clearCurrentTrack() {
        if (activeTrack != null) {
            activeTrack.stop();
            activeTrack = null;
            activeTrackLevel = 0;
        }
        isInMeditation = false;
        isTrackCompleted = false;
    }

    private void setCompletedTrack() {
        if (this.activeTrackLevel > this.user.getCompletedTrackLevel()) {
            this.user.setCompletedTrackLevel(this.activeTrackLevel);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("savedCompletedLevel", this.activeTrackLevel);
            editor.apply();
        }
    }

    public void userCompletedTrack() {
        this.setCompletedTrack();
    }

    public void userStartedTrack() {
        this.setCompletedTrack();
    }

    public void setDefaultDurationMinutes(int durationMinutes) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("savedCustomMeditationDurationMinutes", durationMinutes);
        editor.apply();
        this.user.setCustomMeditationDurationMinutes(durationMinutes);
    }

    public int getDefaultDurationMinutes() {
        return user.getCustomMeditationDurationMinutes();
    }

    public int getMinimumDuration() {
        if(activeTrack == null)
            return 0;
        return activeTrack.getMinimumDuration();
    }

    public int getUserCompletedTrackLevel(){
        return user.getCompletedTrackLevel();
    }

    public int getUserTotalSecondsInMeditation(){
        return user.getTotalSecondsInMeditation();
    }

    public void setSettings(SharedPreferences settings) {
        this.settings = settings;
        int savedCompletedLevel = settings.getInt("savedCompletedLevel", 0);
        this.user.setCompletedTrackLevel(savedCompletedLevel);

        int savedCustomMeditationDurationMinutes = settings.getInt("savedCustomMeditationDurationMinutes", 0);
        this.user.setCustomMeditationDurationMinutes(savedCustomMeditationDurationMinutes);
        int totalSecondsInMeditation = settings.getInt("totalSecondsInMeditation", 0);
        this.user.setTotalSecondsInMeditation(totalSecondsInMeditation);
    }

    public void incrementTotalSecondsInMeditation() {
        this.user.setTotalSecondsInMeditation(this.user.getTotalSecondsInMeditation() + 1);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("totalSecondsInMeditation", this.user.getTotalSecondsInMeditation());
        editor.apply();
    }

    public void setDelegate(TrackDelegate delegate) {
        if (this.activeTrack != null) {
            this.activeTrack.setDelegate(delegate);
        }
    }

    public String getActiveTrackName() {
        if (activeTrack == null) {
            return null;
        }
        return activeTrack.getName();
    }

    public String getActiveTrackLongName() {
        if (activeTrack == null) {
            return null;
        }
        return activeTrack.getLongName();
    }

    public Boolean getInMeditation() {
        return isInMeditation;
    }

    public void setInMeditation(Boolean inMeditation) {
        isInMeditation = inMeditation;
    }

    public Boolean getTrackCompleted() {
        return isTrackCompleted;
    }

    public void setTrackCompleted(Boolean trackCompleted) {
        isTrackCompleted = trackCompleted;
    }
}