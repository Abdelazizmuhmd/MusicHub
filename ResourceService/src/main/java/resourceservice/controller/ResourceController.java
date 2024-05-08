package org.arrow.controller;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import entity.Song;
import entity.SongMetaData;
import org.arrow.handler.ResourceService;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.helpers.DefaultHandler;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping("/")
    public ResponseEntity<?> uploadAudio(@RequestParam("file") MultipartFile file) {
      try {

        String apiUrl = "http://localhost:8080/resources/songs";

        // Validate if the file is an MP3
        if (!isMP3File(file)) {
          return ResponseEntity.status(400).body("Validation failed. Only MP3 files are allowed.");
        }
        String blobId = resourceService.uploadAudio(file);

        SongMetaData metadata = extractMetadata(file, blobId);
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create a request entity with the parameters and headers
        HttpEntity<String> requestEntity = new HttpEntity<>(
            "name=" + metadata.getName() +
                "&artist=" + metadata.getArtist() +
                "&length=" + metadata.getLength() +
                "&year=" + metadata.getYear() +
                "&resourceId=" + metadata.getResourceId() +
                "&album=" + metadata.getAlbum(), headers);

        // Make the POST request and get the response
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity,
            String.class);

        // Process the response if needed
        String responseBody = response.getBody();
        System.out.println("Response from API: " + responseBody);

        // Upload the file to the Blob Storage
        Song song = new Song(blobId, metadata);

        // Return the ID along with a 200 OK response
        return ResponseEntity.ok().body(song);
      } catch (Exception e) {
        // Handle exceptions appropriately
        return ResponseEntity.status(500).body("An internal server error has occurred");
      }
    }

  private SongMetaData extractMetadata(MultipartFile file, String blobId)
      throws IOException,
          CannotReadException,
          TagException,
          InvalidAudioFrameException,
          ReadOnlyFileException {

        File tempFile = File.createTempFile("temp", ".mp3");
        file.transferTo(tempFile);
        AudioFile audioFile = AudioFileIO.read(tempFile);

        Tag tag = audioFile.getTag();
        if (tag != null) {
            String name = tag.getFirst(FieldKey.TITLE);
            String artist = tag.getFirst(FieldKey.ARTIST);
            String album = tag.getFirst(FieldKey.ALBUM);
            String length = String.valueOf(audioFile.getAudioHeader().getTrackLength());
            String year = tag.getFirst(FieldKey.YEAR);

            return new SongMetaData(name, artist, length, blobId, album, year);
        }
        return null;
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> downloadAudio(@PathVariable String id) {
        try {
            // Retrieve the audio file from the Blob Storage based on the provided ID
            Resource audioFile = resourceService.downloadAudio(id);

            // Check if the file exists
            if (audioFile != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("audio/mp3"));
                headers.setContentDispositionFormData("attachment", id);

                // Return the audio file with a 200 OK response
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(audioFile);
            } else {
                // If the file is not found, return a 404 Not Found response
                return ResponseEntity.status(404).body("the resource with specified id does not exist");
            }
        } catch (Exception e) {
            // Handle exceptions appropriately
            return ResponseEntity.status(500).body("An internal server error has occurred");
        }
    }

    @DeleteMapping("/delete/{ids}")
    public ResponseEntity<?> deleteAudio(@PathVariable String ids) throws IOException {
        try {
            List<String> allIds = new ArrayList<>();
            List<String> idsDeleted = new ArrayList<>();
            // Read the CSV file and extract IDs

            // Use StringTokenizer to tokenize the line using commas
            StringTokenizer tokenizer = new StringTokenizer(ids, ",");

            // Add each token (ID) to the list
            while (tokenizer.hasMoreTokens()) {
                String id = tokenizer.nextToken().trim();
                allIds.add(id);

                // Delete the audio file from the Blob Storage based on the provided ID
                boolean isDeleted = resourceService.deleteAudio(id);
                if (isDeleted) {
                    idsDeleted.add(id);
                }
            }
            // Return the list of all IDs in the response body
            return ResponseEntity.ok(idsDeleted);
        } catch (Exception e) {
            // Handle exceptions appropriately
            return ResponseEntity.status(500).body("Failed to delete audio files: " + e.getMessage());
        }
    }

    private boolean isMP3File(MultipartFile file) {
        return file.getOriginalFilename() != null && file.getOriginalFilename().toLowerCase().endsWith(".mp3");
    }

}
