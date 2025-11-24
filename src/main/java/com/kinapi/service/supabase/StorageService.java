package com.kinapi.service.supabase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class StorageService {

    private final String supabaseUrl;
    private final String userProfilesBucket;
    private final String galleryAlbumBucket;
    private final WebClient webClient;

    public StorageService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.api-key}") String apiKey,
            @Value("${supabase.storage.bucket.user-profiles}") String userProfilesBucket,
            @Value("${supabase.storage.bucket.gallery-album}") String galleryAlbumBucket
    ) {
        this.supabaseUrl = supabaseUrl;
        this.userProfilesBucket = userProfilesBucket;
        this.galleryAlbumBucket = galleryAlbumBucket;
        this.webClient = WebClient.builder()
                .baseUrl(supabaseUrl + "/storage/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    /**
     * Upload user profile picture to Supabase Storage
     * @param file MultipartFile from the request
     * @param userId User's UUID
     * @return Public URL of the uploaded file
     */
    public String uploadUserProfile(MultipartFile file, UUID userId) throws IOException {
        validateFile(file);

        String fileExtension = getFileExtension(file.getOriginalFilename());
        String filePath = userId + "-profile." + fileExtension;

        byte[] fileBytes = file.getBytes();

        String uploadPath = String.format("/object/%s/%s", userProfilesBucket, filePath);

        try {
            webClient.post()
                    .uri(uploadPath)
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .body(BodyInserters.fromValue(fileBytes))
                    .retrieve()
                    .onStatus(
                            status -> status.value() != 200,
                            response -> response.bodyToMono(String.class).flatMap(body -> {
                                log.error("Supabase upload failed: {}", body);
                                return Mono.error(new RuntimeException("Failed to upload file to Supabase: " + body));
                            })
                    )
                    .bodyToMono(String.class)
                    .block();

            String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, userProfilesBucket, filePath);

            log.info("Successfully uploaded file to Supabase: {}", publicUrl);
            return publicUrl;

        } catch (Exception e) {
            log.error("Error uploading file to Supabase", e);
            throw new IOException("Failed to upload file to storage", e);
        }
    }

    /**
     * Upload gallery album photo/video to Supabase Storage
     * @param file MultipartFile from the request
     * @param familyGroupId Family Group UUID
     * @param groupAlbumId Group Album UUID
     * @return Public URL of the uploaded file
     */
    public String uploadGalleryAlbumFile(MultipartFile file, UUID familyGroupId, UUID groupAlbumId) throws IOException {
        validateGalleryFile(file);

        String fileExtension = getFileExtension(file.getOriginalFilename());
        String uniqueFilename = UUID.randomUUID() + "." + fileExtension;
        String filePath = familyGroupId + "/" + groupAlbumId + "/" + uniqueFilename;

        byte[] fileBytes = file.getBytes();

        String uploadPath = String.format("/object/%s/%s", galleryAlbumBucket, filePath);

        try {
            webClient.post()
                    .uri(uploadPath)
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .body(BodyInserters.fromValue(fileBytes))
                    .retrieve()
                    .onStatus(
                            status -> status.value() != 200,
                            response -> response.bodyToMono(String.class).flatMap(body -> {
                                log.error("Supabase upload failed: {}", body);
                                return Mono.error(new RuntimeException("Failed to upload file to Supabase: " + body));
                            })
                    )
                    .bodyToMono(String.class)
                    .block();

            String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, galleryAlbumBucket, filePath);

            log.info("Successfully uploaded gallery file to Supabase: {}", publicUrl);
            return publicUrl;

        } catch (Exception e) {
            log.error("Error uploading gallery file to Supabase", e);
            throw new IOException("Failed to upload gallery file to storage", e);
        }
    }

    /**
     * Delete gallery album file from Supabase Storage
     * @param fileUrl Full URL of the file to delete
     */
    public void deleteGalleryAlbumFile(String fileUrl) {
        try {
            String prefix = supabaseUrl + "/storage/v1/object/public/" + galleryAlbumBucket + "/";

            if (!fileUrl.startsWith(prefix)) {
                log.error("[deleteGalleryAlbumFile] Invalid file URL format. Expected prefix: {}, Got: {}", prefix, fileUrl);
                return;
            }

            String filePath = fileUrl.substring(prefix.length());
            String deletePath = String.format("/object/%s/%s", galleryAlbumBucket, filePath);

            log.info("[deleteGalleryAlbumFile] Attempting to delete file: {} (Path: {})", fileUrl, deletePath);

            String response = webClient.delete()
                    .uri(deletePath)
                    .retrieve()
                    .onStatus(
                            status -> !status.is2xxSuccessful(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("[deleteGalleryAlbumFile] Supabase delete failed with status {}: {}",
                                                clientResponse.statusCode(), body);
                                        return Mono.error(new RuntimeException("Failed to delete file: " + body));
                                    })
                    )
                    .bodyToMono(String.class)
                    .block();

            log.info("[deleteGalleryAlbumFile] Successfully deleted gallery file from Supabase: {} (Response: {})", filePath, response);

        } catch (Exception e) {
            log.error("[deleteGalleryAlbumFile] Error deleting gallery file from Supabase: {}", fileUrl, e);
            throw new RuntimeException("Failed to delete file from storage: " + e.getMessage(), e);
        }
    }

    /**
     * Delete user profile picture from Supabase Storage
     * @param userId User's UUID
     * @param fileExtension File extension (jpg, png, etc.)
     */
    public void deleteUserProfile(UUID userId, String fileExtension) {
        String filePath = userId + "-profile." + fileExtension;
        String deletePath = String.format("/object/%s/%s", userProfilesBucket, filePath);

        try {
            webClient.delete()
                    .uri(deletePath)
                    .retrieve()
                    .onStatus(
                            status -> status.value() != 200,
                            response -> response.bodyToMono(String.class).flatMap(body -> {
                                log.warn("Supabase delete failed: {}", body);
                                return Mono.empty(); // Don't throw error if file doesn't exist
                            })
                    )
                    .bodyToMono(String.class)
                    .block();

            log.info("Successfully deleted file from Supabase: {}", filePath);

        } catch (Exception e) {
            log.warn("Error deleting file from Supabase (file may not exist): {}", e.getMessage());
        }
    }

    /**
     * Validate uploaded file for user profile
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        long maxSize = 2 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IOException("File size exceeds maximum limit of 2MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Only image files are allowed");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!isAllowedExtension(extension)) {
            throw new IOException("File type not allowed. Allowed types: jpg, jpeg, png");
        }
    }

    /**
     * Validate uploaded file for gallery album (supports images and videos)
     */
    private void validateGalleryFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        long maxSize = 50 * 1024 * 1024; // 50MB for videos
        if (file.getSize() > maxSize) {
            throw new IOException("File size exceeds maximum limit of 50MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
            throw new IOException("Only image and video files are allowed");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!isAllowedGalleryExtension(extension)) {
            throw new IOException("File type not allowed. Allowed types: jpg, jpeg, png, mp4, mov, avi");
        }
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) throws IOException {
        if (filename == null || !filename.contains(".")) {
            throw new IOException("Invalid filename");
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Check if file extension is allowed for user profile
     */
    private boolean isAllowedExtension(String extension) {
        return extension.matches("jpg|jpeg|png");
    }

    /**
     * Check if file extension is allowed for gallery album
     */
    private boolean isAllowedGalleryExtension(String extension) {
        return extension.matches("jpg|jpeg|png|mp4|mov|avi");
    }
}