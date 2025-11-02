package com.bookshop.order_service.book;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FileResponse {
        Long id;
        Long bookId;
        String filename;
        String contentType;
        String filePath;
}