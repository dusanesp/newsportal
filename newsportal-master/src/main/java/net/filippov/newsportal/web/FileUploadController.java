package net.filippov.newsportal.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import net.filippov.newsportal.exception.UnacceptableFileFormatException;
import net.filippov.newsportal.web.constants.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for file uploading operations
 * 
 * @author Oleg Filippov
 */
@Controller
public class FileUploadController {

	/**
	 * Constants operating with images
	 */
	// TODO: Move to application.properties for configurability
	private static final String ARTICLE_IMAGES_PATH = "c:/Newsportal/article_images/";
	private static final String JPG_CONTENT_TYPE = "image/jpeg";
	private static final String PNG_CONTENT_TYPE = "image/png";
	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

	// Magic bytes for image validation
	private static final byte[] JPG_MAGIC = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF };
	private static final byte[] PNG_MAGIC = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47 };

	/**
	 * Upload image submit - requires ROLE_AUTHOR
	 */
	@PreAuthorize("hasRole('ROLE_AUTHOR')")
	@RequestMapping(method = RequestMethod.POST, value = URL.UPLOAD_IMAGE)
	@ResponseBody
	public String uploadimage(@RequestParam("file") MultipartFile image)
			throws IOException {

		// Validate file size
		if (image.getSize() > MAX_FILE_SIZE) {
			throw new UnacceptableFileFormatException("File size exceeds maximum allowed size of 5MB");
		}

		if (!image.isEmpty()) {
			// Validate MIME type
			String imageType = image.getContentType();
			if (!(imageType.equals(JPG_CONTENT_TYPE) || imageType.equals(PNG_CONTENT_TYPE))) {
				throw new UnacceptableFileFormatException("Invalid file type. Only JPG and PNG are allowed.");
			}

			// Validate file extension
			String originalFilename = image.getOriginalFilename();
			String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();
			if (!ALLOWED_EXTENSIONS.contains(extension)) {
				throw new UnacceptableFileFormatException(
						"Invalid file extension. Only jpg, jpeg, and png are allowed.");
			}

			// Validate file content (magic bytes)
			byte[] fileBytes = image.getBytes();
			if (!isValidImageFile(fileBytes)) {
				throw new UnacceptableFileFormatException("File content does not match image format.");
			}

			// Generate secure random filename
			String secureFilename = UUID.randomUUID().toString() + "." + extension;

			File file = new File(ARTICLE_IMAGES_PATH + secureFilename);
			FileUtils.writeByteArrayToFile(file, fileBytes);

			return "images/" + secureFilename;
		}

		throw new UnacceptableFileFormatException("File is empty");
	}

	/**
	 * Validate image file by checking magic bytes
	 */
	private boolean isValidImageFile(byte[] fileBytes) {
		if (fileBytes.length < 4) {
			return false;
		}

		// Check for JPG magic bytes
		if (fileBytes[0] == JPG_MAGIC[0] && fileBytes[1] == JPG_MAGIC[1] && fileBytes[2] == JPG_MAGIC[2]) {
			return true;
		}

		// Check for PNG magic bytes
		if (fileBytes[0] == PNG_MAGIC[0] && fileBytes[1] == PNG_MAGIC[1] &&
				fileBytes[2] == PNG_MAGIC[2] && fileBytes[3] == PNG_MAGIC[3]) {
			return true;
		}

		return false;
	}

	/**
	 * Get image from file-system
	 * 
	 * @param imageName image-name (sanitized)
	 * @param type      extension of image
	 * @param response  {@link HttpServletResponse}
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.GET, value = URL.SHOW_IMAGE)
	public void showImg(@PathVariable("name") String imageName,
			@PathVariable("type") String type, HttpServletResponse response)
			throws IOException {

		// Sanitize inputs to prevent path traversal
		String sanitizedName = sanitizeFilename(imageName);
		String sanitizedType = sanitizeFilename(type);

		// Validate extension
		if (!ALLOWED_EXTENSIONS.contains(sanitizedType.toLowerCase())) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file type");
			return;
		}

		// Construct safe file path
		Path basePath = Paths.get(ARTICLE_IMAGES_PATH).toAbsolutePath().normalize();
		Path filePath = basePath.resolve(sanitizedName + "." + sanitizedType).normalize();

		// Ensure the resolved path is still within the base directory (prevent path
		// traversal)
		if (!filePath.startsWith(basePath)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file path");
			return;
		}

		File file = filePath.toFile();
		if (!file.exists() || !file.isFile()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
			return;
		}

		try (InputStream in = new FileInputStream(file)) {
			FileCopyUtils.copy(in, response.getOutputStream());
		}
	}

	/**
	 * Sanitize filename to prevent path traversal attacks
	 */
	private String sanitizeFilename(String filename) {
		if (filename == null) {
			return "";
		}
		// Remove any path separators and parent directory references
		return filename.replaceAll("[^a-zA-Z0-9._-]", "");
	}
}
