package com.rideshare.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    // 1. CHANGE: Use an EXTERNAL, persistent directory (e.g., /tmp is good for quick testing, use a real path like C:/rideshare-uploads for production)
    // IMPORTANT: Create this directory manually if it doesn't exist.
    private final Path fileStorageLocation = Paths.get("/tmp/rideshare-uploads/vehicles")
            .toAbsolutePath().normalize();

    // The base URL path the client will use to access the images (matches the Spring config below)
    private static final String WEB_ACCESS_PATH = "/vehicle-images/";

    public FileStorageService() throws IOException {
        try {
            Files.createDirectories(this.fileStorageLocation);
            System.out.println("File storage directory created: " + this.fileStorageLocation.toString());
        } catch (Exception ex) {
            // CRITICAL: Throw an error if directory creation fails
            throw new IOException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        // ... (File naming logic remains the same)
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }

        String uniqueFilename = UUID.randomUUID().toString() + extension;
        // ...

        try {
            if(uniqueFilename.contains("..")) {
                throw new IOException("Filename contains invalid path sequence " + uniqueFilename);
            }

            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation);

            // 2. CHANGE: Return the URL prefix defined for web access (e.g., /vehicle-images/uuid.jpg)
            return WEB_ACCESS_PATH + uniqueFilename;
        } catch (IOException ex) {
            throw new IOException("Could not store file " + uniqueFilename + ". Please try again!", ex);
        }
    }
}