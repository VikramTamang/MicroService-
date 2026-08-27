package com.project.product.service;

import com.project.product.exception.BadRequestException;
import com.project.product.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Path uploadDir;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif", "svg");
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif", "image/svg+xml"
    );

    public FileStorageService() {
        this.uploadDir = Paths.get("uploads", "products").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
            log.info("Initialized product image upload directory at: {}", this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory for product images", e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Failed to store empty file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Invalid file type. Allowed image formats: JPG, PNG, WEBP, GIF, SVG");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "image.jpg"));
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex + 1).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Invalid file extension: " + extension);
        }

        String cleanName = originalFilename.substring(0, dotIndex > 0 ? dotIndex : originalFilename.length())
                .replaceAll("[^a-zA-Z0-9_-]", "_");
        String uniqueFilename = UUID.randomUUID() + "-" + cleanName + "." + extension;

        try {
            Path targetLocation = this.uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored product image successfully: {}", uniqueFilename);

            // Return URL accessible through API Gateway
            return "http://localhost:8080/api/v1/products/images/" + uniqueFilename;
        } catch (IOException ex) {
            log.error("Failed to store file: {}", uniqueFilename, ex);
            throw new BadRequestException("Could not store image file. Please try again.");
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.uploadDir.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Image file not found: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("Image file not found: " + filename);
        }
    }

    public String getContentType(String filename) {
        try {
            Path filePath = this.uploadDir.resolve(filename).normalize();
            String mimeType = Files.probeContentType(filePath);
            return mimeType != null ? mimeType : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
}
