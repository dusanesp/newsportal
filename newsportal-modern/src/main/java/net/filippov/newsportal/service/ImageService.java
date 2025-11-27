package net.filippov.newsportal.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service for handling article image uploads and management
 */
@Service
public class ImageService {

    private static final String UPLOAD_DIR = "src/main/resources/static/images/articles";
    private static final String[] ALLOWED_EXTENSIONS = { ".jpg", ".jpeg", ".png", ".webp" };
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * Save uploaded image file
     * 
     * @param file the uploaded file
     * @return the relative path to the saved image
     * @throws IOException              if file cannot be saved
     * @throws IllegalArgumentException if file validation fails
     */
    public String saveImage(MultipartFile file) throws IOException {
        validateImage(file);

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String filename = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative path for web access
        return "/images/articles/" + filename;
    }

    /**
     * Delete an image file
     * 
     * @param imageUrl the relative path of the image to delete
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // Extract filename from URL
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(UPLOAD_DIR, filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            // Log error but don't throw - deletion failure shouldn't break the application
            System.err.println("Failed to delete image: " + imageUrl);
        }
    }

    /**
     * Get fallback image based on category
     * 
     * @param categoryName the category name
     * @return the path to the fallback image
     */
    public String getFallbackImage(String categoryName) {
        if (categoryName == null) {
            return "/images/placeholder.png";
        }

        // Map category to placeholder
        String placeholderName = switch (categoryName.toLowerCase()) {
            case "technology", "tech" -> "tech_placeholder.png";
            case "business", "economy" -> "business_placeholder.png";
            case "sports" -> "sports_placeholder.png";
            case "politics" -> "politics_placeholder.png";
            default -> "default_placeholder.png";
        };

        return "/images/placeholders/" + placeholderName;
    }

    /**
     * Validate uploaded image file
     * 
     * @param file the file to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select an image file to upload");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size must not exceed 5MB");
        }

        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String extension = originalFilename.toLowerCase().substring(originalFilename.lastIndexOf("."));
        boolean validExtension = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowedExt)) {
                validExtension = true;
                break;
            }
        }

        if (!validExtension) {
            throw new IllegalArgumentException("Only JPG, JPEG, PNG and WEBP files are allowed");
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
    }
}
