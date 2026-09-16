package com.wjy.personal_blog.controllers.user;

import com.wjy.personal_blog.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/images")
@Slf4j
@Tag(name = "图片管理", description = "图片上传相关接口")
public class ImageController {


  //  private static final String UPLOAD_PATH = "classpath:/static/img";

    //在新增文章时添加图片，图片的上传路径
    @Value("${blog.upload.path}")
    private String UPLOAD_IMG_PATH;
    //= "D:/Self_Directory/Pictures/";



    /*
     * 上传图片到
     * */
    @PostMapping("/upload")
    @Operation(summary = "上传图片", description = "上传图片到服务器")
    public Result uploadImage(MultipartFile file) {
        log.info("开始上传图片");
        //判断上传的图片是否为空
        if (file == null) {
            return Result.error("图片为空");
        }

        //获取文件初始名字
        String originalFilename = file.getOriginalFilename();
        //判断文件名是否为空
        if (originalFilename == null || !originalFilename.contains(".")) {
            return Result.error("文件名不合法");
        }

        //获取后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 在获取 suffix 之后加入
        List<String> allowedSuffixes = List.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
        String lowerSuffix = suffix.toLowerCase();
        if (!allowedSuffixes.contains(lowerSuffix)) {
            return Result.error("仅支持 jpg/jpeg/png/gif/webp 格式");
        }
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
            log.error("上传失败", e);
           return Result.error("上传失败"+e.getMessage());
        }


    }


}
