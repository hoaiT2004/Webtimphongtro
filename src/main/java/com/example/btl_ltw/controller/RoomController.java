package com.example.btl_ltw.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.example.btl_ltw.entity.Image;
import com.example.btl_ltw.entity.Room;
import com.example.btl_ltw.model.dto.CommentDto;
import com.example.btl_ltw.model.dto.UserDto;
import com.example.btl_ltw.model.request.AppointmentRequest;
import com.example.btl_ltw.model.dto.RoomDto;
import com.example.btl_ltw.repository.ImageRepository;
import com.example.btl_ltw.service.AppointmentService;
import com.example.btl_ltw.service.CommentService;
import org.springframework.data.domain.PageRequest;
import com.example.btl_ltw.service.RoomService;
import com.example.btl_ltw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("api/room")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;
  
    @Autowired
    private CommentService commentService;

    @Autowired
    private AppointmentService appointmentService;

    private static final int sizeOfPage = 5;
    @Autowired
    private ImageRepository imageRepository;

    @GetMapping
    public String showDetailRoom(Authentication auth,
                                 @RequestParam(name = "FavoriteRoomAddingError" , defaultValue = "false1") String error,
                                 @RequestParam(name = "commentId", defaultValue = "-4") Integer commentId,
                                 @RequestParam(name = "room_id") String room_id, Model model) {
        RoomDto roomDto = roomService.getInfoRoomByRoom_Id(room_id);
        List<String> imageDtos = roomService.getAllImagesByRoom_Id(room_id);
        model.addAttribute("usernameLandlord", userService.findUsernameById(roomDto.getUser_id()));
        model.addAttribute("room", roomDto);
        model.addAttribute("images", imageDtos);
        model.addAttribute("error", error);
        model.addAttribute("commentId", commentId);

        UserDto userDto = userService.getUserById(roomDto.getUser_id());
        model.addAttribute("userDto", userDto);

        List<CommentDto> commentDtos = commentService.getAllCommentsByRoom_id(Long.parseLong(room_id));
        model.addAttribute("comments", commentDtos);

        commonFunc(auth, model);
        return "room/details";
    }

    @GetMapping("/schedule")
    public String schedule(Authentication auth, Model model, @RequestParam(name = "room_id") String room_id) {
        commonFunc(auth, model);
        UserDto dto = userService.findUserByUsername(auth.getName());
        model.addAttribute("dto",dto);
        model.addAttribute("room_id", room_id);
        return "room/schedule";
    }

    @PostMapping("/schedule")
    public String createAppointment(@ModelAttribute AppointmentRequest request, Authentication auth, Model model) {
        commonFunc(auth, model);
        appointmentService.createAppointment(request);
        return "redirect:/api/home";
    }

    private static void commonFunc(Authentication auth, Model model) {
        if (auth != null) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            model.addAttribute("username", auth.getName());
            String roleLength = userDetails.getAuthorities().toString();
            model.addAttribute("role", roleLength.substring(1, roleLength.length() - 1));
        }
    }



    private static void commonFunc2(Model model, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, Page<Room> pages) {
        List<Room> dtoList = new ArrayList<>();
        pages.forEach(dtoList::add);
        model.addAttribute("rooms", RoomDto.toDto(dtoList));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", pages.getTotalPages() == 0 ? 1 : pages.getTotalPages());
    }
}
