package com.example.btl_ltw.model.dto;


import com.example.btl_ltw.entity.Image;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {

    private long id;

    private long room_id;

    private String url;

    public static ImageDto toDto(Image image) {
        if (image == null) {
            return null;
        }
        return ImageDto.builder()
                .id(image.getId())
                .room_id(image.getRoom_id())
                .url(image.getUrl())
                .build() ;
    }

    public static Image toImage(ImageDto imageDto) {
        if (imageDto == null) {
            return null;
        }
        return Image.builder()
                .id(imageDto.getId())
                .room_id(imageDto.getRoom_id())
                .url(imageDto.getUrl())
                .build() ;
    }

    public static List<ImageDto> toDto(List<Image> images) {
        return images.stream()
                .map(ImageDto::toDto)
                .collect(Collectors.toList());
    }

    public static List<Image> toImage(List<ImageDto> imageDtos) {
        return imageDtos.stream()
                .map(ImageDto::toImage)
                .collect(Collectors.toList());
    }
}
