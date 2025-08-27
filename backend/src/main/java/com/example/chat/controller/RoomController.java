package com.example.chat.controller;

import com.example.chat.dto.request.MessageRequest;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.entity.Message;
import com.example.chat.service.RoomService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin("http://localhost:5173")
public class RoomController {
    RoomService roomService;

    @PostMapping("/{code}")
    ResponseEntity<ApiResponse<String>> createRoom(@PathVariable String code) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1100)
                .message("Thêm phòng mới thành công.")
                .result(roomService.createRoom(code).getId())
                .build();

        return ResponseEntity.ok(apiResponse);
    }


}
