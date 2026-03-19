package com.recipes.controller;

import com.recipes.dto.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Value("${app.upload.path:uploads/}")
    private String uploadPath;

    @Value("${server.port:8080}")
    private String serverPort;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};

    @PostMapping("/upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "请选择要上传的文件");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.error(400, "文件大小不能超过 5MB");
        }
        String contentType = file.getContentType();
        boolean typeAllowed = false;
        if (contentType != null) {
            for (String type : ALLOWED_TYPES) {
                if (type.equals(contentType)) {
                    typeAllowed = true;
                    break;
                }
            }
        }
        if (!typeAllowed) {
            return Result.error(400, "仅支持 JPG、PNG、GIF、WebP 格式");
        }

        try {
            // 确保上传目录存在
            Path dirPath = Paths.get(uploadPath);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 生成唯一文件名
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
            Path filePath = dirPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            // 返回可访问的 URL
            String url = "/uploads/" + fileName;
            return Result.success(url);
        } catch (IOException e) {
            return Result.error(500, "文件上传失败：" + e.getMessage());
        }
    }
}
