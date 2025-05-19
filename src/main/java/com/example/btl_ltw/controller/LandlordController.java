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
                                     @RequestParam(name = "pageSize", defaultValue = "10") String sizeOfPage,
                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        commonFunc(auth, model);
        Pageable pageable = PageRequest.of(pageNo - 1, Integer.parseInt(sizeOfPage));
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
                                 @RequestParam(name = "pageSize", defaultValue = "10") String sizeOfPage,
                                 @ModelAttribute RoomFilterDataRequest request,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) throws ParseException  {
        commonFunc(auth, model);
        String username = auth.getName();
        Pageable pageable = PageRequest.of(pageNo - 1, Integer.parseInt(sizeOfPage));
        Page<Room> pages = roomService.getAllRoomByManyContraintsAndUsername(request, username, pageable);
        List<Room> li = new ArrayList<>();
        pages.forEach(li::add);
        commonFunc2(model, pageNo, pages, sizeOfPage);

        model.addAttribute("request", request);
        model.addAttribute("rooms", RoomDto.toDto(li));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", pages.getTotalPages() == 0 ? 1 : pages.getTotalPages());

        return "landlord/manage_room";
    }

    @GetMapping("/add")
    public String showAddRoomForm(@ModelAttribute RoomDto roomDto, Model model, Authentication auth) {
        commonFunc(auth, model);
        model.addAttribute("room", roomDto);
        return "landlord/addroom";
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

    @GetMapping("/upgradeRoom")
    public String upgradeRoom(Authentication auth, Model model, @RequestParam(name = "roomID") Long roomId, HttpSession session) {
        commonFunc(auth, model);
        session.setAttribute("roomID", roomId);
        session.setAttribute("username", auth.getName());
        return "landlord/upgradeStatus";
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

    @PostMapping("/update")
    public String updateRoom(@ModelAttribute RoomDto roomDto, Model model, Authentication auth,
                             @RequestParam(value = "images", required = false) List<MultipartFile> imagesAdd,
                             @RequestParam(value = "imageIdsDel", required = false) List<Long> imageIdsDel) throws ExecutionException, InterruptedException {
        commonFunc(auth, model);
        roomService.updateRoom(roomDto, imagesAdd, imageIdsDel);
        return "redirect:/landlord/manage";
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
                                  @RequestParam(name = "pageSize", defaultValue = "10") String sizeOfPage) throws ParseException {
        commonFunc(auth, model);
        Pageable pageable = PageRequest.of(pageNo - 1, Integer.parseInt(sizeOfPage));
        Page<Appointment> pages = appointmentService.getAllAppointmentsByUsername(auth.getName(), pageable);
        commonFunc(model, sizeOfPage, pageNo, pages);
        return "landlord/viewingAppointment";
    }

    @GetMapping("/permitAppointment")
    public String showAppointment(@RequestParam(name="appointmentId") String appointmentId,
                                  @RequestParam(name="pageNo", defaultValue = "1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize) {
        appointmentService.permitAppointment(Long.parseLong(appointmentId));
        return "redirect:/landlord/appointment?pageNo="+pageNo+"&pageSize=" + pageSize;
    }

    private void commonFunc(Model model,@RequestParam(name = "pageSize", defaultValue = "10") String sizeOfPage, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, Page<Appointment> pages) {
        List<Appointment> dtoList = new ArrayList<>();
        pages.forEach(dtoList::add);
        model.addAttribute("Appointments", AppointmentDto.toDto(dtoList));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("pageSize", sizeOfPage);
        model.addAttribute("totalPage", pages.getTotalPages() == 0 ? 1 : pages.getTotalPages());
    }

    private static void commonFunc2(Model model, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, Page<Room> pages, @RequestParam(name = "pageSize", defaultValue = "10") String sizeOfPage) {
        List<Room> dtoList = new ArrayList<>();
        pages.forEach(dtoList::add);
        model.addAttribute("rooms", RoomDto.toDto(dtoList));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("pageSize", sizeOfPage);
        model.addAttribute("totalPage", pages.getTotalPages() == 0 ? 1 : pages.getTotalPages());
    }
}
