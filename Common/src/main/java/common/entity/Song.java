package common.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class Song {

    private String id;
    private SongMetaData metaData;

    public Song(String id, SongMetaData metaData) {
    this.id = id;
    this.metaData = metaData;
    }

}
