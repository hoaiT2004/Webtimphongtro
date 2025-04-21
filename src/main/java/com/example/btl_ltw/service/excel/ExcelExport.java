package com.example.btl_ltw.service.excel;

import com.example.btl_ltw.common.RoleEnum;
import com.example.btl_ltw.model.dto.RoomDto;
import com.example.btl_ltw.model.dto.UserDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExport {
    public void exportUsersToExcel(List<UserDto> userList) throws IOException {
        // Tạo một workbook mới (XSSFWorkbook dùng cho định dạng .xlsx)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // Tạo header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("User ID");
        headerRow.createCell(1).setCellValue("Tên");
        headerRow.createCell(2).setCellValue("Số điện thoại");
        headerRow.createCell(3).setCellValue("Email");
        headerRow.createCell(4).setCellValue("Quyền hạn");

        // Tạo dữ liệu phòng
        int rowNum = 1;
        for (UserDto user : userList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getFullname());
            row.createCell(2).setCellValue(user.getTel());
            row.createCell(3).setCellValue(user.getEmail());
            row.createCell(4).setCellValue(RoleEnum.fromValue(Integer.parseInt(user.getRole_id()+""))+"");
        }

        // Ghi workbook vào file Excel
        try (FileOutputStream fileOut = new FileOutputStream("C:\\Users\\nguye\\Desktop\\users.xlsx")) {
            workbook.write(fileOut);
        }

    }
    public void exportRoomsToExcel(List<RoomDto> roomList) throws IOException {
        // Tạo một workbook mới (XSSFWorkbook dùng cho định dạng .xlsx)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Rooms");

        // Tạo header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Giá");
        headerRow.createCell(2).setCellValue("Địa Chỉ");
        headerRow.createCell(3).setCellValue("Diện Tích");
        headerRow.createCell(4).setCellValue("Kiểu Phòng");
        headerRow.createCell(5).setCellValue("Trạng thái");

        // Tạo dữ liệu phòng
        int rowNum = 1;
        for (RoomDto roomDto : roomList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(roomDto.getRoom_id());
            row.createCell(1).setCellValue(roomDto.getPrice());
            row.createCell(2).setCellValue(roomDto.getAddress());
            row.createCell(3).setCellValue(roomDto.getArea());
            row.createCell(4).setCellValue(roomDto.getRoomType());
            row.createCell(5).setCellValue(roomDto.getIsApproval());
        }

        // Ghi workbook vào file Excel
        try (FileOutputStream fileOut = new FileOutputStream("C:\\Users\\nguye\\Desktop\\rooms.xlsx")) {
            workbook.write(fileOut);
        }

    }
}
