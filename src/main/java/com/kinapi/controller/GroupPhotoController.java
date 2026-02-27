package com.kinapi.controller;

import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.GroupPhotoService;
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
public class GroupPhotoController {
    private final GroupPhotoService groupPhotoService;

    @PostMapping("/upload-album-photos")
    public ResponseEntity<BaseResponse> uploadPhotosToAlbum(
            @RequestParam(name = "album_id") UUID albumId,
            @RequestParam(name = "files") MultipartFile[] files
    ){
        BaseResponse response = groupPhotoService.uploadPhotosToAlbum(albumId, files);
        return new ResponseEntity<>(response, response.code());
    }

    @GetMapping("/get-album-photos/{id}")
    public ResponseEntity<BaseResponse> getAlbumPhotos(
            @PathVariable UUID id
    ){
        BaseResponse response = groupPhotoService.getAlbumPhotos(id);
        return new ResponseEntity<>(response, response.code());
    }

    @DeleteMapping("/delete-album-photos")
    public ResponseEntity<BaseResponse> deleteAlbumPhotos(
            @RequestBody List<UUID> photoIds
    ){
        BaseResponse response = groupPhotoService.deleteAlbumPhotos(photoIds);
        return new ResponseEntity<>(response, response.code());
    }
}
