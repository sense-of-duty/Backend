package org.example.gdgpage.dto.notice.request.post;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NoticeUpdateRequest {

    private String title;

    private String content;

    private boolean isPinned;

    private Long partId;
}