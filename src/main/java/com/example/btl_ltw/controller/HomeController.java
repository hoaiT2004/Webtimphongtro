package com.example.btl_ltw.controller;

import com.example.btl_ltw.entity.Room;
import com.example.btl_ltw.model.request.room.RoomFilterDataRequest;
import com.example.btl_ltw.model.dto.RoomDto;
import com.example.btl_ltw.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import java.util.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("api/home")
public class HomeController {

    @Autowired
    private RoomService roomService;

    private static final int sizeOfPage = 5;

    private void func_common(Authentication authentication, Model model) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            String roleLength = userDetails.getAuthorities().toString();
            model.addAttribute("role", roleLength.substring(1, roleLength.length() - 1));
        }
    }

    @GetMapping("/priceMenu")
    public String showPriceMenu(Authentication authentication, Model model) {
        func_common(authentication, model);
        return "priceMenu";
    }

    @GetMapping
    public String normalRoom(Authentication authentication, Model model,
                       @ModelAttribute RoomFilterDataRequest request
            , @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        func_common(authentication, model);
        Pageable pageable = PageRequest.of(pageNo - 1, sizeOfPage);
        Page<Room> roomPage = roomService.getAllNormalRoomByManyContraints(request, pageable);
        List<Room> roomList = new LinkedList<>();
        roomPage.forEach(roomList::add);
        model.addAttribute("rooms", RoomDto.toDto(roomList));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", roomPage.getTotalPages() == 0 ? 1 : roomPage.getTotalPages());

        // Lưu trạng thái lọc khi chuyển trang
        model.addAttribute("request", request);
        return "index";
    }

    @GetMapping("/vipRoom")
    public String vipRoom(Authentication authentication, Model model,
                             @ModelAttribute RoomFilterDataRequest request
            , @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        func_common(authentication, model);
        Pageable pageable = PageRequest.of(pageNo - 1, sizeOfPage);
        Page<Room> roomPage = roomService.getAllVipRoomByManyContraints(request, pageable);
        List<Room> roomList = new LinkedList<>();
        roomPage.forEach(roomList::add);
        model.addAttribute("rooms", RoomDto.toDto(roomList));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", roomPage.getTotalPages() == 0 ? 1 : roomPage.getTotalPages());

        // Lưu trạng thái lọc khi chuyển trang
        model.addAttribute("request", request);
        return "vipRoom";
    }


    @GetMapping("/UserManagement")
    public String usermanagement() {
        return "UserManagement";
    }

    @GetMapping("/news_1")
    public String notice(Authentication authentication, Model model) {
        func_common(authentication, model);
        return "news_1";
    }

    @GetMapping("/news_2")
    public String dang_song(Authentication authentication, Model model) {
        func_common(authentication, model);
        return "news_2";
    }

    @GetMapping("/news_3")
    public String post1(Authentication authentication, Model model) {
        func_common(authentication, model);
        return "news_3";
    }

    @GetMapping("/news_4")
    public String post2(Authentication authentication, Model model) {
        func_common(authentication, model);
        return "news_4";
    }


}
