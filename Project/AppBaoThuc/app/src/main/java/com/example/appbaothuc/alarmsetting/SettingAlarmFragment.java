package com.example.appbaothuc.alarmsetting;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.appbaothuc.Alarm;
import com.example.appbaothuc.Music;
import com.example.appbaothuc.R;
import com.example.appbaothuc.UpcomingAlarmFragment;
import com.example.appbaothuc.challenge.ChallengeActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SettingAlarmFragment extends Fragment implements LableDialogFragment.LabelDialogListener,
        AgainDialogFragment.AgainDialogListener, RepeatDialogFragment.RepeatDialogListener, View.OnClickListener{
    enum SettingAlarmMode{
        ADD_NEW, EDIT
    }
    private Context context;
    private UpcomingAlarmFragment upcomingAlarmFragment;

    private SettingAlarmMode settingAlarmMode;
    public  static Alarm alarm;
    private TimePicker timePicker; // Chọn giờ
    private Button btnPlayMusic, btnCancel, btnDelete, btnOk; //Phát nhạc đang chọn, Hủy thao tác, Xóa báo thức, Hoàn tất
    private LinearLayout linearLayoutLabel, linearLayoutType, linearLayoutRingTone,
            linearLayoutRepeat, linearLayoutAgain;
    private TextView textViewPlus10M, textViewMinus10M, textViewPlus1H,
            textViewMinus1H;
    private TextView textViewTimeLeft /*thời gian còn lại*/, textViewType, textViewRepeat,
            textViewRingTone, textViewAgain, textViewLabel;
    private ImageView imageViewType; //cái hình điện thoại rung
    private SeekBar seekBar; // thanh âm lượng
    private Switch aSwitch; // bật tắt rung

    private Music music;
    private String label, outputAgain, outputRepeat;
    private int hour, minute, snoozeTime, volume;
    private List<Boolean> listRepeatDay;
    private boolean vibrate;
    private FragmentManager fragmentManager;
    private TypeFragment typeFragment;
    private MusicPickerFragment musicPickerFragment;
    public static int challengeType; // chua co
    public static SettingAlarmFragment newInstance(){
        SettingAlarmFragment settingAlarmFragment = new SettingAlarmFragment();
        return settingAlarmFragment;
    }
    public void configure(UpcomingAlarmFragment upcomingAlarmFragment, Alarm alarm){
        this.upcomingAlarmFragment = upcomingAlarmFragment;
        SettingAlarmFragment.alarm = alarm;
        if(alarm == null){
            this.settingAlarmMode = SettingAlarmMode.ADD_NEW;
            SettingAlarmFragment.alarm = this.createDefaultAlarm();
        }
        else{
            this.settingAlarmMode = SettingAlarmMode.EDIT;
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_alarm, container, false);
        setControl(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.upcomingAlarmFragment = null;
        alarm = null;
    }

    private Alarm createDefaultAlarm(){
        List<Boolean> listRepeatDay = Arrays.asList(true, true, true, true, true, true, true);
        return new Alarm(true, R.integer.default_hour,R.integer.default_hour, listRepeatDay);
    }

    void setControl(View view){
        music = new Music(alarm.getRingtoneUrl(), alarm.getRingtoneName());
        listRepeatDay = alarm.getListRepeatDay();
        btnPlayMusic = view.findViewById(R.id.btnPlayMusic);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnOk = view.findViewById(R.id.btnOk);
        linearLayoutType = view.findViewById(R.id.linearLayoutType);
        linearLayoutRingTone = view.findViewById(R.id.linearLayoutRingTone);
        linearLayoutRepeat = view.findViewById(R.id.linearLayoutRepeat);
        linearLayoutAgain = view.findViewById(R.id.linearLayoutAgain);
        linearLayoutLabel = view.findViewById(R.id.linearLayoutLabel);

        textViewTimeLeft = view.findViewById(R.id.textViewTimeLeft);
        textViewPlus10M = view.findViewById(R.id.textViewPlus10M);
        textViewMinus10M = view.findViewById(R.id.textViewMinus10M);
        textViewPlus1H = view.findViewById(R.id.textViewPlus1H);
        textViewMinus1H = view.findViewById(R.id.textViewMinus1H);
        textViewType = view.findViewById(R.id.textViewType);
        textViewRepeat = view.findViewById(R.id.textViewRepeat);
        textViewRingTone = view.findViewById(R.id.textViewRingTone);
        textViewAgain = view.findViewById(R.id.textViewAgain);
        textViewLabel = view.findViewById(R.id.textViewLabel);

        imageViewType = view.findViewById(R.id.imageViewType);
        seekBar = view.findViewById(R.id.seekBar);
        aSwitch = view.findViewById(R.id.aSwitch);
        timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);


        btnOk.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                outputAgain = textViewAgain.getText().toString();
                label = textViewLabel.getText().toString();

                if(Build.VERSION.SDK_INT >= 23){
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                }
                else{
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }
                timePicker.getCurrentHour();
                volume = seekBar.getProgress();
                if(aSwitch.isChecked()) vibrate = true;
                else vibrate = false;

                alarm.setHour(hour);
                alarm.setMinute(minute);
                alarm.setListRepeatDay(listRepeatDay);
                alarm.setRingtoneUrl(music.getUrl());
                alarm.setRingtoneName(music.getName());
                alarm.setLabel(label);
                alarm.setVibrate(vibrate);
                alarm.setSnoozeTime(snoozeTime);
                alarm.setVolume(volume);
                alarm.setChallengeType(challengeType);
                if(settingAlarmMode == SettingAlarmMode.EDIT){
                    upcomingAlarmFragment.editAlarm(alarm);
                }
                else if(settingAlarmMode == SettingAlarmMode.ADD_NEW){
                    upcomingAlarmFragment.addNewAlarm(alarm);
                }
                getFragmentManager().beginTransaction().remove(SettingAlarmFragment.this).commit();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(settingAlarmMode == SettingAlarmMode.EDIT){
                    upcomingAlarmFragment.deleteAlarm(alarm.getIdAlarm());
                    getFragmentManager().beginTransaction().remove(SettingAlarmFragment.this).commit();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(SettingAlarmFragment.this).commit();
            }
        });
        linearLayoutType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().add(R.id.full_screen_fragment_container, typeFragment).commit();
            }
        });
        linearLayoutRingTone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().add(R.id.full_screen_fragment_container, musicPickerFragment).commit();
            }
        });
        linearLayoutLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLableDialog();
            }
        });

        linearLayoutAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAgainDialog();
            }
        });

        linearLayoutRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRepeatDialog();
            }
        });
        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///playMusic();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if(settingAlarmMode == SettingAlarmMode.ADD_NEW){
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, 1);
            setHour(timePicker, now.get(Calendar.HOUR_OF_DAY));
            setMinute(timePicker, now.get(Calendar.MINUTE));
        }
        else if(settingAlarmMode == SettingAlarmMode.EDIT){
            setHour(timePicker, alarm.getHour());
            setMinute(timePicker,alarm.getMinute());
        }
        textViewRepeat.setText(alarm.getDescribeRepeatDay());
        seekBar.setProgress(alarm.getVolume());
        aSwitch.setChecked(alarm.isVibrate());
        textViewLabel.setText(alarm.getLabel());

        String alarmTextAgain;
        if(alarm.getSnoozeTime() == 0) alarmTextAgain = "Tắt.";
        else alarmTextAgain = alarm.getSnoozeTime() + " Phút.";
        textViewAgain.setText(alarmTextAgain);

        textViewRingTone.setText(alarm.getRingtoneName());

        textViewMinus1H.setOnClickListener(this); // event trong hàm onClick()
        textViewMinus10M.setOnClickListener(this);
        textViewPlus1H.setOnClickListener(this);
        textViewPlus10M.setOnClickListener(this);

        fragmentManager = getFragmentManager();
        typeFragment = new TypeFragment();
        typeFragment.setEnterTransition(new Slide(Gravity.TOP));
        typeFragment.setExitTransition(new Slide(Gravity.TOP));
        musicPickerFragment = MusicPickerFragment.newInstance(this, alarm);
        musicPickerFragment.setEnterTransition(new Slide(Gravity.BOTTOM));
        musicPickerFragment.setExitTransition(new Slide(Gravity.BOTTOM));

        textViewRingTone.setText(alarm.getRingtoneName());
        String textType = null;
        if(challengeType == 0) textType = "Default";
        else if(challengeType == 1) textType = "Math problems";
        else if(challengeType == 2) textType = "Shake";
        textViewType.setText(textType);
    }
    private int getHour(TimePicker timePicker){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return timePicker.getHour();
        }
        else {
            return timePicker.getCurrentHour();
        }
    }
    private int getMinute(TimePicker timePicker){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return timePicker.getMinute();
        }
        else {
            return timePicker.getCurrentMinute();
        }
    }
    private void setHour(TimePicker timePicker, int hour){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            timePicker.setHour(hour);
        }
        else {
            timePicker.setCurrentHour(hour);
        }
    }
    private void setMinute(TimePicker timePicker, int minute){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            timePicker.setMinute(minute);
        }
        else {
            timePicker.setCurrentMinute(minute);
        }
    }

    @Override
    public void onClick(View view) {
        int timeHour = getHour(timePicker);
        int timeMinute = getMinute(timePicker);
        switch (view.getId()){
            case R.id.textViewMinus1H:  // bấm trừ 1 giờ
                if(timeHour == 0) setHour(timePicker, 23);
                else  setHour(timePicker, timeHour - 1);
                break;
            case R.id.textViewPlus1H:   // bấm cộng 1 giờ
                if(timeHour == 23) setHour(timePicker, 0);
                else  setHour(timePicker, timeHour + 1);
                break;
            case R.id.textViewMinus10M: // Bấm trừ 10 phút
                if(timeMinute - 10 < 0) {   // nếu trừ 10 phút mà ra số âm thì phải trừ giờ xuống 1.
                    if(timeHour == 0) setHour(timePicker, 23);
                    else  setHour(timePicker, timeHour - 1);
                    setMinute(timePicker, 60 - (10 - timeMinute));   // phút thì còn tầm 5 mươi mấy đó theo công thức
                }
                else setMinute(timePicker, timeMinute - 10);
                break;
            case R.id.textViewPlus10M:
                if(timeMinute + 10 > 59) {
                    if(timeHour == 23) setHour(timePicker, 0);
                    else setHour(timePicker, timeHour + 1);
                    setMinute(timePicker, 10 + timeMinute - 60);
                }
                else setMinute(timePicker, timeMinute + 10);
                break;
        }
    }
    private void showLableDialog() { // show fragment để Sửa tên báo thức
        LableDialogFragment lableDialogFragment = new LableDialogFragment();
        lableDialogFragment.setListener(this);
        lableDialogFragment.show(getChildFragmentManager(), "fragment_edit_name");
    }
    @Override
    public void onFinishEditDialog(String inputText) { //get text ở label dialog fragment
        textViewLabel.setText(inputText);
    }


    private void showAgainDialog() { // fragment chọn thời gian báo thức lại
        AgainDialogFragment againDialogFragment = new AgainDialogFragment();
        againDialogFragment.setListener(this);
        againDialogFragment.show(getChildFragmentManager(), "fragment_choice");
    }
    @Override
    public void onFinishChoiceDialog(Integer input) {
        snoozeTime = input;
        if(input == 0) textViewAgain.setText("Tắt.");
        else textViewAgain.setText(input + " Phút.");
    }

    private void showRepeatDialog() { // fragment chọn các ngày báo thức
        RepeatDialogFragment repeatDialogFragment = new RepeatDialogFragment();
        repeatDialogFragment.setListener(this);
        repeatDialogFragment.show(getChildFragmentManager(), "fragment_repeat");
    }
    @Override
    public void onFinishCheckDialog(ArrayList<Boolean> input) {
        listRepeatDay = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            listRepeatDay.add(i, false);
            listRepeatDay.set(i, input.get(i));
        }
        //createStringRepeat(input);
        alarm.setListRepeatDay(listRepeatDay);
        createStringRepeat();
    }
    public void createStringRepeat(){
        textViewRepeat.setText(alarm.getDescribeRepeatDay());
    }
    public void createStringRepeat(ArrayList<Boolean> listDays){
        //TODO: aaaa
        String repeatString = "";
        int i = 0;
        for(i = 0; i < 7; i++){
            if(listDays.get(i) == false) break;
        }
        if(i == 7) {
            outputRepeat = "Hằng ngày.";
            textViewRepeat.setText(outputRepeat);
            return;
        }
        else if(i == 5){
            outputRepeat = "Các ngày làm việc.";
            textViewRepeat.setText(outputRepeat);
            return;
        }
        i = 0;
        for(i = 0; i < 5; i++){
            if(listDays.get(i) == true) break;
        }
        if(i == 5 && listDays.get(5) == true && listDays.get(6) == true ){
            outputRepeat = "Cuối tuần.";
            textViewRepeat.setText(outputRepeat);
            return;
        }
        else {
            for(int j = 0; j < 7; j++){
                if(listDays.get(j) == true){
                    if(j == 6) repeatString += " CN";
                    else repeatString += " T" + (j+2);
                }
            }
            outputRepeat = repeatString;
            textViewRepeat.setText(outputRepeat);
        }
    }


    public void getMusic(Music music) {
        this.music = music;
        alarm.setRingtoneUrl(music.getUrl());
        alarm.setRingtoneName(music.getName());
        textViewRingTone.setText(music.getName());
    }

}
