package com.uway.booking.uploadmultiple.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.uway.booking.model.User;
import com.uway.booking.model.UserDocument;
import com.uway.booking.repository.UserDocumentRepository;

@Service
public class DocumentStorageService {
	private final Path fileStorageLocation;

	@Autowired
	UserDocumentRepository userDocumentRepository;

	@Autowired
	public DocumentStorageService(@Value("${file.upload-dir}") String uploadDir) {
		System.out.println("UPLOADED_FOLDER :: " + uploadDir);
		this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new DocumentStorageException(
					"Could not create the directory where the uploaded files will be stored.", ex);
		}
	}

	public String storeFile(MultipartFile file, User user) {
		// Normalize file name
		String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
		String fileName = "";
		try {
			// Check if the file's name contains invalid characters
			if (originalFileName.contains("..")) {
				throw new DocumentStorageException(
						"Sorry! Filename contains invalid path sequence " + originalFileName);
			}
			String fileExtension = "";
			try {
				fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
			} catch (Exception e) {
				fileExtension = "";
			}
			fileName = user.getId() + "_" + new Date().getTime() + fileExtension;

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			UserDocument document = new UserDocument();
			document.setDocumentType(file.getContentType());
			document.setFileName(fileName);
			document.setActualFileName(originalFileName);
			document.setUserId(user.getId());
			userDocumentRepository.save(document);

			return fileName;

		} catch (IOException ex) {
			throw new DocumentStorageException("Could not store file " + fileName + ". Please try again!", ex);

		}

	}

	public Resource loadFileAsResource(String fileName) throws Exception {

		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new FileNotFoundException("File not found " + fileName);
		}
	}

	public String getDocumentName(Integer userId, String docType) {
		return "";// docStorageRepo.getUploadDocumnetPath(userId, docType);
	}

	public Resource load(String filename) {
		try {
			Path file = this.fileStorageLocation.resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

}