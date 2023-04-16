package aeonlabs.serenity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Locale;

import aeonlabs.serenity.Helper.TextViewCoffeeService;
import aeonlabs.serenity.managers.SerenityManager;
import aeonlabs.serenity.models.TrackDelegate;
// import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
// import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MeditationActivity extends Activity implements TrackDelegate {

    private ToggleButton playPauseButton;
    private TextViewCoffeeService timerTextView;
    private TextViewCoffeeService meditationNameTextView;
    private boolean isInMeditation = false;
    private final SerenityManager serenityManager = SerenityManager.singleton;
    private View clearClickBackgroundView;
    private View blackClickBackgroundView;

    @Override
    protected void attachBaseContext(Context newBase) {
        //super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        super.attachBaseContext((newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);
        serenityManager.setDelegate(this);
        connectView();
        /*
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/sf-pro-text-semibold.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        */

        if (!serenityManager.getInMeditation()) { //workaround for screen rotation
            Intent intent = getIntent();
            int gapAmount =  intent.getIntExtra("gapAmount", 0);
            serenityManager.playActiveTrackFromBeginning(gapAmount);
            serenityManager.setInMeditation(true);
        }
        if (!serenityManager.getTrackCompleted()) {
            isInMeditation = true;
            serenityManager.userStartedTrack();
        }
    }

    private void closeActivity() {
        serenityManager.clearCurrentTrack();
        finish();
    }

    @Override
    public void onBackPressed()
    {
        if (isInMeditation) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Meditation Underway");
            alertDialogBuilder
                    .setMessage("Would you like to stop the current session?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            closeActivity();
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            closeActivity();
        }
    }

    public void didTapBackButton(View v) {
        this.onBackPressed();
    }

    private void connectView() {
        playPauseButton = (ToggleButton) findViewById(R.id.playPauseButton);
        playPauseButton.setVisibility(View.VISIBLE);
        timerTextView = (TextViewCoffeeService) findViewById(R.id.timerTextView);
        meditationNameTextView = (TextViewCoffeeService) findViewById(R.id.meditationNameTextView);
        meditationNameTextView.setText(serenityManager.getActiveTrackLongName());
        meditationNameTextView.setShadowLayer(2.0f, 2.0f, 2.0f, R.color.colorShadow);
        clearClickBackgroundView = findViewById(R.id.clearClickBackground);
        blackClickBackgroundView = findViewById(R.id.blackClickBackground);

        clearClickBackgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blackClickBackgroundView.setVisibility(View.VISIBLE);
            }
        });

        blackClickBackgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blackClickBackgroundView.setVisibility(View.INVISIBLE);
            }
        });

        blackClickBackgroundView.setZ(50);
        playPauseButton.setZ(20);

    }

    public void didTapPlayPause(View v) {
        serenityManager.pauseOrResume();
    }

    @Override
    public void trackTimeRemainingUpdated(int timeRemaining) {
        serenityManager.incrementTotalSecondsInMeditation();
        String timeRemainingString = String.format(Locale.getDefault(), "%01d:%02d", timeRemaining / 60, ((timeRemaining % 3600) % 60));
        timerTextView.setText(timeRemainingString);
    }

    @Override
    public void trackEnded() {
        serenityManager.userCompletedTrack();
        playPauseButton.setVisibility(View.INVISIBLE);
        isInMeditation = false;
        serenityManager.setTrackCompleted(true);
        closeActivity();
    }


}
