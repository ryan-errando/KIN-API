package com.kinapi.service;

import com.kinapi.common.dto.EditFamilyGroupAlbumDto;
import com.kinapi.common.dto.FamilyGroupAlbumDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.FamilyGroups;
import com.kinapi.common.entity.GroupAlbum;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.GroupAlbumRepository;
import com.kinapi.common.util.UserAuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupAlbumService {
    private final GroupAlbumRepository groupAlbumRepository;

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

}
