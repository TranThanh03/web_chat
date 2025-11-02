package com.example.chat.service;

import com.example.chat.dto.request.attachment.TempAttachmentCreationRequest;
import com.example.chat.entity.TempAttachment;
import com.example.chat.repository.TempAttachmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TempAttachmentService {
    TempAttachmentRepository tempAttachmentRepository;

    public void create(TempAttachmentCreationRequest request) {
        TempAttachment tempAttachment = new TempAttachment();

        tempAttachment.setPublicId(request.getPublicId());
        tempAttachment.setUserId(request.getUserId());
        tempAttachment.setConversationId(request.getConversationId());
        tempAttachment.setCreatedAt(Instant.now());
        tempAttachment.setExpireAt(Instant.now().plusSeconds(24 * 3600));

        tempAttachmentRepository.save(tempAttachment);
    }

    public Boolean isValid(String publicId, String userId, String conversationId) {
        return tempAttachmentRepository.existsByPublicIdAndUserIdAndConversationId(publicId, userId, conversationId);
    }

    public void deleteByPublicId(String publicId) {
        tempAttachmentRepository.deleteByPublicId(publicId);
    }
}