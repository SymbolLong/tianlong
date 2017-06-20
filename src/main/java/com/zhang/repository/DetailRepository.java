package com.zhang.repository;

import com.zhang.entity.Detail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by 致远 on 2017/6/15 0015.
 */
public interface DetailRepository extends CrudRepository<Detail, Long> {
}
