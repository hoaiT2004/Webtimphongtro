package com.example.btl_ltw.model.dto;


import com.example.btl_ltw.entity.Appointment;
import lombok.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDto {

    private long id;

    private String username;

    private String fullname;

    private String tel;

    private String email;

    private int numPeople;

    private String comeDate;

    private String transportation;

    private long room_id;

    private String isApproval;

    public static AppointmentDto toDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        String comeDate = appointment.getComeDate().getDate() +
                    "/" + (appointment.getComeDate().getMonth() + 1) + "/" + (appointment.getComeDate().getYear() + 1900);

        return AppointmentDto.builder()
                .id(appointment.getId())
                .username(appointment.getUsername())
                .room_id(appointment.getRoom_id())
                .tel(appointment.getTel())
                .email(appointment.getEmail())
                .comeDate(comeDate)
                .fullname(appointment.getFullname())
                .numPeople(appointment.getNumPeople())
                .isApproval(appointment.getIsApproval())
                .transportation(appointment.getTransportation())
                .build();
    }

    public static Appointment toAppointment(AppointmentDto appointment) {
        if (appointment == null) {
            return null;
        }
        Date comeDate = null;
        try {
            comeDate = new SimpleDateFormat("dd/MM/yyyy").parse(appointment.getComeDate());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Appointment.builder()
                .username(appointment.getUsername())
                .room_id(appointment.getRoom_id())
                .tel(appointment.getTel())
                .email(appointment.getEmail())
                .comeDate(comeDate)
                .fullname(appointment.getFullname())
                .numPeople(appointment.getNumPeople())
                .transportation(appointment.getTransportation())
                .isApproval(appointment.getIsApproval())
                .build();
    }

    public static List<AppointmentDto> toDto(List<Appointment> appointments) {
        return appointments.stream()
                .map(AppointmentDto::toDto)
                .collect(Collectors.toList());
    }

    public static List<Appointment> toAppointment(List<AppointmentDto> appointmentDtos) {
        return appointmentDtos.stream()
                .map(AppointmentDto::toAppointment)
                .collect(Collectors.toList());
    }
}
