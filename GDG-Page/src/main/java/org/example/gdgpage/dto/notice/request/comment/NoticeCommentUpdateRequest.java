package org.example.gdgpage.dto.notice.request.comment;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NoticeCommentUpdateRequest {

    private String content;

    private boolean isAnonymous;
}