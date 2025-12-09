package com.wjy.personal_blog.controllers.admin;

import com.wjy.personal_blog.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/images")
@Slf4j
public class ImageController {


    private static final String UPLOAD_PATH = "classpath:/static/img";

    //在新增文章时添加图片，图片的上传路径
    private static final String UPLOAD_IMG_PATH = "D:/Self_Directory/Pictures/";


    /*
     * 上传图片到
     * */
    @PostMapping("/upload")
    public Result uploadImage(MultipartFile file) {
        //判断上传的图片是否为空
        if (file == null) {
            return Result.error("图片为空");
        }

        //获取文件初始名字
        String originalFilename = file.getOriginalFilename();
        //获取后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //随机生成图片文件名称
        String imgName = UUID.randomUUID().toString() + suffix;

        //创建文件目录
        File dir = new File(UPLOAD_IMG_PATH);
        if (!dir.exists()) {
            dir.mkdirs();    //递归创建
        }

        //进行文件上传
        try {
            File dest = new File(UPLOAD_IMG_PATH + imgName);
            file.transferTo(dest);
            log.info("上传成功：{}", imgName);
            return Result.success(imgName);
        } catch (IOException e) {
           e.printStackTrace();
           return Result.error("上传失败"+e.getMessage());
        }


    }

    /*
    * 展示图片
    * */

}
