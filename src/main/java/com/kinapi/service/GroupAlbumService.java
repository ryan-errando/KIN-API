package com.kinapi.service;

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
        FamilyGroups familyGroup = user.getFamilyMembers().getGroup();

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
            FamilyGroups familyGroup = user.getFamilyMembers().getGroup();

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
    public BaseResponse deleteAlbum(UUID albumId) {
        try {
            GroupAlbum groupAlbum = groupAlbumRepository.findById(albumId)
                    .orElseThrow(() -> new Exception("Album not found"));

            String albumName = groupAlbum.getAlbumName();
            List<AlbumPhoto> albumPhotos = albumPhotoRepository.findByGroupAlbum_IdOrderByCreatedAtDesc(albumId);

            log.info("[deleteAlbum] Deleting album '{}' with {} photos", albumName, albumPhotos.size());

            int successCount = 0;
            int failCount = 0;

            for (AlbumPhoto albumPhoto : albumPhotos) {
                try {
                    String fileUrl = albumPhoto.getFileUrl();

                    storageService.deleteGalleryAlbumFile(fileUrl);

                    albumPhotoRepository.delete(albumPhoto);

                    successCount++;
                    log.info("[deleteAlbum] Successfully deleted photo: {}", albumPhoto.getId());

                } catch (Exception e) {
                    failCount++;
                    log.error("[deleteAlbum] Failed to delete photo: {}", albumPhoto.getId(), e);
                }
            }

            groupAlbumRepository.delete(groupAlbum);

            log.info("[deleteAlbum] Successfully deleted album '{}': {} photos deleted, {} failed",
                    albumName, successCount, failCount);

            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message(String.format("Successfully deleted album '%s' with %d out of %d photos",
                            albumName, successCount, albumPhotos.size()))
                    .build();

        } catch (Exception e) {
            log.error("[deleteAlbum] Error deleting album: {}", albumId, e);
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Error deleting album: " + e.getMessage())
                    .build();
        }
    }
}
