package com.doo.aqqle.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "create")
public class CrawlerDto {
    private String keyword;
}
