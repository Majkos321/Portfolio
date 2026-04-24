package com.example.client_music;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SpotifyController {

    private String driverAccessToken = "";
    private final Set<String> blockedIps = ConcurrentHashMap.newKeySet();
    private String lastPassengerIp = "Brak";

    @Value("${spotify.client-id}")
    private String clientID;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    @GetMapping("/api/skip-track")
    @ResponseBody
    public String skipTrack(HttpServletRequest request) {
        if (blockedIps.contains(request.getRemoteAddr())) {
            return "Brak uprawnień. Twoje IP zostało zablokowane!";
        }

        if (driverAccessToken.isEmpty()) return "Najpierw się zaloguj!";

        try {
            SpotifyApi api = new SpotifyApi.Builder().setAccessToken(driverAccessToken).build();
            api.skipUsersPlaybackToNextTrack().build().execute();
            return "Przełączono na następny utwór!";
        } catch (Exception e) {
            return "Błąd skipowania: Upewnij się, że Spotify jest aktywne (" + e.getMessage() + ")";
        }
    }

    private SpotifyApi getSpotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId(clientID)
                .setClientSecret(clientSecret)
                .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
                .build();
    }

    @GetMapping("/login-spotify")
    public RedirectView loginToSpotify() {
        SpotifyApi spotifyApi = getSpotifyApi();

        URI uriForCode = spotifyApi.authorizationCodeUri()
                .scope("user-read-playback-state user-modify-playback-state user-read-email streaming app-remote-control")
                .show_dialog(true)
                .build()
                .execute();

        return new RedirectView(uriForCode.toString());
    }

    @GetMapping("/callback")
    public RedirectView spotifyCallback(@RequestParam String code) {
        try {
            SpotifyApi spotifyApi = getSpotifyApi();
            var authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
            var credentials = authorizationCodeRequest.execute();
            this.driverAccessToken = credentials.getAccessToken();

            return new RedirectView("/");

        } catch (Exception e) {
            System.out.println("Błąd logowania Spotify: " + e.getMessage());
            return new RedirectView("/?error=spotify_login_failed");
        }
    }

    @GetMapping("/api/play-spotify")
    @ResponseBody
    public String playOnSpotify(@RequestParam String title, @RequestParam String artist, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        lastPassengerIp = clientIp;

        if (blockedIps.contains(clientIp)) {
            return "Twoje IP (" + clientIp + ") zostało zablokowane przez kierowcę!";
        }

        if (driverAccessToken.isEmpty()) {
            return "Błąd: Kierowca nie zalogował się do Spotify! (Kliknij Zaloguj najpierw)";
        }

        try {
            SpotifyApi api = new SpotifyApi.Builder()
                    .setAccessToken(driverAccessToken)
                    .build();

            String searchQuery = title + " " + artist;
            var searchResults = api.searchTracks(searchQuery).limit(1).build().execute();

            if (searchResults.getItems().length == 0) {
                return "Nie znaleziono takiej piosenki na Spotify :(";
            }

            String trackUri = searchResults.getItems()[0].getUri();
            String foundName = searchResults.getItems()[0].getName();

            api.addItemToUsersPlaybackQueue(trackUri).build().execute();

            return "Ogień! Piosenka '" + foundName + "' dodana z sukcesem do auta!";

        } catch (Exception e) {
            return "Błąd Spotify: Upewnij się, że Spotify na telefonie kierowcy gra i jest aktywne! (" + e.getMessage() + ")";
        }
    }

    @GetMapping("/api/search-spotify")
    @ResponseBody
    public List<Map<String, String>> searchSpotify(@RequestParam String query, HttpServletRequest request) {
        if (blockedIps.contains(request.getRemoteAddr())) {
            return new ArrayList<>();
        }

        if (driverAccessToken.isEmpty() || query.isBlank()) {
            return new ArrayList<>();
        }

        try {
            SpotifyApi api = new SpotifyApi.Builder()
                    .setAccessToken(driverAccessToken)
                    .build();

            var results = api.searchTracks(query).limit(5).build().execute();
            List<Map<String, String>> tracks = new ArrayList<>();

            for (var track : results.getItems()) {
                Map<String, String> songData = new HashMap<>();
                songData.put("title", track.getName());
                songData.put("artist", track.getArtists()[0].getName());

                if (track.getAlbum().getImages().length > 0) {
                    songData.put("image", track.getAlbum().getImages()[0].getUrl());
                } else {
                    songData.put("image", "");
                }
                tracks.add(songData);
            }
            return tracks;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @GetMapping("/api/admin/status")
    @ResponseBody
    public Map<String, String> getStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("lastIp", lastPassengerIp);
        status.put("blockedCount", String.valueOf(blockedIps.size()));
        return status;
    }

    @PostMapping("/api/admin/block-last")
    @ResponseBody
    public String blockLastIp() {
        if (!lastPassengerIp.equals("Brak")) {
            blockedIps.add(lastPassengerIp);
            return "Zablokowano IP: " + lastPassengerIp;
        }
        return "Nie ma kogo blokować.";
    }
}