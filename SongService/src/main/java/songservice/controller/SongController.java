package org.arrow.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources")
public class SongController {


  @PostMapping("/songs")
  public String submitMetaData(@RequestParam String name, @RequestParam String artist,
      @RequestParam int length,
      @RequestParam int year,
      @RequestParam String resourceId,
      @RequestParam String album) {
    // Process the received form values
    // You can perform any business logic here

    // For demonstration purposes, let's just return a simple response
    return "Form values received: " ;
  }

  @DeleteMapping("/delete/{ids}")
  public ResponseEntity<?> deleteMetaData(@PathVariable String ids) {
    return ResponseEntity.ok("da");
  }

  @GetMapping("/songs/{ids}")
  public ResponseEntity<?> getMetaData(@PathVariable String ids) {
    return ResponseEntity.ok("da");
  }


}
