package com.example.client_music;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByCarID(String carID);
}
