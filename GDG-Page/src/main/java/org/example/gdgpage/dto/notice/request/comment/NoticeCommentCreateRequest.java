package org.example.gdgpage.dto.notice.request.comment;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NoticeCommentCreateRequest {

    private String content;

    private boolean isAnonymous;

    private Long parentId;

    private String postType;
}