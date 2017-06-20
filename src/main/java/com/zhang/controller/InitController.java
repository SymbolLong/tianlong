package com.zhang.controller;

import com.zhang.entity.Category;
import com.zhang.entity.Channel;
import com.zhang.entity.Detail;
import com.zhang.entity.Picture;
import com.zhang.repository.CategoryRepository;
import com.zhang.repository.ChannelRepository;
import com.zhang.repository.DetailRepository;
import com.zhang.repository.PictureRepository;
import com.zhang.util.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 致远 on 2017/6/15 0015.
 */
@RestController
@RequestMapping(value = "zsl")
public class InitController {

    @Resource
    private CategoryRepository categoryRepository;
    @Resource
    private ChannelRepository channelRepository;
    @Resource
    private DetailRepository detailRepository;
    @Resource
    private PictureRepository pictureRepository;

    @RequestMapping(value = "init")
    public String init(String type) {
        HttpUtil httpUtil = new HttpUtil();
        if (type.equalsIgnoreCase("category")) {
            categoryRepository.deleteAll();
            String url = "http://www.tngou.net/tnfs/api/classify";
            String categories = httpUtil.doGet(url);
            JSONObject json = JSONObject.fromObject(categories);
            JSONArray array = json.getJSONArray("tngou");
            for (int i = 0; i < array.size(); i++) {
                JSONObject tmp = array.getJSONObject(i);
                Category category = new Category();
                category.setName(tmp.getString("name"));
                categoryRepository.save(category);
            }
        } else if (type.equalsIgnoreCase("channel")) {
            String url = "http://www.tngou.net/tnfs/api/list";
            String channels = httpUtil.doGet(url);
            JSONObject json = JSONObject.fromObject(channels);
            int totalPage = json.getInt("total") / 20 + 1;
            JSONArray array = json.getJSONArray("tngou");
            if (!array.isEmpty()) {
                saveChannel(array);
            }
            for (int i = 2; i <= totalPage; i++) {
                String pageurl = url + "?page=" + i;
                System.out.println(pageurl);
                channels = httpUtil.doGet(pageurl);
                json = JSONObject.fromObject(channels);
                array = json.getJSONArray("tngou");
                if (!array.isEmpty()) {
                    saveChannel(array);
                }
            }
        } else if (type.equalsIgnoreCase("detail")) {
            Iterable<Channel> channels = channelRepository.findAll();
            List<String> list = new ArrayList<>();
            for (Channel channel : channels) {
                if (!list.contains(channel.getId() + "")) {
                    continue;
                }
                String url = "http://www.tngou.net/tnfs/api/show?id=" + channel.getOtherId();
                String pics = httpUtil.doGet(url);
                if (pics == null || pics.equalsIgnoreCase("")) {
                    try {
                        FileWriter fileWriter = new FileWriter("f://error.txt", true);
                        fileWriter.write("list.add(\"" + channel.getId() + "\");\r\n");
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                JSONObject json = JSONObject.fromObject(pics);
                Detail detail = new Detail();
                detail.setUrl(json.getString("img"));
                detail.setTimestamp((json.getLong("time") / 1000) + "");
                detail.setName(json.getString("title"));
                detail.setChannelId(channel.getId());
                detailRepository.save(detail);

                JSONArray array = json.getJSONArray("list");
                for (int i = 0; i < array.size(); i++) {
                    JSONObject tmp = array.getJSONObject(i);
                    Picture picture = new Picture();
                    picture.setDetailId(detail.getId());
                    picture.setUrl(tmp.getString("src"));
                    pictureRepository.save(picture);
                }
            }
        }else if(type.equalsIgnoreCase("saveImage")){
            String suffix = "/ext/161223/7083a1fde72448a62e477c5aab0721c8.jpg";
            saveImage(suffix);
        }
        System.out.println("Success");
        return "执行完毕";
    }

    private void saveChannel(JSONArray array) {
        for (int i = 0; i < array.size(); i++) {
            JSONObject tmp = array.getJSONObject(i);
            Channel channel = new Channel();
            channel.setOtherId(tmp.getLong("id"));
            channel.setCategoryId(tmp.getLong("galleryclass"));
            channel.setName(tmp.getString("title"));
            channel.setUrl(tmp.getString("img"));
            channel.setTimestamp((tmp.getLong("time") / 1000) + "");
            channel.setTotal(tmp.getInt("size"));
            channelRepository.save(channel);
        }
    }


    private void saveImage(String suffix) {
        try {
            String name = suffix.substring(suffix.lastIndexOf("/") + 1);
            String type = name.substring(name.lastIndexOf("."));
            name = name.substring(8, 24);
            File file = new File("F://tianlong/"+name+type);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            URL url = new URL("http://tnfs.tngou.net/image" + suffix);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent",
                    "User-Agent:Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50");
            connection.setRequestMethod("GET");
            int code = connection.getResponseCode();
            String contentType = connection.getHeaderField("Content-Type");
            long end = System.currentTimeMillis();

            if (code != 200 || !contentType.contains("image")) {
                System.out.println("something is wrong!");
            }
            byte[] data = IOUtils.toByteArray(connection.getInputStream());
            fileOutputStream.write(data, 0, data.length);
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
