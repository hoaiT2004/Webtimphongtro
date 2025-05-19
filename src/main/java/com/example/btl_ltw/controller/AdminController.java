package com.example.btl_ltw.controller;

import com.example.btl_ltw.entity.PaymentTransaction;
import com.example.btl_ltw.entity.User;
import com.example.btl_ltw.model.dto.PaymentDto;
import com.example.btl_ltw.model.dto.UserDto;
import com.example.btl_ltw.model.dto.RoomDto;
import com.example.btl_ltw.model.request.room.RoomFilterDataRequest;
import com.example.btl_ltw.service.PaymentService;
import com.example.btl_ltw.service.RoomService;
import com.example.btl_ltw.service.UserService;
import com.example.btl_ltw.entity.Room;
//import com.example.btl_oop.model.dto.RoomDTO;
import com.example.btl_ltw.service.excel.ExcelExport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.time.Month;
import java.util.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ExcelExport excelExport;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;


    private void func_common(Authentication authentication, Model model) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            String roleLength = userDetails.getAuthorities().toString();
            model.addAttribute("role", roleLength.substring(1, roleLength.length() - 1));
        }
    }

    @GetMapping("/RoomManagement")
    public String roomManagement(Authentication authentication, Model model,
                                 @ModelAttribute RoomFilterDataRequest request,
                                 @RequestParam(name = "pageSize", defaultValue = "10") String sizeOfPage,
                                 @RequestParam(name = "exportSuccess", defaultValue = "false") String exportSuccess
            , @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) throws JsonProcessingException {
        func_common(authentication, model);
        Pageable pageable = PageRequest.of(pageNo - 1, Integer.parseInt(sizeOfPage));
        Page<Room> roomPage = roomService.getAllRoomsWithManyContraintsForAdmin(request, pageable);
        List<Room> roomList = new ArrayList<>();
        roomPage.forEach(roomList::add);

        model.addAttribute("rooms", RoomDto.toDto(roomList));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("pageSize", sizeOfPage);
        model.addAttribute("totalPage", roomPage.getTotalPages() == 0 ? 1 : roomPage.getTotalPages());
        model.addAttribute("exportSuccess", exportSuccess);

        // Lưu trạng thái lọc khi chuyển trang
        model.addAttribute("request", request);

        List<Room> rooms = RoomDto.toRoom(roomService.getAllRoom());
        // Lấy thời điểm hiện tại
        Calendar now = Calendar.getInstance();

        // Tạo 3 map thống kê theo tuần, tháng, năm
        Map<Integer, Long> weekStats = new HashMap<>();
        Map<Integer, Long> monthStats = new HashMap<>();
        Map<Integer, Long> yearStats = new HashMap<>();

        // Lấy năm đầu tiên có người dùng đăng ký
        int firstYear = now.get(Calendar.YEAR);
        for (Room room : rooms) {
            Date createdAt = room.getCreatedAt();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createdAt);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0
            int week = calendar.get(Calendar.WEEK_OF_YEAR);

            // Cập nhật năm đầu tiên có dữ liệu
            firstYear = Math.min(firstYear, year);

            // Thống kê theo tuần (7 tuần gần nhất)
            if (now.get(Calendar.WEEK_OF_YEAR) - week <= 6) {
                weekStats.put(week, weekStats.getOrDefault(week, 0L) + 1);
            }

            // Thống kê theo tháng (12 tháng gần nhất)
            if (now.get(Calendar.YEAR) == year) {
                monthStats.put(month, monthStats.getOrDefault(month, 0L) + 1);
            }

            // Thống kê theo năm (từ năm có người đăng ký đầu tiên đến hiện tại)
            yearStats.put(year, yearStats.getOrDefault(year, 0L) + 1);
        }

        // Chuyển Map thành JSON để dùng trong Thymeleaf
        ObjectMapper objectMapper = new ObjectMapper();
        model.addAttribute("weekStats", objectMapper.writeValueAsString(weekStats));
        model.addAttribute("monthStats", objectMapper.writeValueAsString(monthStats));
        model.addAttribute("yearStats", objectMapper.writeValueAsString(yearStats));

        return "admin/RoomManagement";
    }

    @GetMapping("/RevenueManagement")
    public String revenueManagement(Authentication authentication, Model model,
                                    @RequestParam(name = "pageSize", defaultValue = "10") String sizeOfPage,
                                 @RequestParam(name = "exportSuccess", defaultValue = "false") String exportSuccess,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) throws JsonProcessingException {
        func_common(authentication, model);

        if (pageNo < 1) {
            pageNo = 1; // Đảm bảo pageNo luôn bắt đầu từ 1
        }

        Pageable pageable = PageRequest.of(pageNo - 1, Integer.parseInt(sizeOfPage));
        Page<PaymentDto> revenues = paymentService.getAllRevenueForAdmin(pageable);
        List<PaymentDto> revenueList = new LinkedList<>();
        revenues.forEach(revenueList::add);
        model.addAttribute("exportSuccess", exportSuccess);
        model.addAttribute("revenues", revenueList);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("pageSize", sizeOfPage);
        model.addAttribute("totalPage", revenues.getTotalPages() == 0 ? 1 : revenues.getTotalPages());

        List<PaymentTransaction> allRevenues = paymentService.getAllRevenues();
        // Lấy thời điểm hiện tại
        Calendar now = Calendar.getInstance();

        // Tạo 3 map thống kê theo tuần, tháng, năm
        Map<Integer, Long> weekStats = new HashMap<>();
        Map<Integer, Long> monthStats = new HashMap<>();
        Map<Integer, Long> yearStats = new HashMap<>();

        // Lấy năm đầu tiên có người dùng đăng ký
        int firstYear = now.get(Calendar.YEAR);
        for (PaymentTransaction revenue : allRevenues) {
            Date createdAt = revenue.getCreatedAt();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createdAt);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0
            int week = calendar.get(Calendar.WEEK_OF_YEAR);

            // Cập nhật năm đầu tiên có dữ liệu
            firstYear = Math.min(firstYear, year);

            // Thống kê theo tuần (7 tuần gần nhất)
            if (now.get(Calendar.WEEK_OF_YEAR) - week <= 6) {
                weekStats.put(week, weekStats.getOrDefault(week, 0L) + revenue.getAmount());
            }

            // Thống kê theo tháng (12 tháng gần nhất)
            if (now.get(Calendar.YEAR) == year) {
                monthStats.put(month, monthStats.getOrDefault(month, 0L) + revenue.getAmount());
            }

            // Thống kê theo năm (từ năm có người đăng ký đầu tiên đến hiện tại)
            yearStats.put(year, yearStats.getOrDefault(year, 0L) + revenue.getAmount());
        }

        // Chuyển Map thành JSON để dùng trong Thymeleaf
        ObjectMapper objectMapper = new ObjectMapper();
        model.addAttribute("weekStats", objectMapper.writeValueAsString(weekStats));
        model.addAttribute("monthStats", objectMapper.writeValueAsString(monthStats));
        model.addAttribute("yearStats", objectMapper.writeValueAsString(yearStats));
        return "admin/RevenueManagement";
    }

    @GetMapping("/UserManagement")
    public String userManagement(Authentication authentication, Model model,
                                 @RequestParam(name = "pageSize", defaultValue = "10") String sizeOfPage,
                                 @RequestParam(name = "exportSuccess", defaultValue = "false") String exportSuccess,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) throws JsonProcessingException {
        func_common(authentication, model);

        if (pageNo < 1) {
            pageNo = 1; // Đảm bảo pageNo luôn bắt đầu từ 1
        }

        Pageable pageable = PageRequest.of(pageNo - 1, Integer.parseInt(sizeOfPage));
        Page<UserDto> userPage = userService.getAllUserForAdmin(pageable);
        List<UserDto> userList = new LinkedList<>();
        userPage.forEach(userList::add);
        model.addAttribute("exportSuccess", exportSuccess);
        model.addAttribute("users", userList);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("pageSize", sizeOfPage);
        model.addAttribute("totalPage", userPage.getTotalPages() == 0 ? 1 : userPage.getTotalPages());

        List<User> users = UserDto.toUser(userService.getAllUser());
        // Lấy thời điểm hiện tại
        Calendar now = Calendar.getInstance();

        // Tạo 3 map thống kê theo tuần, tháng, năm
        Map<Integer, Long> weekStats = new HashMap<>();
        Map<Integer, Long> monthStats = new HashMap<>();
        Map<Integer, Long> yearStats = new HashMap<>();

        // Lấy năm đầu tiên có người dùng đăng ký
        int firstYear = now.get(Calendar.YEAR);
        for (User user : users) {
            Date createdAt = user.getCreatedAt();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createdAt);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0
            int week = calendar.get(Calendar.WEEK_OF_YEAR);

            // Cập nhật năm đầu tiên có dữ liệu
            firstYear = Math.min(firstYear, year);

            // Thống kê theo tuần (7 tuần gần nhất)
            if (now.get(Calendar.WEEK_OF_YEAR) - week <= 6) {
                weekStats.put(week, weekStats.getOrDefault(week, 0L) + 1);
            }

            // Thống kê theo tháng (12 tháng gần nhất)
            if (now.get(Calendar.YEAR) == year) {
                monthStats.put(month, monthStats.getOrDefault(month, 0L) + 1);
            }

            // Thống kê theo năm (từ năm có người đăng ký đầu tiên đến hiện tại)
            yearStats.put(year, yearStats.getOrDefault(year, 0L) + 1);
        }

        // Chuyển Map thành JSON để dùng trong Thymeleaf
        ObjectMapper objectMapper = new ObjectMapper();
        model.addAttribute("weekStats", objectMapper.writeValueAsString(weekStats));
        model.addAttribute("monthStats", objectMapper.writeValueAsString(monthStats));
        model.addAttribute("yearStats", objectMapper.writeValueAsString(yearStats));
        return "admin/UserManagement";
    }

    @PostMapping("/approveRoom")
    public String approveRoom(@RequestParam Long roomId, Authentication authentication, Model model) {
        func_common(authentication, model);
        roomService.approveRoom(roomId);
        return "redirect:/admin/RoomManagement";
    }

    @PostMapping("/disapproveRoom")
    public String disapproveRoom(@RequestParam Long roomId, Authentication authentication, Model model) {
        func_common(authentication, model);
        roomService.disapproveRoom(roomId);
        return "redirect:/admin/RoomManagement";
    }

    @GetMapping("/printUsers")
    public String printUsersToExcel() throws IOException {
        List<UserDto> dtos = userService.getAllUser();
        excelExport.exportUsersToExcel(dtos);
        return "redirect:/admin/UserManagement?exportSuccess=true";
    }

    @GetMapping("/printRooms")
    public String printRoomsToExcel() throws IOException {
        List<RoomDto> dtos = roomService.getAllRoom();
        excelExport.exportRoomsToExcel(dtos);
        return "redirect:/admin/RoomManagement?exportSuccess=true";
    }

    @GetMapping("/remove/room")
    public String removeRoom(@RequestParam(name = "roomId") String roomId) {
        roomService.deleteRoomByRoomId(Long.parseLong(roomId));
        return "redirect:/admin/RoomManagement";
    }
}