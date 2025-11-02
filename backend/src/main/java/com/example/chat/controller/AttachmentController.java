package com.example.chat.controller;

import com.example.chat.configuration.CustomSecurity;
import com.example.chat.dto.request.attachment.UploadSignatureRequest;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.dto.response.attachment.UploadSignatureResponse;
import com.example.chat.service.AttachmentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attachments")

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttachmentController {
    AttachmentService attachmentService;
    CustomSecurity customSecurity;

    @PostMapping("/signature")
    ResponseEntity<ApiResponse<UploadSignatureResponse>> getSignature(
            Authentication authentication,
            @RequestBody @Valid UploadSignatureRequest request
    ) {
        String userId = customSecurity.getUserId(authentication);

        ApiResponse<UploadSignatureResponse> apiResponse = ApiResponse.<UploadSignatureResponse>builder()
                .result(attachmentService.generateUploadSignature(userId, request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
