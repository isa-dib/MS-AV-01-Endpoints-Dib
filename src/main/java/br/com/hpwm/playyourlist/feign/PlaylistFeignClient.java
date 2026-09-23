package br.com.hpwm.playyourlist.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.hpwm.playyourlist.entity.PlaylistEntity;

@FeignClient(name = "playlists-service", url = "${app.playlists.url}")
public interface PlaylistFeignClient {

    @GetMapping("/playlists/{playlistid}")
    ResponseEntity<PlaylistEntity> buscarPorId(@PathVariable("playlistid") Integer playlistid);

    @PostMapping("/playlists/{playlistid}/musicas/{musicaId}")
    ResponseEntity<Void> adicionarMusica(@PathVariable("playlistid") Integer playlistid,
            @PathVariable("musicaId") Integer musicaId);
}
