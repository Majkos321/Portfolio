package com.example.client_music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MusicController {

    @Autowired
    private SongRepository songRepository;



    @GetMapping("/add")
    public String addSong(
            @RequestParam String carID,
            @RequestParam String title,
            @RequestParam String artist
    ){

        Song newSong = new Song(carID,title,artist);
        songRepository.save(newSong);
        return "Zapisano w bazie dla auta: " + carID;

    }

    @GetMapping("/playlista")
    public List<Song> getPlayList(@RequestParam String carID){
        //ta adontacja sluzy do wyciagania dancyh
        //z przegladarki
        return songRepository.findByCarID(carID);
    }

}
