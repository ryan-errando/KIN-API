package com.kinapi.controller;

import com.kinapi.common.dto.EditFamilyGroupAlbumDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.GroupAlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}