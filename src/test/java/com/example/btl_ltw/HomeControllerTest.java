package com.example.btl_ltw;

import com.example.btl_ltw.entity.Room;
import com.example.btl_ltw.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser()
    void showPriceMenu_shouldReturnPriceMenuView() throws Exception {
        mockMvc.perform(get("/api/home/priceMenu"))
                .andExpect(status().isOk())
                .andExpect(view().name("priceMenu"));
    }

    @Test
    @WithMockUser()
    void normalRoom_shouldReturnIndexViewWithRooms() throws Exception {
        mockMvc.perform(get("/api/home")
                        .param("pageNo", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("rooms", "currentPage", "totalPage", "request"))
                .andExpect(content().string(containsString("Ngã tư Tố Hữu-Vạn Phúc-Hà Đông")));
    }

    @Test
    @WithMockUser()
    void vipRoom_shouldReturnVipRoomViewWithRooms() throws Exception {
        mockMvc.perform(get("/api/home/vipRoom")
                        .param("pageNo", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("vipRoom"))
                .andExpect(model().attributeExists("rooms", "currentPage", "totalPage", "request"))
                .andExpect(content().string(containsString("Ngõ 1 Trần Nhật Duật, Q.Trung, Hà Đông, Hà Nội")));
    }

    @Test
    @WithMockUser()
    void newsPages_shouldReturnCorrectViews() throws Exception {
        mockMvc.perform(get("/api/home/news_1"))
                .andExpect(status().isOk())
                .andExpect(view().name("news_1"));

        mockMvc.perform(get("/api/home/news_2"))
                .andExpect(status().isOk())
                .andExpect(view().name("news_2"));

        mockMvc.perform(get("/api/home/news_3"))
                .andExpect(status().isOk())
                .andExpect(view().name("news_3"));

        mockMvc.perform(get("/api/home/news_4"))
                .andExpect(status().isOk())
                .andExpect(view().name("news_4"));
    }
}
