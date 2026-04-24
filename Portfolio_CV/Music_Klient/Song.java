package com.example.client_music;

import jakarta.persistence.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
@Entity //mowi ze to nie jest zywkla klasa tlyko to bedzie tabela w bazie danych
public class Song{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//to jest autonuemracja
    private Long id;


    private String carID;
    private String title;
    private String artist;

    public Song(){}

    Song(String carID, String title, String artist){
        this.carID = carID;
        this.title = title;
        this.artist = artist;
    }

    Long getId(){
        return id;
    }
    String getCarId(){
        return carID;
    }

    String getTitle(){
        return title;
    }
    String getArtist(){
        return artist;
    }

}