package com.poetry.service;

import com.aliyun.oss.OSS;
import com.poetry.config.OssConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OssService {

    @Autowired(required = false)
    private OSS ossClient;

    @Autowired
    private OssConfig ossConfig;

    public String uploadFile(MultipartFile file) throws IOException {
        if (ossClient == null) {
            throw new IllegalStateException("OSS未配置，请检查application-dev.yml中的OSS配置");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String objectName = "poetry/" + datePath + "/" + UUID.randomUUID() + ext;

        ossClient.putObject(ossConfig.getBucketName(), objectName, file.getInputStream());

        return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + objectName;
    }
}
