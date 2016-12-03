package com.mabrouk.medicalconferences.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mabrouk.medicalconferences.R;
import com.mabrouk.medicalconferences.model.Invitation;

import java.util.List;

/**
 * Created by User on 12/3/2016.
 */

public class InvitationDoctorAdapter extends RecyclerView.Adapter<InvitationDoctorAdapter.InvitationDoctorViewHolder> {
    List<Invitation> invitations;
    public InvitationDoctorAdapter(List<Invitation> invitations) {
        this.invitations = invitations;
    }

    @Override
    public InvitationDoctorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_invitation_doctor, parent, false);
        return new InvitationDoctorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InvitationDoctorViewHolder holder, int position) {
        Invitation invitation = invitations.get(position);
        holder.doctorName.setText(String.format("To: %s %s", invitation.getDoctor().getFirstName(),
                invitation.getDoctor().getLastName()));
        holder.state.setText(Invitation.getStateTextForAdmin(invitation));
    }

    @Override
    public int getItemCount() {
        return invitations.size();
    }

    static class InvitationDoctorViewHolder extends RecyclerView.ViewHolder{
        TextView state;
        TextView doctorName;

        public InvitationDoctorViewHolder(View itemView) {
            super(itemView);
            state = (TextView) itemView.findViewById(R.id.state);
            doctorName = (TextView) itemView.findViewById(R.id.doctor_name);
        }
    }
}
