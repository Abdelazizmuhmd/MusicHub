package org.arrow.handler;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class ResourceService {

    @Autowired
    private BlobServiceClient blobServiceClient;

    public String uploadAudio(MultipartFile file) throws Exception {
        return uploadToBlobStorage(file);
    }

    private String uploadToBlobStorage(MultipartFile file) throws Exception {
        // Get the Blob Container reference (assuming "arrow-audio" is the container name)
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("arrow-audio");

        // Generate a unique blob name or use the original file name
        String blobName = generateUniqueBlobName(file.getOriginalFilename());

        // Upload the file to the Blob Storage
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        var blobHttpHeader = new BlobHttpHeaders();
        blobHttpHeader.setContentType("audio/mpeg");
        blobClient.upload(file.getInputStream(), file.getSize(), true);
        blobClient.setHttpHeaders(blobHttpHeader);

        // Return the ID or blob name as per your requirement
        return blobName;
    }

    public boolean deleteAudio(String blobId) {
        // Create a BlobServiceClient using the connection string
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("arrow-audio");

        // Get the Blob Client reference using the blob ID
        BlobClient blobClient = containerClient.getBlobClient(blobId);

        // Check if the blob exists before attempting to delete
        if (!blobClient.exists()) {
            return false; // Return false if the blob does not exist
        }

        // Delete the blob
        blobClient.delete();

        return true; // Return true if the blob is successfully deleted
    }

    public Resource downloadAudio(String blobId) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("arrow-audio");


        // Get the Blob Client reference using the blob ID
        BlobClient blobClient = containerClient.getBlobClient(blobId);

        // Download the blob content as InputStream
        InputStream inputStream = blobClient.openInputStream();

        // Create a Resource from the InputStream
        InputStreamResource resource = new InputStreamResource(inputStream);

        // Return the ResponseEntity with the Resource and headers
        return resource;
    }

    private String generateUniqueBlobName(String originalFileName) {
        // Implement your logic to generate a unique blob name
        // For example, you can append a timestamp or use a UUID
        return "audio_" + System.currentTimeMillis() + "_" + originalFileName;
    }

}
