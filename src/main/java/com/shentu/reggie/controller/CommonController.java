/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/6/22
 * Time: 8:18
 * Describe:
 */

package com.shentu.reggie.controller;

import com.shentu.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info("上传的文件:{}", file.toString());
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString();
        fileName += originalFilename.substring(originalFilename.lastIndexOf("."));
        // 创建一个目录对象
        File dir = new File(basePath);
        // 判断目录是否存在,不存在则创建
        if (!dir.exists()) {
            // 创建目录
            dir.mkdirs();
        }
        try {
            // 将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            // 输入流读取文件内容
            FileInputStream fis = new FileInputStream(new File(basePath + name));
            //输出流将文件写回浏览器
            ServletOutputStream fos = response.getOutputStream();

            response.setContentType("image/jpeg");

            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fis.read(bytes)) != -1){
                fos.write(bytes,0,len);
                fos.flush();
            }
            fis.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
