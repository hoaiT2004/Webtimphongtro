package com.example.btl_ltw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LandlordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "thanh123", authorities = {"Landlord"})
    void showTransactionHistory_shouldReturnTransactionView() throws Exception {
        mockMvc.perform(get("/landlord/transaction"))
                .andExpect(status().isOk())
                .andExpect(view().name("landlord/transaction_history"))
                .andExpect(model().attributeExists("transactions", "currentPage", "totalPage"));
    }

    @Test
    @WithMockUser(username = "thanh123", authorities = {"Landlord"})
    void showAppointment_shouldReturnViewingAppointmentView() throws Exception {
        mockMvc.perform(get("/landlord/appointment"))
                .andExpect(status().isOk())
                .andExpect(view().name("landlord/viewingAppointment"))
                .andExpect(model().attributeExists("Appointments", "currentPage", "pageSize", "totalPage"));
    }

    @Test
    @WithMockUser(username = "thanh123", authorities = {"Landlord"})
    void resolvePermitAppointment() throws Exception {
        mockMvc.perform(get("/landlord/appointment")
                        .param("appointmentId", "2")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("landlord/appointment"));
    }

    @Test
    @WithMockUser(username = "thanh123", authorities = {"Landlord"})
    void showOwnedRooms_shouldReturnManageRoomView() throws Exception {
        mockMvc.perform(get("/landlord/manage"))
                .andExpect(status().isOk())
                .andExpect(view().name("landlord/manage_room"))
                .andExpect(model().attributeExists("rooms", "currentPage", "totalPage", "request"));
    }

    @Test
    @WithMockUser(username = "thanh123", authorities = {"Landlord"})
    void showAddRoomForm_shouldReturnAddRoomView() throws Exception {
        mockMvc.perform(get("/landlord/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("landlord/addroom"))
                .andExpect(model().attributeExists("room"));
    }

    @Test
    @WithMockUser(username = "thanh123", authorities = {"Landlord"})
    void showUpdateRoomForm_shouldReturnUpdateRoomView() throws Exception {
        mockMvc.perform(get("/landlord/update")
                        .param("roomId", "6"))
                .andExpect(status().isOk())
                .andExpect(view().name("landlord/updateroom"))
                .andExpect(model().attributeExists("images", "room"));
    }

    @Test
    @WithMockUser(username = "minh197", authorities = {"Landlord"})
    void resolveUpdateRoom() throws Exception {
        mockMvc.perform(post("/landlord/update")
                        .param("roomType", "CHUNG_CHU")
                        .param("room_id", "3")
                        .param("price","2")
                        .param("waterPrice", "2")
                        .param("electricityPrice", "2")
                        .param("address", "HN")
                        .param("capacity", "99")
                        .param("description", "test unit test")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/landlord/manage"));
    }

    @Test
    @WithMockUser(username = "minh197", authorities = {"Landlord"})
    void resolveDeleteRoom() throws Exception {
        mockMvc.perform(post("/landlord/delete")
                        .param("roomId", "3")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/landlord/manage"));
    }
}
