package com.example.btl_ltw;

import com.example.btl_ltw.model.dto.CommentDto;
import com.example.btl_ltw.model.dto.RoomDto;
import com.example.btl_ltw.model.dto.UserDto;
import com.example.btl_ltw.service.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "hoa2004", authorities = {"Tenant"})
    void showDetailRoom_shouldReturnRoomDetailsPage() throws Exception {
        mockMvc.perform(get("/api/room")
                        .param("room_id", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("room/details"))
                .andExpect(model().attributeExists("room", "images", "userDto", "parentComments", "subComments", "starAvarage"));
    }

    @Test
    @WithMockUser(username = "hoa2004", authorities = {"Tenant"})
    void schedule_shouldReturnScheduleView() throws Exception {
        mockMvc.perform(get("/api/room/schedule")
                        .param("room_id", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("room/schedule"))
                .andExpect(model().attributeExists("room_id", "dto"));
    }

    @Test
    @WithMockUser(username = "hoa2004", authorities = {"Tenant"})
    void resolveCreateAppointment() throws Exception {
        mockMvc.perform(post("/api/room/schedule")
                        .param("username", "hoa2004")
                        .param("fullname", "Nguyen Xuan Hoa")
                        .param("tel", "0343199493")
                        .param("email", "nguy@gmail.com")
                        .param("numPeople", "3")
                        .param("comeDate", "2030-02-04")
                        .param("transportation", "Đi bộ")
                        .param("room_id", "3")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/home"));
    }

    @Test
    @WithMockUser(username = "hoa2004", authorities = {"Tenant"})
    void addStar_shouldRedirectToRoomDetail() throws Exception {
        mockMvc.perform(post("/api/room/addStar")
                        .param("roomId", "3")
                        .param("rating", "5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/room?room_id=3"));
    }
}
