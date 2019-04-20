package com.example.appbaothuc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appbaothuc.alarmsetting.SettingAlarmFragment;

import java.util.Collections;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private Context context;
    private UpcomingAlarmFragment upcomingAlarmFragment;
    private List<Alarm> listAlarm;

    private SparseIntArray mapButtonAlarm;
    public AlarmAdapter(Context context, UpcomingAlarmFragment upcomingAlarmFragment, List<Alarm> listAlarm) {
        this.context = context;
        this.upcomingAlarmFragment = upcomingAlarmFragment;
        this.listAlarm = listAlarm;
        this.mapButtonAlarm = new SparseIntArray();
    }

    @Override
    public int getItemCount() {
        return this.listAlarm.size();
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View alarmView = layoutInflater.inflate(R.layout.item_alarm, viewGroup, false);
        return new AlarmViewHolder(alarmView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlarmViewHolder alarmViewHolder, int i) {
        mapButtonAlarm.put(alarmViewHolder.buttonAlarmType.getId(), i);
        final Alarm alarm = listAlarm.get(i);
        final CheckBox checkBoxEnable = alarmViewHolder.checkBoxEnable;
        TextView textViewHour = alarmViewHolder.textViewHour;
        TextView textViewMinute = alarmViewHolder.textViewMinute;
        TextView textViewDescribeRepeatDay = alarmViewHolder.textViewDescribeRepeatDay;
        ImageView buttonAlarmType = alarmViewHolder.buttonAlarmType;

        checkBoxEnable.setChecked(alarm.isEnable());
        textViewHour.setText(String.valueOf(alarm.getHour()));
        if (alarm.getMinute() < 10) {
            textViewMinute.setText("0" + alarm.getMinute());
        }
        else {
            textViewMinute.setText(String.valueOf(alarm.getMinute()));
        }
        textViewDescribeRepeatDay.setText(alarm.getDescribeRepeatDay());

        checkBoxEnable.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                alarm.setEnable(checkBoxEnable.isChecked());
                upcomingAlarmFragment.updateAlarmEnable(alarm);
                listAlarm.set(alarmViewHolder.getAdapterPosition(), alarm);
                Collections.sort(listAlarm);
                AlarmAdapter.this.notifyDataSetChanged();
            }
        });

        buttonAlarmType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlarmSetting(v);
            }
        });
    }
    public void openAlarmSetting(View v) {
        SettingAlarmFragment settingAlarmFragment = SettingAlarmFragment.newInstance();
        settingAlarmFragment.configure(upcomingAlarmFragment, listAlarm.get(mapButtonAlarm.get(v.getId())));
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.full_screen_fragment_container, settingAlarmFragment).commit();
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBoxEnable;
        private TextView textViewHour;
        private TextView textViewMinute;
        private TextView textViewDescribeRepeatDay;
        private ImageButton buttonAlarmType;
        AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxEnable = itemView.findViewById(R.id.check_box_enable);
            textViewHour = itemView.findViewById(R.id.text_view_hour);
            textViewMinute = itemView.findViewById(R.id.text_view_minute);
            textViewDescribeRepeatDay = itemView.findViewById(R.id.text_view_describe_repeat_day);
            buttonAlarmType = itemView.findViewById(R.id.button_alarm_type);
            buttonAlarmType.setId(View.generateViewId());
        }
    }
}