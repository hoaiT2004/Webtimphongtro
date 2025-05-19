package com.example.btl_ltw.controller;

import java.util.List;
import com.example.btl_ltw.model.dto.CommentDto;
import com.example.btl_ltw.model.dto.UserDto;
import com.example.btl_ltw.model.request.AppointmentRequest;
import com.example.btl_ltw.model.dto.RoomDto;
import com.example.btl_ltw.repository.ImageRepository;
import com.example.btl_ltw.service.*;
import com.example.btl_ltw.service.Impl.FavoriteRoomServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private FavoriteRoomServiceImpl favoriteRoomService;

    @Autowired
    private RoomStarService roomStarService;

    @GetMapping
    public String showDetailRoom(Authentication auth,
                                 @RequestParam(name = "FavoriteRoomAddingError" , defaultValue = "false1") String error,
                                 @RequestParam(name = "commentId", defaultValue = "-4") Integer commentId,
                                 @RequestParam(name = "room_id") String room_id, Model model) {
        roomService.addView(Long.parseLong(room_id));
        RoomDto roomDto = roomService.getInfoRoomByRoom_Id(room_id);
        List<String> imageDtos = roomService.getAllImagesByRoom_Id(room_id);
        model.addAttribute("usernameLandlord", userService.findUsernameById(roomDto.getUser_id()));
        model.addAttribute("room", roomDto);
        model.addAttribute("images", imageDtos);
        model.addAttribute("error", error);
        model.addAttribute("commentId", commentId);
        model.addAttribute("totalAddFavorite", favoriteRoomService.getTotalAddFavoriteByRoomId(Long.parseLong(room_id)));

        UserDto userDto = userService.getUserById(roomDto.getUser_id());
        model.addAttribute("userDto", userDto);

        List<CommentDto> parentCommentDtos = commentService.getAllParentCommentsByRoom_id(Long.parseLong(room_id));
        List<CommentDto> subCommentDtos = commentService.getAllSubCommentsByRoom_id(Long.parseLong(room_id));
        model.addAttribute("parentComments", parentCommentDtos);
        model.addAttribute("subComments", subCommentDtos);

        //////// Đánh giá sao
        model.addAttribute("star1", roomStarService.StarTotal(Long.parseLong(room_id), 1));
        model.addAttribute("star2", roomStarService.StarTotal(Long.parseLong(room_id), 2));
        model.addAttribute("star3", roomStarService.StarTotal(Long.parseLong(room_id), 3));
        model.addAttribute("star4", roomStarService.StarTotal(Long.parseLong(room_id), 4));
        model.addAttribute("star5", roomStarService.StarTotal(Long.parseLong(room_id), 5));
        model.addAttribute("starAvarage", roomStarService.StarAverage(Long.parseLong(room_id)));


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

    @PostMapping("/addStar")
    public String addStar(@RequestParam(name = "roomId") String roomId, @RequestParam(name = "rating") String rating, Authentication authentication) {
        roomStarService.addStar(Long.parseLong(roomId), Long.parseLong(rating), authentication.getName());
        return "redirect:/api/room?room_id=" + roomId;
    }

    private static void commonFunc(Authentication auth, Model model) {
        if (auth != null) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            model.addAttribute("username", auth.getName());
            String roleLength = userDetails.getAuthorities().toString();
            model.addAttribute("role", roleLength.substring(1, roleLength.length() - 1));
        }
    }

}
