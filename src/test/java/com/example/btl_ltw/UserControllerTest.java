package com.example.btl_ltw;

import com.example.btl_ltw.entity.User;
import com.example.btl_ltw.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerPage_shouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/user/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/register"));
    }

    @Test
    void registerUser_shouldCreateNewUser() throws Exception {
        mockMvc.perform(post("/user/register")
                        .param("username", "hoadev2005")
                        .param("email", "newuser@gmail.com")
                        .param("password", "Trumhoahoc1@")
                        .param("fullname", "Nguyen van a")
                        .param("tel", "0123456789")
                        .param("role_id", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));

        User user = userRepository.findUserByUsername("hoadev2005").orElse(null);
        assert user != null;
        assert user.getEmail().equals("newuser@gmail.com");
    }

    @Test
    void loginPage_shouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/user/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/login"));
    }

    @Test
    @WithMockUser(username = "hoa2004", authorities = {"Tenant"})
    void showProfile() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"))
                .andExpect(model().attributeExists("username", "role", "linkAvatar", "fullname", "email", "tel"));
    }

    @Test
    @WithMockUser(username = "hoa2004", authorities = {"Tenant"})
    void changeInfo() throws Exception {
        mockMvc.perform(post("/user/changeInfo")
                        .param("username", "hoa2004")
                        .param("fullname", "Nguyen Van B")
                        .param("tel", "0999999999")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"));
    }

    @Test
    @WithMockUser(username = "hoa2004", authorities = {"Tenant"})
    void showChangePasswordForm() throws Exception {
        mockMvc.perform(get("/user/changePassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/changePassword"))
                .andExpect(model().attributeExists("username", "role"));
    }

    @Test
    @WithMockUser(username = "hoadev2005", authorities = {"Tenant"})
    void changePassword_shouldUpdateUserPassword() throws Exception {
        mockMvc.perform(post("/user/changePassword")
                        .param("password", "Trumasdasd1@")
                        .param("newPassword", "Trumaac2@")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"));

        String password = userRepository.loadPasswordByUsername("hoadev2005");
        assert passwordEncoder.matches("Trumaac2@", password);
    }

    @Test
    @WithMockUser()
    void showRetrievePassword() throws Exception {
        mockMvc.perform(get("/user/retrievePassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/enterUsername"));
    }

    @Test
    @WithMockUser(username = "hoadev2005", authorities = {"Tenant"})
    void saveNewPassword() throws Exception {
        mockMvc.perform(post("/user/createNewPassword")
                        .param("newPassword", "Trumasdasd1@")
                        .param("username", "hoadev2005")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));

        String password = userRepository.loadPasswordByUsername("hoadev2005");
        assert passwordEncoder.matches("Trumasdasd1@", password);
    }

    @Test
    @WithMockUser(username = "hoadev2005", authorities = {"Tenant"})
    void resolveSendEmail() throws Exception {
        mockMvc.perform(get("/user/sendEmailAgain")
                        .param("username", "hoadev2005")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/createNewPassword"))
                .andExpect(model().attributeExists("username", "role"));
    }

    @Test
    @WithMockUser(username = "hoa2004", authorities = {"Tenant"})
    void viewRoomViewSchedule_shouldReturnRoomViewScheduleList() throws Exception {
        mockMvc.perform(get("/user/schedule"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/roomViewingSchedule"))
                .andExpect(model().attributeExists("username", "role", "Appointments", "currentPage", "pageSize", "totalPage"));
    }

    @Test
    @WithMockUser(username = "hoa2004", authorities = {"Tenant"})
    void deleteRoomViewingSchedule() throws Exception {
        mockMvc.perform(get("/user/deleteSchedule")
                        .param("scheduleId", "3")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/schedule?pageNo=1&pageSize=10"));
    }

    @Test
    @WithMockUser(username = "hoa2004", authorities = {"Tenant"})
    void updateSchedule() throws Exception {
        mockMvc.perform(post("/user/updateSchedule")
                        .param("appointmentId", "4")
                        .param("comeDate", "2030-05-11")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/schedule?pageNo=1&pageSize=10"));
    }
}
