package com.example.btl_ltw.controller;

import com.example.btl_ltw.entity.Room;
import com.example.btl_ltw.entity.User;
import com.example.btl_ltw.model.dto.RoomDto;
import com.example.btl_ltw.model.dto.UserDto;
import com.example.btl_ltw.service.FavoriteRoomService;
import com.example.btl_ltw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/room")
public class FavoriteRoomController {

    @Autowired
    private FavoriteRoomService favoriteRoomService;

    @Autowired
    private UserService userService;

    private static final int sizeOfPage = 5;

    @GetMapping("/favorites")
    public String showFavoriteRooms(Model model, Authentication auth
                                    , @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        func_common(auth, model);
        Pageable pageable = PageRequest.of(pageNo - 1, sizeOfPage);
        UserDto user = userService.findUserByUsername(auth.getName());
        Page<Room> roomPage = favoriteRoomService.getFavoriteRooms(pageable, user.getId());
        List<Room> roomList = new LinkedList<>();
        roomPage.forEach(roomList::add);
        model.addAttribute("rooms", RoomDto.toDto(roomList));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", roomPage.getTotalPages() == 0 ? 1 : roomPage.getTotalPages());
        return "room/favorite_rooms";
    }

    @GetMapping("/{roomId}/save")
    public String addFavoriteRoom(@PathVariable Long roomId, Authentication auth) {
        UserDto user = userService.findUserByUsername(auth.getName());
        favoriteRoomService.saveFavoriteRoom(user.getId(), roomId);
        return "redirect:/api/room?room_id=" + roomId+"&FavoriteRoomAddingError=false";
    }

    @PostMapping("/{roomId}/delete")
    public String removeFavoriteRoom(@PathVariable Long roomId, Authentication auth) {
        UserDto user = userService.findUserByUsername(auth.getName());
        favoriteRoomService.removeFavoriteRoom(user.getId(), roomId);
        return "redirect:/room/favorites";
    }



    private void func_common(Authentication authentication, Model model) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            String roleLength = userDetails.getAuthorities().toString();
            model.addAttribute("role", roleLength.substring(1, roleLength.length() - 1));
        }
    }
}

