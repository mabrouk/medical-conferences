package com.mabrouk.medicalconferences.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.mabrouk.medicalconferences.R;
import com.mabrouk.medicalconferences.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 12/3/2016.
 */

public class DoctorsListAdapter extends BaseAdapter {
    List<DoctorWrapper> doctors;

    public DoctorsListAdapter(List<User> doctors) {
        int size = doctors.size();
        this.doctors = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            this.doctors.add(new DoctorWrapper(doctors.get(i)));
        }
    }

    @Override
    public int getCount() {
        return doctors.size();
    }

    @Override
    public Object getItem(int position) {
        return doctors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DoctorWrapper wrapper = doctors.get(position);
        DoctorViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_doctor, parent, false);
            viewHolder = new DoctorViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (DoctorViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(String.format("Dr: %s %s", wrapper.doctor.getFirstName(),
                wrapper.doctor.getLastName()));
        viewHolder.name.setChecked(wrapper.selected);
        viewHolder.name.setOnClickListener(v -> {
            wrapper.selected = !wrapper.selected;
            viewHolder.name.setChecked(wrapper.selected);
            viewHolder.name.setBackgroundColor(wrapper.selected ? 0xffaaff44 : 0x00 );
        });
        return convertView;
    }

    public List<User> getSelectedDoctors() {
        ArrayList<User> list = new ArrayList<>(10);
        int size = doctors.size();
        for(int i = 0; i < size; i++) {
            DoctorWrapper wrapper = doctors.get(i);
            if(wrapper.selected) {
                list.add(wrapper.doctor);
            }
        }
        return list;
    }

    static class DoctorViewHolder {
        CheckedTextView name;
        public DoctorViewHolder(View view) {
            name = (CheckedTextView) view.findViewById(R.id.doctor_name);
        }
    }

    static class DoctorWrapper {
        User doctor;
        boolean selected;
        DoctorWrapper(User doctor) {
            this.doctor = doctor;
            selected = false;
        }
    }
}
