package com.example.btl_ltw.controller;

import com.example.btl_ltw.entity.Appointment;
import com.example.btl_ltw.entity.Image;
import com.example.btl_ltw.entity.PaymentTransaction;
import com.example.btl_ltw.entity.Room;
import com.example.btl_ltw.model.dto.AppointmentDto;
import com.example.btl_ltw.model.dto.PaymentDto;
import com.example.btl_ltw.model.dto.RoomDto;
import com.example.btl_ltw.model.request.room.RoomFilterDataRequest;
import com.example.btl_ltw.repository.ImageRepository;
import com.example.btl_ltw.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/landlord")
public class LandlordController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private FileService fileService;

    private static final int sizeOfPage = 5;

    @Autowired
    private ImageRepository imageRepository;

    @GetMapping("/delete")
    public String deteteRoom(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                             @RequestParam(name = "roomId") Long roomId) throws ParseException {
        roomService.deleteRoomByRoomId(roomId);
        return "redirect:/landlord/manage?pageNo="+pageNo;
    }

    @GetMapping("/transaction")
    public String transactionHistory(Model model, Authentication auth,
                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        commonFunc(auth, model);
        Pageable pageable = PageRequest.of(pageNo - 1, sizeOfPage);
        Page<PaymentDto> pages = paymentService.getAllRevenueForLandlord(auth.getName(), pageable);
        List<PaymentDto> transactions = new ArrayList<>();
        pages.forEach(transactions::add);

        model.addAttribute("transactions", transactions);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", pages.getTotalPages() == 0 ? 1 : pages.getTotalPages());

        return "landlord/transaction_history";
    }

    @GetMapping("/manage") //api/room/manage
    public String showOwnedRooms(Model model, Authentication auth,
                                 @ModelAttribute RoomFilterDataRequest request,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) throws ParseException  {
        commonFunc(auth, model);
        String username = auth.getName();
        Pageable pageable = PageRequest.of(pageNo - 1, sizeOfPage);
        Page<Room> pages = roomService.getAllRoomByManyContraintsAndUsername(request, username, pageable);
        List<Room> li = new ArrayList<>();
        pages.forEach(li::add);
        commonFunc2(model, pageNo, pages);

        model.addAttribute("request", request);
        model.addAttribute("rooms", RoomDto.toDto(li));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", pages.getTotalPages() == 0 ? 1 : pages.getTotalPages());

        return "landlord/manage_room";
    }

    @PostMapping("/add")
    public String addRoom(@ModelAttribute RoomDto roomDto, HttpSession session, Model model, Authentication auth, @RequestParam(value = "images", required = false) List<MultipartFile> images ) throws ExecutionException, InterruptedException {
        commonFunc(auth, model);
        //Lưu dữ liệu vào session để lấy ra lưu vào redis
        session.setAttribute("room", roomDto);
        List<String> imageList = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            CompletableFuture<String> futureUrl = fileService.uploadFile(images.get(i));
            String str = futureUrl.get();
            imageList.add(str);
        }
        session.setAttribute("images", imageList);
        session.setAttribute("username", auth.getName());

        return "landlord/addroom2";
    }

    @GetMapping("/add")
    public String showAddRoomForm(@ModelAttribute RoomDto roomDto, Model model, Authentication auth) {
        commonFunc(auth, model);
        model.addAttribute("room", roomDto);
        return "landlord/addroom";
    }

    @GetMapping("/addFinish")
    public String showOption(HttpSession session, Authentication auth) {
        if (session.getAttribute("room") instanceof RoomDto) {
            RoomDto roomDto = (RoomDto) session.getAttribute("room");
            if (session.getAttribute("images") instanceof List<?>) {
                List<String> images = (List<String>) session.getAttribute("images");
                roomService.addRoom(roomDto, images, auth.getName());
            }
        }
        return "redirect:/landlord/add";
    }

    @PostMapping("/update")
    public String updateRoom(@ModelAttribute RoomDto roomDto, Model model, Authentication auth,
                             @RequestParam(value = "images", required = false) List<MultipartFile> imagesAdd,
                             @RequestParam(value = "imageIdsDel", required = false) List<Long> imageIdsDel) throws ExecutionException, InterruptedException {
        commonFunc(auth, model);
        roomService.updateRoom(roomDto,auth, imagesAdd, imageIdsDel);
        roomService.updateRoom(roomDto,auth, imagesAdd, imageIdsDel);
        return "redirect:/landlord/manage";
    }


    @GetMapping("/update")
    public String showUpdateRoomForm(@ModelAttribute RoomDto roomDto, Model model, Authentication auth,
                                          @RequestParam(name = "roomId") Long roomId) {
        commonFunc(auth, model);
        roomDto = roomService.getInfoRoomByRoom_Id(String.valueOf(roomId));
        List<Image> images = imageRepository.findAllImagesEntityByRoomId(roomId);
        model.addAttribute("images", images);
        model.addAttribute("room", roomDto);
        return "landlord/updateroom";
    }

    private static void commonFunc(Authentication auth, Model model) {
        if (auth != null) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            model.addAttribute("username", auth.getName());
            String roleLength = userDetails.getAuthorities().toString();
            model.addAttribute("role", roleLength.substring(1, roleLength.length() - 1));
        }
    }

    @GetMapping("/appointment")
    public String showAppointment(Authentication auth, Model model,
                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                  @RequestParam(name = "isApproval", defaultValue = "ok") String isApproval) throws ParseException {
        commonFunc(auth, model);
        Pageable pageable = PageRequest.of(pageNo - 1, sizeOfPage);
        Page<Appointment> pages = null;
        if (!isApproval.equals("ok")) {
            pages = appointmentService.getAppointmentsByUsername(isApproval, auth.getName(), pageable);
        } else {
            pages = appointmentService.getAllAppointmentsByUsername(auth.getName(), pageable);
        }
        commonFunc(model, pageNo, pages);
        model.addAttribute("isApproval", isApproval);
        return "landlord/viewingAppointment";
    }

    @GetMapping("/permitAppointment")
    public String showAppointment(@RequestParam(name="appointmentId") String appointmentId,
                                  @RequestParam(name="pageNo", defaultValue = "1") Integer pageNo) {
        appointmentService.permitAppointment(Long.parseLong(appointmentId));
        return "redirect:/landlord/appointment?isApproval=false&pageNo="+pageNo;
    }

    private void commonFunc(Model model, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, Page<Appointment> pages) {
        List<Appointment> dtoList = new ArrayList<>();
        pages.forEach(dtoList::add);
        model.addAttribute("Appointments", AppointmentDto.toDto(dtoList));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", pages.getTotalPages() == 0 ? 1 : pages.getTotalPages());
    }

    private static void commonFunc2(Model model, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, Page<Room> pages) {
        List<Room> dtoList = new ArrayList<>();
        pages.forEach(dtoList::add);
        model.addAttribute("rooms", RoomDto.toDto(dtoList));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", pages.getTotalPages() == 0 ? 1 : pages.getTotalPages());
    }
}
