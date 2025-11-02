package com.example.chat.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.chat.dto.request.attachment.UploadSignatureRequest;
import com.example.chat.dto.request.attachment.TempAttachmentCreationRequest;
import com.example.chat.dto.response.attachment.UploadSignatureItem;
import com.example.chat.dto.response.attachment.UploadSignatureResponse;
import com.example.chat.dto.response.message.MessageResponse;
import com.example.chat.entity.Attachment;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.mapper.ExternalApiMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AttachmentService {
    Cloudinary cloudinary;
    TempAttachmentService tempAttachmentService;
    MessageService messageService;
    ExternalApiMapper externalApiMapper;

    @NonFinal
    @Value("${cloudinary.folder}")
    private String FOLDER;

    private static final Set<String> RAW_EXTENSIONS = Set.of(
            ".doc", ".docx",
            ".xls", ".xlsx",
            ".ppt", ".pptx",
            ".txt", ".csv", ".rtf",
            ".zip", ".rar", ".7z", ".tar", ".gz",
            ".json", ".xml", ".yaml", ".yml", ".sql", ".log", ".md",
            ".apk", ".exe", ".bin", ".iso",
            ".psd", ".ai", ".sketch"
    );

    public UploadSignatureResponse generateUploadSignature(String userId, UploadSignatureRequest request) {
        var uploadItems = request.getItems();
        List<UploadSignatureItem> items = new ArrayList<>(request.getItems().size());
        long timestamp = Instant.now().getEpochSecond();
        LocalDate now = LocalDate.now();
        String folder = String.format("%s/%d/%02d/%02d",
                FOLDER,
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth()
        );

        for (var item : uploadItems) {
            String publicId = UUID.randomUUID().toString();

            Map<String, Object> paramsToSign = new HashMap<>();
            paramsToSign.put("timestamp", timestamp);
            paramsToSign.put("folder", folder);
            paramsToSign.put("public_id", publicId);
            paramsToSign.put(
                    "context",
                    "filename=" + URLEncoder.encode(item.getFileName(), StandardCharsets.UTF_8)
                            .replace("+", "%20")
            );

            String signature = cloudinary.apiSignRequest(
                    paramsToSign,
                    cloudinary.config.apiSecret
            );

            items.add(
                    UploadSignatureItem.builder()
                            .fileId(item.getFileId())
                            .publicId(publicId)
                            .signature(signature)
                            .build()
            );

            String extension = extractExtension(item.getFileName());
            tempAttachmentService.create(
                    TempAttachmentCreationRequest.builder()
                            .publicId(String.format("%s/%s%s", folder, publicId, extension))
                            .userId(userId)
                            .conversationId(request.getConversationId())
                            .build()
            );
        }

        return UploadSignatureResponse.builder()
                .apiKey(cloudinary.config.apiKey)
                .cloudName(cloudinary.config.cloudName)
                .folder(folder)
                .timeStamp(timestamp)
                .items(items)
                .build();
    }

    public List<MessageResponse> getMetadata(String conversationId, String senderId, List<String> publicIds) {
        List<String> validIds = publicIds.stream()
                .filter(publicId -> tempAttachmentService.isValid(publicId, senderId, conversationId))
                .toList();

        if (validIds.isEmpty()) {
            return List.of();
        }

        List<MessageResponse> responses = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();
        List<Map<String, Object>> resources = fetchMetadataForAllTypes(validIds);

        for (Map res : resources) {
            Attachment attachment = externalApiMapper.convertValue(res, Attachment.class);

            if ("image".equals(attachment.getResourceType()) && !"pdf".equalsIgnoreCase(attachment.getFormat())) {
                attachments.add(attachment);
            }
            else if (attachment.getResourceType().equals("image")) {
                responses.add(messageService.atachmentMessage(conversationId, senderId, "file", List.of(attachment)));
            } else {
                responses.add(messageService.atachmentMessage(conversationId, senderId, attachment.getResourceType(), List.of(attachment)));
            }

            tempAttachmentService.deleteByPublicId(attachment.getPublicId());
        }

        if (!attachments.isEmpty()) {
            responses.add(
                    messageService.atachmentMessage(conversationId, senderId, "image", attachments)
            );
        }

        return responses;
    }

    private Map tryDetectResource(String publicId) throws AppException {
        String[] types = {"image", "video", "raw"};

        for (String type : types) {
            try {
                return cloudinary.api().resource(
                        publicId,
                        ObjectUtils.asMap(
                                "resource_type", type,
                                "context", true
                        )
                );
            } catch (Exception ignored) {}
        }

        throw new AppException(ErrorCode.ATTACHMENT_METADATA_FETCH_FAILED);
    }

    private List<Map<String, Object>> fetchMetadataForAllTypes(List<String> publicIds) {
        List<Map<String, Object>> results = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();

        for (String publicId : publicIds) {
            try {
                results.add(tryDetectResource(publicId));
            } catch (Exception e) {
                log.error("Failed fetch metadata: ", e);
                failedIds.add(publicId);
            }
        }

        if (!failedIds.isEmpty()) {
            throw new AppException(
                    ErrorCode.ATTACHMENT_METADATA_FETCH_FAILED,
                    Map.of("failedIds", failedIds)
            );
        }

        return results;
    }

    private String extractExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        int lastDot = fileName.lastIndexOf(".");
        int lastSlash = Math.max(fileName.lastIndexOf("/"), fileName.lastIndexOf("\\"));

        if (lastDot > lastSlash) {
            String ext = fileName.substring(lastDot).toLowerCase();

            if (RAW_EXTENSIONS.contains(ext)) {
                return ext;
            }
        }

        return "";
    }
}
