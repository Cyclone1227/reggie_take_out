package com.zwt.reggie.controller;

import com.zwt.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

//文件上传和下载
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){

        log.info("照片上传中。。。");

        //原始文件名
        String originalFilename = file.getOriginalFilename();//a.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));  //后缀
        //使用UUID，避免出现相同的文件名称
        String fileName = UUID.randomUUID().toString()+suffix;
        //创建一个目录对象，判断是否存在
        File file1 = new File(basePath);
        if (!file1.exists()){
            //创建目录
            file1.mkdirs();
        }
        //将临时文件转存在指定位置
        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回菜品名称
        return R.success(fileName);
    }

    //通过输出流向浏览器页面写出数据
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            //输出流，将文件写会浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //设置文件类型
            response.setContentType("image/jpeg");

            int len =0;
            byte[] bytes = new byte[1024];
            //len为读取数据长度
            while ((len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
