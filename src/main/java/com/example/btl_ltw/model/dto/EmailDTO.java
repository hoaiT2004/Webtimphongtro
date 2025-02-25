package com.example.btl_ltw.model.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class EmailDTO {
    private String to;
    private String subject;
    private String body;

}
