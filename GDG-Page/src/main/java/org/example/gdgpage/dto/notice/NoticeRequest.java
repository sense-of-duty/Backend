package org.example.gdgpage.dto.notice;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeRequest {


    @NotBlank(message = "제목을 입력해주세요.")
    private String title;


    @NotBlank(message = "내용을 입력해주세요.")
    private String content;


    private boolean isPinned;

//어느 파트 공지사항인지 보여주기
    private Long partId;
}