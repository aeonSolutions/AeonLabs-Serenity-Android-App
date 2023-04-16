package aeonlabs.serenity.models;

/**
 * Created by aerozero on 12/23/17.
 */

public interface TrackDelegate {
    void trackTimeRemainingUpdated(int timeRemaining);
    void trackEnded();
}
