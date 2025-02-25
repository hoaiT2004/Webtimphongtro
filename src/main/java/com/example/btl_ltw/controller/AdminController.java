package com.example.btl_ltw.controller;

import com.example.btl_ltw.model.dto.UserDto;
import com.example.btl_ltw.model.dto.RoomDto;
import com.example.btl_ltw.service.RoomService;
import com.example.btl_ltw.service.UserService;
import com.example.btl_ltw.entity.Room;
//import com.example.btl_oop.model.dto.RoomDTO;
import com.example.btl_ltw.service.excel.ExcelExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.io.IOException;
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

    private static final int sizeOfPage = 5;


    private void func_common(Authentication authentication, Model model) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            String roleLength = userDetails.getAuthorities().toString();
            model.addAttribute("role", roleLength.substring(1, roleLength.length() - 1));
        }
    }

    @GetMapping("/RoomManagement")
    public String home(Authentication authentication, Model model,
                       @RequestParam(name = "exportSuccess", defaultValue = "false") String exportSuccess
            , @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        func_common(authentication, model);
        Pageable pageable = PageRequest.of(pageNo - 1, sizeOfPage);
        Page<Room> roomPage = roomService.getAllRoomsForAdmin(pageable);
        List<Room> roomList = new LinkedList<>();
        roomPage.forEach(roomList::add);
        model.addAttribute("rooms", RoomDto.toDto(roomList));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", roomPage.getTotalPages() == 0 ? 1 : roomPage.getTotalPages());
        model.addAttribute("exportSuccess", exportSuccess);
        return "admin/RoomManagement";
    }

    @GetMapping("/UserManagement")
    public String userManagement(Authentication authentication, Model model,
                                 @RequestParam(name = "exportSuccess", defaultValue = "false") String exportSuccess,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        func_common(authentication, model);

        if (pageNo < 1) {
            pageNo = 1; // Đảm bảo pageNo luôn bắt đầu từ 1
        }

        Pageable pageable = PageRequest.of(pageNo - 1, sizeOfPage);
        Page<UserDto> userPage = userService.getAllUserForAdmin(pageable);
        List<UserDto> userList = new LinkedList<>();
        userPage.forEach(userList::add);
        model.addAttribute("exportSuccess", exportSuccess);
        model.addAttribute("users", userList);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", userPage.getTotalPages() == 0 ? 1 : userPage.getTotalPages());


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
}