package com.kinapi.service;

import com.kinapi.common.dto.AlbumPhotosDto;
import com.kinapi.common.dto.EditFamilyGroupAlbumDto;
import com.kinapi.common.dto.FamilyGroupAlbumDto;
import com.kinapi.common.entity.AlbumPhoto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.FamilyGroups;
import com.kinapi.common.entity.GroupAlbum;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.AlbumPhotoRepository;
import com.kinapi.common.repository.GroupAlbumRepository;
import com.kinapi.common.util.UserAuthHelper;
import com.kinapi.service.supabase.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupAlbumService {
    private final GroupAlbumRepository groupAlbumRepository;
    private final AlbumPhotoRepository albumPhotoRepository;
    private final StorageService storageService;

    public BaseResponse getGroupAlbum(){
        Users user = UserAuthHelper.getUser();
        FamilyGroups familyGroup = user.getFamilyGroups();

        List<GroupAlbum> groupAlbums = familyGroup.getGroupAlbums();
        List<FamilyGroupAlbumDto> response = groupAlbums.stream().filter(Objects::nonNull)
                .map(album -> FamilyGroupAlbumDto.builder()
                        .albumId(album.getId())
                        .albumName(album.getAlbumName())
                        .build())
                .toList();

        return BaseResponse.builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK)
                .message("Successfully retrieved group albums")
                .data(response)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public BaseResponse addNewAlbum(String albumName){
        try{
            Users user = UserAuthHelper.getUser();
            FamilyGroups familyGroup = user.getFamilyGroups();

            log.info("[addNewAlbum] Creating new group album for group: {}", familyGroup.getGroupName());

            GroupAlbum groupAlbum = GroupAlbum.builder()
                    .albumName(albumName)
                    .albumPhotos(List.of())
                    .familyGroups(familyGroup)
                    .build();

            groupAlbumRepository.save(groupAlbum);

            log.info("[addNewAlbum] Successfully added new album");
            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message("Successfully created new group album")
                    .build();

        } catch (Exception e) {
            log.error("[addNewAlbum] Error adding group album", e);
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Error adding group album: " + e.getMessage())
                    .build();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public BaseResponse editAlbum(EditFamilyGroupAlbumDto reqDto){
        try{
            GroupAlbum groupAlbum = groupAlbumRepository.findById(reqDto.getAlbumId())
                    .orElseThrow(() -> new Exception("Album not found"));

            groupAlbum.setAlbumName(reqDto.getAlbumName());
            groupAlbumRepository.save(groupAlbum);

            log.info("[editAlbum] Successfully edited album: {}", groupAlbum.getId());
            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message("Successfully edited album")
                    .build();

        } catch (Exception e) {
            log.error("[editAlbum] Error editing group album", e);
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Error editing group album: " + e.getMessage())
                    .build();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public BaseResponse uploadPhotosToAlbum(UUID albumId, MultipartFile[] files) {
        try {
            Users user = UserAuthHelper.getUser();
            FamilyGroups familyGroup = user.getFamilyGroups();

            GroupAlbum groupAlbum = groupAlbumRepository.findById(albumId)
                    .orElseThrow(() -> new Exception("Album not found"));

            log.info("[uploadPhotosToAlbum] Uploading {} files to album: {}", files.length, albumId);

            List<AlbumPhoto> uploadedPhotos = new ArrayList<>();

            for (MultipartFile file : files) {
                try {
                    // Upload file to Supabase Storage
                    String fileUrl = storageService.uploadGalleryAlbumFile(file, familyGroup.getId(), albumId);

                    // Create AlbumPhoto record
                    AlbumPhoto albumPhoto = AlbumPhoto.builder()
                            .groupAlbum(groupAlbum)
                            .fileUrl(fileUrl)
                            .uploadedBy(user.getName())
                            .build();

                    albumPhotoRepository.save(albumPhoto);
                    uploadedPhotos.add(albumPhoto);

                    log.info("[uploadPhotosToAlbum] Successfully uploaded file: {}", fileUrl);
                } catch (Exception e) {
                    log.error("[uploadPhotosToAlbum] Failed to upload file: {}", file.getOriginalFilename(), e);
                }
            }

            log.info("[uploadPhotosToAlbum] Successfully uploaded {} out of {} files", uploadedPhotos.size(), files.length);

            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message(String.format("Successfully uploaded %d out of %d files", uploadedPhotos.size(), files.length))
                    .data(uploadedPhotos.stream()
                            .map(photo -> photo.getFileUrl())
                            .toList())
                    .build();

        } catch (Exception e) {
            log.error("[uploadPhotosToAlbum] Error uploading photos to album", e);
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Error uploading photos: " + e.getMessage())
                    .build();
        }
    }

    public BaseResponse getAlbumPhotos(UUID id) {
        try {
            GroupAlbum groupAlbum = groupAlbumRepository.findById(id)
                    .orElseThrow(() -> new Exception("Album not found"));

            log.info("[getAlbumPhotos] Retrieving photos for album: {}", id);

            List<AlbumPhoto> albumPhotos = albumPhotoRepository.findByGroupAlbum_IdOrderByCreatedAtDesc(id);

            List<AlbumPhotosDto> response = albumPhotos.stream()
                    .filter(Objects::nonNull)
                    .map(p -> AlbumPhotosDto.builder()
                            .photoId(p.getId())
                            .fileUrl(p.getFileUrl())
                            .uploadedBy(p.getUploadedBy())
                            .createdAt(p.getCreatedAt())
                            .build()
                    ).toList();

            log.info("[getAlbumPhotos] Found {} photos in album: {}", response.size(), id);

            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message(String.format("Successfully retrieved %d photos from album '%s'",
                            response.size(), groupAlbum.getAlbumName()))
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("[getAlbumPhotos] Error retrieving photos for album: {}", id, e);
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Error retrieving album photos: " + e.getMessage())
                    .build();
        }
    }
}
