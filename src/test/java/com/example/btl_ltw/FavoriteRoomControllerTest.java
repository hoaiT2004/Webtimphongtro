package com.example.btl_ltw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional : Dữ liệu sẽ rollback lại sau khi chạy xong
public class FavoriteRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "hoa2004", authorities = {"Tenant"})
    void showFavoriteRooms_shouldReturnRoomList() throws Exception {
        mockMvc.perform(get("/room/favorites"))
                .andExpect(status().isOk())
                .andExpect(view().name("room/favorite_rooms"))
                .andExpect(model().attributeExists("rooms", "totalPage", "currentPage", "pageSize", "request"))
                .andExpect(content().string(containsString("165 Phố Văn Quán")));
    }

    @Test
    @WithMockUser(username = "hoa2004", authorities = {"Tenant"})
    void addFavoriteRoom_shouldRedirectToRoomDetail() throws Exception {
        Long testRoomId = 6L;
        mockMvc.perform(get("/room/" + testRoomId + "/save"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/room?room_id=" + testRoomId + "&FavoriteRoomAddingError=false"));
    }

    @Test
    @WithMockUser(username = "hoa2004", authorities = {"Tenant"})
    void removeFavoriteRoom_shouldRedirectToFavorites() throws Exception {
        Long testRoomId = 6L;
        mockMvc.perform(post("/room/" + testRoomId + "/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/room/favorites"));
    }
}
