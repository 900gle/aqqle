package com.doo.aqqle.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ExtractGoodsTextDTO {

    private Long id;
    private String keyword;
    private String name;
    private long price;
    private String brand;
    private String category;
    private String category1;
    private String category2;
    private String category3;
    private String category4;
    private String category5;
    private String image;
    private String featureVector;
    private String type;
    private float weight;
    private float popular;
    private String createdTime;
    private String updatedTime;

}
