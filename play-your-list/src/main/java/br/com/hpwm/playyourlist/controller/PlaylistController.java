package br.com.hpwm.playyourlist.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.hpwm.playyourlist.dto.MusicaIdDTO;
import br.com.hpwm.playyourlist.entity.MusicaEntity;
import br.com.hpwm.playyourlist.entity.PlaylistEntity;
import br.com.hpwm.playyourlist.entity.PlaylistMusicaEntity;
import br.com.hpwm.playyourlist.exception.RecursoNaoEncontradoException;
import br.com.hpwm.playyourlist.repository.MusicaRepository;
import br.com.hpwm.playyourlist.repository.PlaylistMusicaRepository;
import br.com.hpwm.playyourlist.repository.PlaylistRepository;
import jakarta.validation.Valid;

/**
 * Microsserviço "playlists": responsável pela manutenção das playlists.
 */
@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistRepository playlistRepository;
    private final MusicaRepository musicaRepository;
    private final PlaylistMusicaRepository playlistMusicaRepository;

    public PlaylistController(PlaylistRepository playlistRepository,
            MusicaRepository musicaRepository,
            PlaylistMusicaRepository playlistMusicaRepository) {
        this.playlistRepository = playlistRepository;
        this.musicaRepository = musicaRepository;
        this.playlistMusicaRepository = playlistMusicaRepository;
    }

    @PostMapping
    public ResponseEntity<PlaylistEntity> criar(@Valid @RequestBody PlaylistEntity playlist) {
        playlist.setId(null);
        PlaylistEntity salva = playlistRepository.save(playlist);
        return new ResponseEntity<>(salva, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Iterable<PlaylistEntity>> listarTodas() {
        return new ResponseEntity<>(playlistRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{playlistid}")
    public ResponseEntity<PlaylistEntity> buscarPorId(@PathVariable Integer playlistid) {
        PlaylistEntity playlist = obterPlaylistOuFalhar(playlistid);
        return new ResponseEntity<>(playlist, HttpStatus.OK);
    }

    @PutMapping("/{playlistid}")
    public ResponseEntity<PlaylistEntity> atualizar(@PathVariable Integer playlistid,
            @Valid @RequestBody PlaylistEntity playlist) {
        PlaylistEntity existente = obterPlaylistOuFalhar(playlistid);
        existente.setNome(playlist.getNome());
        existente.setDescricao(playlist.getDescricao());
        return new ResponseEntity<>(playlistRepository.save(existente), HttpStatus.OK);
    }

    @DeleteMapping("/{playlistid}")
    public ResponseEntity<Void> excluir(@PathVariable Integer playlistid) {
        obterPlaylistOuFalhar(playlistid);
        // remove as associações antes de excluir a playlist
        playlistMusicaRepository.deleteByPlaylistid(playlistid);
        playlistRepository.deleteById(playlistid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{playlistid}/musicas/{musicaId}")
    public ResponseEntity<Void> adicionarMusica(@PathVariable Integer playlistid, @PathVariable Integer musicaId) {
        obterPlaylistOuFalhar(playlistid);
        obterMusicaOuFalhar(musicaId);

        boolean jaExiste = playlistMusicaRepository.findByPlaylistidAndMusicaid(playlistid, musicaId).isPresent();
        if (!jaExiste) {
            playlistMusicaRepository.save(new PlaylistMusicaEntity(playlistid, musicaId));
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{playlistid}/musicas/{musicaId}")
    public ResponseEntity<Void> removerMusica(@PathVariable Integer playlistid, @PathVariable Integer musicaId) {
        obterPlaylistOuFalhar(playlistid);
        obterMusicaOuFalhar(musicaId);
        playlistMusicaRepository.deleteByPlaylistidAndMusicaid(playlistid, musicaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{playlistid}/musicas")
    public ResponseEntity<List<MusicaIdDTO>> listarMusicas(@PathVariable Integer playlistid) {
        obterPlaylistOuFalhar(playlistid);

        List<MusicaIdDTO> musicas = playlistMusicaRepository.findByPlaylistid(playlistid).stream()
                .map(pm -> new MusicaIdDTO(pm.getMusicaid()))
                .toList();

        return new ResponseEntity<>(musicas, HttpStatus.OK);
    }

    private PlaylistEntity obterPlaylistOuFalhar(Integer playlistid) {
        return playlistRepository.findById(playlistid)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Playlist não encontrada para o id " + playlistid));
    }

    private MusicaEntity obterMusicaOuFalhar(Integer musicaId) {
        return musicaRepository.findById(musicaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Música não encontrada para o id " + musicaId));
    }
}
