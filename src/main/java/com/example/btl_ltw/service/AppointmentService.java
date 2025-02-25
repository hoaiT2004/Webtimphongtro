package com.example.btl_ltw.service;

import com.example.btl_ltw.entity.Appointment;
import com.example.btl_ltw.model.request.AppointmentRequest;
import com.example.btl_ltw.model.request.schedule.UpdateScheduleRequest;
import com.example.btl_ltw.model.response.AppointmentResponse;
import com.example.btl_ltw.model.response.DeleteScheduleResponse;
import com.example.btl_ltw.model.response.schedule.UpdateScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;

public interface AppointmentService {

    AppointmentResponse createAppointment (AppointmentRequest request);

    Page<Appointment> getAllByUsername(String username, Pageable pageable) throws ParseException;

    DeleteScheduleResponse deleteScheduleById(Long scheduleId);

    Page<Appointment> getAppointmentsByUsername(String isApproval, String username, Pageable pageable);

    Page<Appointment> getAllAppointmentsByUsername(String username, Pageable pageable);

    void permitAppointment(long appointmentId);

    UpdateScheduleResponse updateAppointment (UpdateScheduleRequest request);
}
