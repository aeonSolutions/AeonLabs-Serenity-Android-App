package aeonlabs.serenity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import aeonlabs.serenity.Helper.ButtonCoffeeService;
import aeonlabs.serenity.Helper.TextViewCoffeeService;
import aeonlabs.serenity.managers.SerenityManager;
import aeonlabs.serenity.managers.TrackTemplateFactory;
import aeonlabs.serenity.models.TrackTemplate;
//import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
//import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "VipassanaPrefs";
    public SerenityManager serenityManager = SerenityManager.singleton;
    public TrackTemplateFactory trackTemplateFactory = TrackTemplateFactory.singleton;

    private ImageButton timerButton;
    private TextViewCoffeeService infoButton;
    private ButtonCoffeeService meditationTotalTimeTextView;
    private Timer rotateBackground;
    private ImageView backgoundImage;

    public  void didTapTimerButton(View v) {
        presentAlerts(0);
    }

    public  void didTapMeditationButton(View v) {
        int trackLevel = (int)v.getTag();
        presentAlerts(trackLevel);
    }

    public void didTapInfoButton(View v) {
        Uri uriUrl = Uri.parse(getResources().getString(R.string.url));
        Intent webView = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(webView);
    }

    private void secureButtons() {

        float disabledAlpha = 0.5f;
        int enabledLevel = serenityManager.getUserCompletedTrackLevel() + 1;
        boolean alwaysEnable = !trackTemplateFactory.getRequireMeditationsBeDoneInOrder();
        int totalTrackCount = trackTemplateFactory.getTrackTemplateCount();
        timerButton.setEnabled(true);

        for (int i = 1; i < totalTrackCount; i++) {
            TrackTemplate trackTemplate = trackTemplateFactory.getTrackTemplate(i);

            boolean isNotLastTrack = i < totalTrackCount - 1;
            LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.buttonLinearLayout);

            Button button = linearLayout.findViewById(trackTemplate.getButtonId());
            button.setEnabled(alwaysEnable || enabledLevel >= i);
            button.setAlpha(enabledLevel >= i ? 1.0f : disabledAlpha);

            if (isNotLastTrack) {
                ImageView dots = (ImageView) this.findViewById(trackTemplate.getSpacerId());
                dots.setBackgroundResource(alwaysEnable || enabledLevel >= i ? R.mipmap.dots : R.mipmap.dots);
            }
        }

        int medHours = serenityManager.getUserTotalSecondsInMeditation() / 3600;
        String meditationTimeLabelText = medHours == 1 ? String.format(Locale.getDefault(), "%d hour spent meditating", medHours) : String.format(Locale.getDefault(),"%d hours spent meditating", medHours);
        //meditationTotalTimeTextView.setText(meditationTimeLabelText);
    }

    private void connectView() {
        timerButton = (ImageButton) findViewById(R.id.silentTimerButton);
        infoButton = (TextViewCoffeeService) findViewById(R.id.appTitle);
        backgoundImage= (ImageView) findViewById(R.id.backgoundImage);

        rotateBackground= new Timer();
        TimerTask addTimerBackground = new RotateBackgoundTimer();
        rotateBackground.schedule(addTimerBackground, 0, 5000);

        infoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                didTapInfoButton(v);
            }
        });


        int trackCount = trackTemplateFactory.getTrackTemplateCount();

        for (int i = 0; i < trackCount; i++) {
            TrackTemplate trackTemplate = trackTemplateFactory.getTrackTemplate(i);
            trackTemplate.setButtonId(View.generateViewId());
            trackTemplate.setSpacerId(View.generateViewId());

            View v = LayoutInflater.from(this).inflate(R.layout.button_template, null);
            ButtonCoffeeService button = v.findViewById(R.id.templateButton);
            button.setTag(i);
            button.setId(trackTemplate.getButtonId());
            button.setText(trackTemplate.getName());
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    didTapMeditationButton(v);
                }
            });
            LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.buttonLinearLayout);
            LinearLayout parent = (LinearLayout) button.getParent();
            parent.removeView(button);
            linearLayout.addView(button);
            if (i < trackCount - 1) {
                ImageView dots = v.findViewById(R.id.templateDots);
                dots.setId(trackTemplate.getSpacerId());
                parent.removeView(dots);
                linearLayout.addView(dots);
            }
        }
    }

    /*****************************************************************/
    class RotateBackgoundTimer extends TimerTask {
        private Integer pos;
        private final int[] images;
        public RotateBackgoundTimer(){
            pos=-1;
            images= new int[9];
            images[0]=R.mipmap.background_1;
            images[1]=R.mipmap.background_2;
            images[2]=R.mipmap.background_3;
            images[3]=R.mipmap.background_4;
            images[4]=R.mipmap.background_5;
            images[5]=R.mipmap.background_6;
            images[6]=R.mipmap.background_7;
            images[7]=R.mipmap.background_8;
            images[8]=R.mipmap.background_9;
        }

        public void run() {
            this.pos++;
            if(this.pos> 8)
                this.pos=0;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    backgoundImage.setImageResource(images[pos]);
                }
            });
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        super.attachBaseContext((newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/font-diner-coffee-service.otf")
                .setFontAttrId(R.attr.fontPath)
                .disableCustomViewInflation() // <----- this fix
                .build());
        */

        setContentView(R.layout.activity_main);
        connectView();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        serenityManager.setSettings(settings);
        this.secureButtons();
        /*
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()

                .setDefaultFontPath("fonts/sf-pro-text-semibold.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        */

        autoScrollToMeditation();
    }

    private void autoScrollToMeditation() {

        int scrollToBottomOfTrack = serenityManager.getUserCompletedTrackLevel();
        if (scrollToBottomOfTrack > 0) {
            final ScrollView scrollView = (ScrollView) this.findViewById(R.id.mainScrollView);
            final int scrollToBottomOfButtonId = trackTemplateFactory.getTrackTemplate(scrollToBottomOfTrack).getButtonId();
            final LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.buttonLinearLayout);

            linearLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        public void onGlobalLayout() {
                            //Remove the listener before proceeding
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                linearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            } else {
                                linearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }
                            final int scrollToY =  linearLayout.findViewById(scrollToBottomOfButtonId).getBottom();
                            scrollView.post(new Runnable() {
                                public void run() {
                                    scrollView.scrollTo(0, scrollToY);
                                }
                            });
                        }
                    });
        }
    }

    private void presentAlerts(final int trackLevel) {
        presentCountdownLengthAlertOrRun(trackLevel);
    }

    private void presentCountdownLengthAlertOrRun(final int trackLevel) {

        serenityManager.initTrackAtLevel(trackLevel, this);
        int minDurationSeconds = serenityManager.getMinimumDuration();
        final int minDurationMinutes = minDurationSeconds / 60 + 2;

        if (!serenityManager.isMultiPart()) {
            this.runMeditationWithGap(0);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View promptView = layoutInflater.inflate(R.layout.prompt, null);

            final AlertDialog alertD = new AlertDialog.Builder(this).create();

            ButtonCoffeeService btnMinimum = promptView.findViewById(R.id.btnMinimum);
            btnMinimum.setText(String.format(Locale.getDefault(),"%d minutes (minimum)", minDurationMinutes));

            ButtonCoffeeService btnHour = promptView.findViewById(R.id.btnHour);
            ButtonCoffeeService btnFortyFive = promptView.findViewById(R.id.btnFortyFive);

            btnMinimum.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                runMeditationWithFullLength(minDurationMinutes * 60);
                alertD.dismiss();
                }
            });

            btnHour.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    runMeditationWithFullLength(60 * 60);
                    alertD.dismiss();
                }
            });

            btnFortyFive.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    runMeditationWithFullLength(45 * 60);
                    alertD.dismiss();
                }
            });


            final ButtonCoffeeService btnCustom = promptView.findViewById(R.id.btnCustom);
            final EditText userInput = promptView.findViewById(R.id.userInput);

            int customValue = serenityManager.getDefaultDurationMinutes();
            if (customValue < minDurationMinutes) {
                customValue = minDurationMinutes;
            }

            userInput.setText(String.format(Locale.getDefault(),"%d", customValue));
            userInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        btnCustom.callOnClick();
                        return true;
                    }
                    return false;
                }
            });
            userInput.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    userInput.setText("");
                }
            });

            btnCustom.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Integer userValue;
                    try {
                        userValue = Integer.parseInt(userInput.getText().toString());
                    } catch (NumberFormatException ex) {
                        presentInvalidCustomCountdownAlert(trackLevel, minDurationMinutes);
                        return;
                    }
                    if (userValue<0 || userValue>360){
                        Toast.makeText(getApplication(),"Values must be between 0 and 360", Toast.LENGTH_LONG).show();
                    }else if (userValue < minDurationMinutes) {
                        presentInvalidCustomCountdownAlert(trackLevel, minDurationMinutes);
                    } else {
                        serenityManager.setDefaultDurationMinutes(userValue);
                        runMeditationWithFullLength(userValue * 60);
                        alertD.dismiss();
                    }
                }
            });
            alertD.setView(promptView);
            alertD.show();
        }
    }

    private void presentInvalidCustomCountdownAlert(int trackLevel, int minDurationMinutes) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        String minAlertString = String.format(Locale.getDefault(), "Length for this meditation must be at least %d minutes", minDurationMinutes);

        alertDialogBuilder.setTitle("Invalid Custom Time");
        alertDialogBuilder
                .setMessage(minAlertString)
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private static final int MEDITATION_ACTIVITY_REQUEST_CODE = 0xe110;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MEDITATION_ACTIVITY_REQUEST_CODE)
            secureButtons();
    }

    private void runMeditationWithGap(int gapAmount) {
        Intent myIntent = new Intent(MainActivity.this, MeditationActivity.class);
        myIntent.putExtra("gapAmount", gapAmount);
//        MainActivity.this.startActivity(myIntent);
        startActivityForResult(myIntent, MEDITATION_ACTIVITY_REQUEST_CODE);
    }

    private void runMeditationWithFullLength(int fullLengthSeconds) {
        int minDurationSeconds = serenityManager.getMinimumDuration();
        int gapLength = fullLengthSeconds- minDurationSeconds;
        runMeditationWithGap(gapLength);
    }


}
