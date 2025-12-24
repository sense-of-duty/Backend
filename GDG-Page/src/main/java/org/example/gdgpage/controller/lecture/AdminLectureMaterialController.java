package org.example.gdgpage.controller.lecture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.dto.lecture.request.CreateLectureMaterialRequest;
import org.example.gdgpage.dto.lecture.request.UpdateLectureMaterialRequest;
import org.example.gdgpage.dto.lecture.response.LectureMaterialResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.service.lecture.LectureMaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/lectures")
public class AdminLectureMaterialController {

    private final LectureMaterialService lectureMaterialService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Operation(summary = "강의자료 작성(관리자)", description = "multipart/form-data로 JSON(request) + 파일(file) 업로드")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "작성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<LectureMaterialResponse> create(@RequestPart("request") String requestJson,
                                                          @RequestPart(value = "file", required = false) MultipartFile file) {
        CreateLectureMaterialRequest request = parseAndValidate(requestJson, CreateLectureMaterialRequest.class);
        LectureMaterialResponse created = lectureMaterialService.create(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "강의자료 수정(관리자)", description = "제목/공개일 수정 + 첨부파일 교체 가능")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "강의자료 없음")
    })
    @PatchMapping(value = "/{lectureId}", consumes = "multipart/form-data")
    public ResponseEntity<Void> update(@PathVariable Long lectureId,
                                       @RequestPart("request") String requestJson,
                                       @RequestPart(value = "file", required = false) MultipartFile file) {
        UpdateLectureMaterialRequest request = parseAndValidate(requestJson, UpdateLectureMaterialRequest.class);
        lectureMaterialService.updateMaterial(lectureId, request, file);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강의자료 삭제(관리자)", description = "소프트 삭제 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "강의자료 없음")
    })
    @DeleteMapping("/{lectureId}")
    public ResponseEntity<Void> delete(@PathVariable Long lectureId) {
        lectureMaterialService.deleteMaterial(lectureId);
        return ResponseEntity.noContent().build();
    }

    private <T> T parseAndValidate(String json, Class<T> clazz) {
        try {
            T dto = objectMapper.readValue(json, clazz);

            BindingResult errors = new BeanPropertyBindingResult(dto, clazz.getSimpleName());
            validator.validate(dto, errors);
            if (errors.hasErrors()) {
                throw new BadRequestException(ErrorMessage.INVALID_LECTURE_INPUT);
            }
            return dto;
        } catch (JsonProcessingException e) {
            throw new BadRequestException(ErrorMessage.INVALID_LECTURE_INPUT);
        }
    }
}
