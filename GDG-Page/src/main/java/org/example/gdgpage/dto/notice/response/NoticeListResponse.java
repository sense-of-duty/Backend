package org.example.gdgpage.dto.notice.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NoticeListResponse {

    private Long id;
    private String title;
    private Long authorId;
    private Long partId;
    private int viewCount;
    private boolean isPinned;
    private LocalDateTime createdAt;
}