package com.kinapi.controller;

import com.kinapi.common.dto.EditFamilyGroupAlbumDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.GroupAlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("kin-api")
@RequiredArgsConstructor
public class GroupAlbumController {
    private final GroupAlbumService groupAlbumService;

    @GetMapping("/get-group-album-list")
    public ResponseEntity<BaseResponse> getGroupAlbum(){
        BaseResponse response = groupAlbumService.getGroupAlbum();
        return new ResponseEntity<>(response, response.code());
    }

    @PostMapping("/add-group-album")
    public ResponseEntity<BaseResponse> addNewAlbum(
            @RequestParam(name = "album_name") String albumName
    ){
        BaseResponse response = groupAlbumService.addNewAlbum(albumName);
        return new ResponseEntity<>(response, response.code());
    }

    @PutMapping("/edit-group-album")
    public ResponseEntity<BaseResponse> editAlbum(
            @RequestBody EditFamilyGroupAlbumDto editFamilyGroupAlbumDto
    ){
        BaseResponse response = groupAlbumService.editAlbum(editFamilyGroupAlbumDto);
        return new ResponseEntity<>(response, response.code());
    }

    @PostMapping("/upload-album-photos")
    public ResponseEntity<BaseResponse> uploadPhotosToAlbum(
            @RequestParam(name = "album_id") UUID albumId,
            @RequestParam(name = "files") MultipartFile[] files
    ){
        BaseResponse response = groupAlbumService.uploadPhotosToAlbum(albumId, files);
        return new ResponseEntity<>(response, response.code());
    }

    @GetMapping("/get-album-photos/{id}")
    public ResponseEntity<BaseResponse> getAlbumPhotos(
            @PathVariable UUID id
    ){
        BaseResponse response = groupAlbumService.getAlbumPhotos(id);
        return new ResponseEntity<>(response, response.code());
    }

    @DeleteMapping("/delete-album-photos")
    public ResponseEntity<BaseResponse> deleteAlbumPhotos(
            @RequestBody List<UUID> photoIds
    ){
        BaseResponse response = groupAlbumService.deleteAlbumPhotos(photoIds);
        return new ResponseEntity<>(response, response.code());
    }

    @DeleteMapping("/delete-album/{albumId}")
    public ResponseEntity<BaseResponse> deleteAlbum(
            @PathVariable UUID albumId
    ){
        BaseResponse response = groupAlbumService.deleteAlbum(albumId);
        return new ResponseEntity<>(response, response.code());
    }
}