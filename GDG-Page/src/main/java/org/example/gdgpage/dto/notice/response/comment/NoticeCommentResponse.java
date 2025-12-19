package org.example.gdgpage.dto.notice.response.comment;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NoticeCommentResponse {

    private Long id;
    private String content;
    private Long authorId;
    private boolean isAnonymous;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<NoticeCommentResponse> children;

}