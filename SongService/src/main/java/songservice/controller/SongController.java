package songservice.controller;

import common.Repository.SongMetaDataRepository;
import common.entity.SongMetaData;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
public class SongController {

  @Autowired
  private SongMetaDataRepository songMetaDataRepository;

  @PostMapping("/submit")
  public ResponseEntity submitMetaData(@RequestParam("name") String name,
      @RequestParam("artist") String artist,
      @RequestParam("length") String length,
      @RequestParam("year") String year,
      @RequestParam("resourceId") String resourceId,
      @RequestParam("album") String album) {
    System.out.println("Done");
    songMetaDataRepository.save(
        SongMetaData.builder().album(album).name(name).resourceId(resourceId).artist(artist)
            .year(year).length(length).build());
    return ResponseEntity.ok("ok");
  }

  @DeleteMapping("/delete/{ids}")
  public ResponseEntity<?> deleteMetaData(@PathVariable List<Long> ids) {
    songMetaDataRepository.deleteAllByIdIn(ids);
    return ResponseEntity.ok("da");
  }

  @GetMapping("/songs/{ids}")
  public ResponseEntity<?> getMetaData(@PathVariable Long id) {
    SongMetaData songMetaData =  songMetaDataRepository.getById(id);
    if(songMetaData!=null){

    }else{

    }
    return ResponseEntity.ok("da");
  }


}
