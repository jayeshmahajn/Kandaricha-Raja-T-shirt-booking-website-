package com.mandal.tshirt.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FileStorageService {

    private final Cloudinary cloudinary;

    public FileStorageService(@Value("${cloudinary.url:}") String cloudinaryUrl) {
        if (cloudinaryUrl != null && !cloudinaryUrl.isEmpty()) {
            this.cloudinary = new Cloudinary(cloudinaryUrl);
        } else {
            this.cloudinary = null;
        }
    }

    public String storeImage(MultipartFile file) throws IOException {
        if (cloudinary == null) throw new IOException("Cloudinary not configured. Set CLOUDINARY_URL.");
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("secure_url").toString();
    }

    public List<String> listImages() {
        if (cloudinary == null) return List.of();
        try {
            Map result = cloudinary.api().resources(ObjectUtils.asMap("type", "upload", "max_results", 100));
            List<Map> resources = (List<Map>) result.get("resources");
            List<String> urls = new ArrayList<>();
            for (Map res : resources) {
                urls.add(res.get("secure_url").toString());
            }
            return urls;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public boolean deleteImage(String urlOrPublicId) {
        if (cloudinary == null) return false;
        try {
            String publicId = extractPublicId(urlOrPublicId);
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String extractPublicId(String url) {
        if (!url.startsWith("http")) return url;
        String[] parts = url.split("/");
        String file = parts[parts.length - 1];
        int dot = file.lastIndexOf('.');
        if (dot >= 0) {
            return file.substring(0, dot);
        }
        return file;
    }
}
