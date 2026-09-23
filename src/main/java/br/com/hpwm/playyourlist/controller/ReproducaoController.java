package br.com.hpwm.playyourlist.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.hpwm.playyourlist.entity.ReproducaoEntity;
import br.com.hpwm.playyourlist.repository.ReproducaoRepository;
import jakarta.validation.Valid;

/**
 * Microsserviço "reproducoes": controla quantas vezes a playlist foi executada.
 */
@RestController
@RequestMapping("/reproducao")
public class ReproducaoController {

    private final ReproducaoRepository reproducaoRepository;

    public ReproducaoController(ReproducaoRepository reproducaoRepository) {
        this.reproducaoRepository = reproducaoRepository;
    }

    @PostMapping
    public ResponseEntity<ReproducaoEntity> registrar(@Valid @RequestBody ReproducaoEntity reproducao) {
        reproducao.setId(null);
        reproducao.setDatahora(LocalDateTime.now());
        ReproducaoEntity salva = reproducaoRepository.save(reproducao);
        return new ResponseEntity<>(salva, HttpStatus.CREATED);
    }

    @GetMapping("/{playlistid}")
    public ResponseEntity<Iterable<ReproducaoEntity>> listarPorPlaylist(@PathVariable Integer playlistid) {
        return new ResponseEntity<>(reproducaoRepository.findByPlaylistid(playlistid), HttpStatus.OK);
    }

    @GetMapping("/total/{playlistid}")
    public ResponseEntity<Long> totalPorPlaylist(@PathVariable Integer playlistid) {
        return new ResponseEntity<>(reproducaoRepository.countByPlaylistid(playlistid), HttpStatus.OK);
    }
}
