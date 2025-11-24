package com.kinapi.common.repository;

import com.kinapi.common.entity.GroupAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupAlbumRepository extends JpaRepository<GroupAlbum, UUID> {
}
