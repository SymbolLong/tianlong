package com.zhang.repository;

import com.zhang.entity.Channel;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by 致远 on 2017/6/15 0015.
 */
public interface ChannelRepository extends PagingAndSortingRepository<Channel, Long> {

    List<Channel> findChannelsByCategoryId(Long categoryId);
}
