package com.example.UberComp.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PageDTO<T> {
        private List<T> content;
        private int totalPages;
        private long totalElements;
        private int number;
        private int size;

        public PageDTO(Page<T> page) {
            this.content = page.getContent();
            this.totalPages = page.getTotalPages();
            this.totalElements = page.getTotalElements();
            this.number = page.getNumber();
            this.size = page.getSize();
        }

}
