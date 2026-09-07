package com.project.user.service;

import com.project.user.exception.BadRequestException;
import com.project.user.exception.ResourceNotFoundException;
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
            log.info("Initialized user avatar upload directory at: {} (mirror: {})", this.uploadDir, mirrorDir);
        } catch (IOException e) {
            log.error("Could not initialize storage directories for user avatars", e);
        }
    }

    private Path resolveUploadDir() {
        Path cwd = Paths.get("").toAbsolutePath().normalize();
        log.info("Resolving user avatar upload directory from cwd: {}", cwd);

        // If cwd is the workspace root (contains user-service folder)
        if (Files.exists(cwd.resolve("user-service")) && Files.isDirectory(cwd.resolve("user-service"))) {
            return cwd.resolve("uploads").resolve("avatars");
        }

        // If cwd is user-service submodule directory
        if (cwd.getFileName() != null && "user-service".equalsIgnoreCase(cwd.getFileName().toString()) && cwd.getParent() != null) {
            return cwd.getParent().resolve("uploads").resolve("avatars");
        }

        // If parent has user-service directory
        if (cwd.getParent() != null && Files.exists(cwd.getParent().resolve("user-service"))) {
            return cwd.getParent().resolve("uploads").resolve("avatars");
        }

        return cwd.resolve("uploads").resolve("avatars");
    }

    private Path getMirrorDir() {
        try {
            if (this.uploadDir.getParent() != null) {
                Path root = this.uploadDir.getParent().getParent();
                if (root != null && Files.exists(root.resolve("user-service"))) {
                    return root.resolve("user-service").resolve("uploads").resolve("avatars");
                }
                Path directSubmodule = this.uploadDir.getParent().resolve("user-service").resolve("uploads").resolve("avatars");
                return directSubmodule;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public String storeAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Failed to store empty avatar file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Invalid file type. Allowed image formats: JPG, PNG, WEBP, GIF, SVG");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "avatar.jpg"));
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
        String uniqueFilename = "avatar-" + UUID.randomUUID() + "-" + cleanName + "." + extension;

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
                log.debug("Mirror copy for avatar skipped: {}", mirrorEx.getMessage());
            }

            log.info("Stored user avatar image successfully: {}", uniqueFilename);

            // Return URL accessible through API Gateway
            String base = (imageBaseUrl != null && !imageBaseUrl.isBlank()) ? imageBaseUrl.replaceAll("/+$", "") : "http://localhost:8080";
            return base + "/api/v1/users/avatars/" + uniqueFilename;
        } catch (IOException ex) {
            log.error("Failed to store avatar file: {}", uniqueFilename, ex);
            throw new BadRequestException("Could not store avatar image file. Please try again.");
        }
    }

    public Resource loadAvatarAsResource(String filename) {
        if (filename == null || filename.isBlank() || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            log.warn("Invalid or traversal filename requested: {}", filename);
            throw new BadRequestException("Invalid filename specified: " + filename);
        }

        List<Path> candidatePaths = new ArrayList<>();
        candidatePaths.add(this.uploadDir.resolve(filename).normalize());
        
        Path mirror = getMirrorDir();
        if (mirror != null) {
            candidatePaths.add(mirror.resolve(filename).normalize());
        }

        if (this.uploadDir.getParent() != null) {
            candidatePaths.add(this.uploadDir.getParent().resolve("user-service").resolve("uploads").resolve("avatars").resolve(filename).normalize());
            candidatePaths.add(this.uploadDir.getParent().resolve("uploads").resolve("avatars").resolve(filename).normalize());
        }

        Path cwd = Paths.get("").toAbsolutePath().normalize();
        candidatePaths.add(cwd.resolve("uploads").resolve("avatars").resolve(filename).normalize());
        candidatePaths.add(cwd.resolve("user-service").resolve("uploads").resolve("avatars").resolve(filename).normalize());
        if (cwd.getParent() != null) {
            candidatePaths.add(cwd.getParent().resolve("uploads").resolve("avatars").resolve(filename).normalize());
            candidatePaths.add(cwd.getParent().resolve("user-service").resolve("uploads").resolve("avatars").resolve(filename).normalize());
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

        log.warn("Avatar image file not found in any candidate path for filename: {}", filename);
        throw new ResourceNotFoundException("Avatar image file not found: " + filename);
    }

    public String getContentType(String filename) {
        try {
            Resource res = loadAvatarAsResource(filename);
            Path filePath = Paths.get(res.getURI());
            String mimeType = Files.probeContentType(filePath);
            return mimeType != null ? mimeType : "application/octet-stream";
        } catch (Exception e) {
            return "application/octet-stream";
        }
    }
}

