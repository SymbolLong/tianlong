package com.zhang.repository;

import com.zhang.entity.Picture;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by 致远 on 2017/6/15 0015.
 */
public interface PictureRepository extends CrudRepository<Picture, Long> {

    List<Picture> findPicturesByDetailId(Long detailId);
}
