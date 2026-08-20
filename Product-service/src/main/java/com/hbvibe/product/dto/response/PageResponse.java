package com.hbvibe.product.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse <T> implements Serializable {
    private static final long serialVersionUID = 1L;
     int currentPage;
     int totalPages;
     int pageSize;
     long totalElements;
     List<T> items;
}
