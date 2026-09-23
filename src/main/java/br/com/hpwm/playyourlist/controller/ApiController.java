package br.com.hpwm.playyourlist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.hpwm.playyourlist.entity.MusicaEntity;
import br.com.hpwm.playyourlist.entity.PlaylistEntity;
import br.com.hpwm.playyourlist.entity.ReproducaoEntity;
import br.com.hpwm.playyourlist.exception.RecursoNaoEncontradoException;
import br.com.hpwm.playyourlist.feign.MusicaFeignClient;
import br.com.hpwm.playyourlist.feign.PlaylistFeignClient;
import br.com.hpwm.playyourlist.feign.ReproducaoFeignClient;

/**
 * Microsserviço "api": orquestra os demais serviços via Open Feign.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final MusicaFeignClient musicaFeignClient;
    private final PlaylistFeignClient playlistFeignClient;
    private final ReproducaoFeignClient reproducaoFeignClient;

    public ApiController(MusicaFeignClient musicaFeignClient,
            PlaylistFeignClient playlistFeignClient,
            ReproducaoFeignClient reproducaoFeignClient) {
        this.musicaFeignClient = musicaFeignClient;
        this.playlistFeignClient = playlistFeignClient;
        this.reproducaoFeignClient = reproducaoFeignClient;
    }

    @PostMapping("/adicionar/{playlistId}/musicas/{musicaId}")
    public ResponseEntity<String> adicionarMusicaAPlaylist(@PathVariable Integer playlistId,
            @PathVariable Integer musicaId) {

        PlaylistEntity playlist = obterPlaylist(playlistId);
        MusicaEntity musica = obterMusica(musicaId);

        playlistFeignClient.adicionarMusica(playlistId, musicaId);

        String mensagem = "Música " + musica.getTitulo() + " adicionada com sucesso à playlist " + playlist.getNome();
        return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @PutMapping("/executar/{playlistId}")
    public ResponseEntity<ReproducaoEntity> executarPlaylist(@PathVariable Integer playlistId) {
        // valida que a playlist existe antes de registrar a execução
        obterPlaylist(playlistId);

        ReproducaoEntity reproducao = new ReproducaoEntity();
        reproducao.setPlaylistid(playlistId);

        ReproducaoEntity registrada = reproducaoFeignClient.registrar(reproducao).getBody();

        return new ResponseEntity<>(registrada, HttpStatus.OK);
    }

    private PlaylistEntity obterPlaylist(Integer playlistId) {
        try {
            PlaylistEntity playlist = playlistFeignClient.buscarPorId(playlistId).getBody();
            if (playlist == null) {
                throw new RecursoNaoEncontradoException("Playlist não encontrada para o id " + playlistId);
            }
            return playlist;
        } catch (RecursoNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            throw new RecursoNaoEncontradoException("Playlist não encontrada para o id " + playlistId);
        }
    }

    private MusicaEntity obterMusica(Integer musicaId) {
        try {
            MusicaEntity musica = musicaFeignClient.buscarPorId(musicaId).getBody();
            if (musica == null) {
                throw new RecursoNaoEncontradoException("Música não encontrada para o id " + musicaId);
            }
            return musica;
        } catch (RecursoNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            throw new RecursoNaoEncontradoException("Música não encontrada para o id " + musicaId);
        }
    }
}
