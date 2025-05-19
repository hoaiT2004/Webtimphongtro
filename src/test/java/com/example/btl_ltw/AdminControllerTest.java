package com.example.btl_ltw;

import com.example.btl_ltw.entity.Room;
import com.example.btl_ltw.entity.User;
import com.example.btl_ltw.entity.PaymentTransaction;
import com.example.btl_ltw.repository.RoomRepository;
import com.example.btl_ltw.repository.UserRepository;
import com.example.btl_ltw.repository.PaymentTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    @WithMockUser(username = "admin", authorities = {"Admin"})
    void roomManagement_shouldReturnRoomList() throws Exception {
        mockMvc.perform(get("/admin/RoomManagement"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/RoomManagement"))
                .andExpect(model().attributeExists("rooms", "totalPage", "currentPage", "pageSize", "exportSuccess"))
                .andExpect(content().string(containsString("Ngã tư Tố Hữu-Vạn Phúc-Hà Đông")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"Admin"})
    void approveRoom_shouldSetRoomApprovedTrue() throws Exception {
        mockMvc.perform(post("/admin/approveRoom")
                        .param("roomId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/RoomManagement"));

        Room updated = roomRepository.findById(1L).orElse(null);
        assert updated != null;
        assert updated.getIsApproval().equals("true");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"Admin"})
    void disapproveRoom_shouldSetRoomApprovedFalse() throws Exception {

        mockMvc.perform(post("/admin/disapproveRoom")
                        .param("roomId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/RoomManagement"));

        Room updated = roomRepository.findById(1L).orElse(null);
        assert updated == null; //Vì k duyệt thì sẽ xóa luôn
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"Admin"})
    void removeRoom_shouldDeleteRoom() throws Exception {
        Long roomId = 2L;

        mockMvc.perform(get("/admin/remove/room")
                        .param("roomId", roomId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/RoomManagement"));

        Room deletedRoom = roomRepository.findById(roomId).orElse(null);
        assert deletedRoom == null;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"Admin"})
    void revenueManagement_shouldReturnRevenueList() throws Exception {
        mockMvc.perform(get("/admin/RevenueManagement"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("revenues", "totalPage", "currentPage", "pageSize", "exportSuccess"))
                .andExpect(view().name("admin/RevenueManagement"));
//                .andExpect(content().string(containsString("1000")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"Admin"})
    void userManagement_shouldReturnUserList() throws Exception {
        mockMvc.perform(get("/admin/UserManagement"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users", "totalPage", "currentPage", "pageSize", "exportSuccess"))
                .andExpect(view().name("admin/UserManagement"))
                .andExpect(content().string(containsString("ntthanh@gmail.com")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"Admin"})
    void printUsersToExcel_shouldRedirectWithExportSuccess() throws Exception {
        mockMvc.perform(get("/admin/printUsers"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/UserManagement?exportSuccess=true"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"Admin"})
    void printRoomsToExcel_shouldRedirectWithExportSuccess() throws Exception {
        mockMvc.perform(get("/admin/printRooms"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/RoomManagement?exportSuccess=true"));
    }

}
