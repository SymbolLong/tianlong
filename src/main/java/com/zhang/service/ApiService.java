package com.zhang.service;

import com.zhang.entity.Category;
import com.zhang.entity.Channel;
import com.zhang.entity.Detail;
import com.zhang.entity.Picture;
import com.zhang.repository.CategoryRepository;
import com.zhang.repository.ChannelRepository;
import com.zhang.repository.DetailRepository;
import com.zhang.repository.PictureRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 致远 on 2017/6/15 0015.
 */
@Service
public class ApiService {
    @Resource
    private CategoryRepository categoryRepository;
    @Resource
    private ChannelRepository channelRepository;
    @Resource
    private DetailRepository detailRepository;
    @Resource
    private PictureRepository pictureRepository;

    public List<Category> getCategories(){
        return (List<Category>) categoryRepository.findAll();
    }

    public List<Channel> getChannelsById(Long id){
        if (id == null){
            return (List<Channel>) channelRepository.findAll();
        }else{
            return channelRepository.findChannelsByCategoryId(id);
        }
    }

    public Detail getDetailById(Long id){
        Detail detail = detailRepository.findOne(id);
        List<Picture> pictures = pictureRepository.findPicturesByDetailId(id);
        detail.setPictures(pictures);
        return detail;
    }
}
