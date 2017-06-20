package com.zhang.controller;

import com.zhang.entity.Category;
import com.zhang.entity.Channel;
import com.zhang.entity.Detail;
import com.zhang.entity.Picture;
import com.zhang.response.BaseResponse;
import com.zhang.service.ApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 致远 on 2017/6/15 0015.
 */
@RestController
@RequestMapping(value = "api")
public class ApiController {

    @Resource
    private ApiService apiService;
    @Value("${tianlong.pre.url}")
    private String preUrl;

    @RequestMapping(value = "category")
    public BaseResponse getCategories() {
        BaseResponse response = new BaseResponse();
        List<Category> categories = apiService.getCategories();
        response.setSuccess(true);
        response.setTotal(categories.size());
        response.setData(categories);
        response.setPreUrl(preUrl);
        return response;
    }

    @RequestMapping(value = "channel")
    public BaseResponse getList(@RequestParam(required = false) Long id) {
        BaseResponse response = new BaseResponse();
        List<Channel> channels = apiService.getChannelsById(id);
        response.setSuccess(true);
        response.setTotal(channels.size());
        response.setPreUrl(preUrl);
        response.setData(channels);
        return response;
    }

    @RequestMapping(value = "detail")
    public BaseResponse getDetail(Long id){
        BaseResponse response = new BaseResponse();
        Detail detail = apiService.getDetailById(id);
        response.setSuccess(true);
        response.setTotal(detail.getPictures().size());
        response.setData(detail);
        response.setPreUrl(preUrl);
        return response;
    }


}
