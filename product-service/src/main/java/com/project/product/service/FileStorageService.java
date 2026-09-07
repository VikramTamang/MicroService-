package com.project.product.service;

import com.project.product.exception.BadRequestException;
import com.project.product.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Path uploadDir;

    @Value("${app.image-base-url:http://localhost:8080}")
    private String imageBaseUrl;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif", "svg");
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif", "image/svg+xml"
    );

    public FileStorageService() {
        this.uploadDir = resolveUploadDir().toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
            Path mirrorDir = getMirrorDir();
            if (mirrorDir != null) {
                Files.createDirectories(mirrorDir);
            }
            log.info("Initialized product image upload directory at: {} (mirror: {})", this.uploadDir, mirrorDir);
        } catch (IOException e) {
            log.error("Could not initialize storage directories for product images", e);
        }
    }

    private Path resolveUploadDir() {
        Path cwd = Paths.get("").toAbsolutePath().normalize();
        log.info("Resolving product image upload directory from cwd: {}", cwd);

        // If cwd is the workspace root (contains product-service folder)
        if (Files.exists(cwd.resolve("product-service")) && Files.isDirectory(cwd.resolve("product-service"))) {
            return cwd.resolve("uploads").resolve("products");
        }

        // If cwd is product-service submodule directory
        if (cwd.getFileName() != null && "product-service".equalsIgnoreCase(cwd.getFileName().toString()) && cwd.getParent() != null) {
            return cwd.getParent().resolve("uploads").resolve("products");
        }

        // If parent has product-service directory
        if (cwd.getParent() != null && Files.exists(cwd.getParent().resolve("product-service"))) {
            return cwd.getParent().resolve("uploads").resolve("products");
        }

        return cwd.resolve("uploads").resolve("products");
    }

    private Path getMirrorDir() {
        try {
            if (this.uploadDir.getParent() != null) {
                Path root = this.uploadDir.getParent().getParent();
                if (root != null && Files.exists(root.resolve("product-service"))) {
                    return root.resolve("product-service").resolve("uploads").resolve("products");
                }
                Path directSubmodule = this.uploadDir.getParent().resolve("product-service").resolve("uploads").resolve("products");
                return directSubmodule;
            }
        } catch (Exception ignored) {
        }
        return null;
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

            // Mirror copy to submodule upload folder as well
            try {
                Path mirrorDir = getMirrorDir();
                if (mirrorDir != null) {
                    Files.createDirectories(mirrorDir);
                    Path mirrorLocation = mirrorDir.resolve(uniqueFilename);
                    Files.copy(targetLocation, mirrorLocation, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception mirrorEx) {
                log.debug("Mirror copy for product image skipped: {}", mirrorEx.getMessage());
            }

            log.info("Stored product image successfully: {}", uniqueFilename);

            // Return URL accessible through API Gateway
            String base = (imageBaseUrl != null && !imageBaseUrl.isBlank()) ? imageBaseUrl.replaceAll("/+$", "") : "http://localhost:8080";
            return base + "/api/v1/products/images/" + uniqueFilename;
        } catch (IOException ex) {
            log.error("Failed to store file: {}", uniqueFilename, ex);
            throw new BadRequestException("Could not store image file. Please try again.");
        }
    }

    public Resource loadFileAsResource(String filename) {
        if (filename == null || filename.isBlank() || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            log.warn("Invalid or traversal filename requested: {}", filename);
            throw new BadRequestException("Invalid file path specified: " + filename);
        }

        List<Path> candidatePaths = new ArrayList<>();
        candidatePaths.add(this.uploadDir.resolve(filename).normalize());

        Path mirror = getMirrorDir();
        if (mirror != null) {
            candidatePaths.add(mirror.resolve(filename).normalize());
        }

        if (this.uploadDir.getParent() != null) {
            candidatePaths.add(this.uploadDir.getParent().resolve("product-service").resolve("uploads").resolve("products").resolve(filename).normalize());
            candidatePaths.add(this.uploadDir.getParent().resolve("uploads").resolve("products").resolve(filename).normalize());
        }

        Path cwd = Paths.get("").toAbsolutePath().normalize();
        candidatePaths.add(cwd.resolve("uploads").resolve("products").resolve(filename).normalize());
        candidatePaths.add(cwd.resolve("product-service").resolve("uploads").resolve("products").resolve(filename).normalize());
        if (cwd.getParent() != null) {
            candidatePaths.add(cwd.getParent().resolve("uploads").resolve("products").resolve(filename).normalize());
            candidatePaths.add(cwd.getParent().resolve("product-service").resolve("uploads").resolve("products").resolve(filename).normalize());
        }

        for (Path path : candidatePaths) {
            try {
                if (Files.exists(path) && Files.isReadable(path)) {
                    Resource resource = new UrlResource(path.toUri());
                    if (resource.exists() && resource.isReadable()) {
                        return resource;
                    }
                }
            } catch (MalformedURLException ignored) {
            }
        }

        log.warn("Product image file not found in any candidate path for filename: {}", filename);
        throw new ResourceNotFoundException("Image file not found: " + filename);
    }

    public String getContentType(String filename) {
        try {
            Resource res = loadFileAsResource(filename);
            Path filePath = Paths.get(res.getURI());
            String mimeType = Files.probeContentType(filePath);
            return mimeType != null ? mimeType : "application/octet-stream";
        } catch (Exception e) {
            return "application/octet-stream";
        }
    }
}

