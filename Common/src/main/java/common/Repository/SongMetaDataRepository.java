package common.Repository;

import common.entity.SongMetaData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongMetaDataRepository extends JpaRepository<SongMetaData, Long> {

  void deleteAllByIdIn(List<Long> ids);

  SongMetaData getById(Long id);

}