package br.com.hpwm.playyourlist.controller;

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

import br.com.hpwm.playyourlist.entity.MusicaEntity;
import br.com.hpwm.playyourlist.exception.RecursoNaoEncontradoException;
import br.com.hpwm.playyourlist.repository.MusicaRepository;
import jakarta.validation.Valid;

/**
 * Microsserviço "musicas": mantém as músicas disponíveis (CRUD).
 */
@RestController
@RequestMapping("/musicas")
public class MusicaController {

    private final MusicaRepository musicaRepository;

    public MusicaController(MusicaRepository musicaRepository) {
        this.musicaRepository = musicaRepository;
    }

    @PostMapping
    public ResponseEntity<MusicaEntity> cadastrar(@Valid @RequestBody MusicaEntity musica) {
        musica.setId(null);
        MusicaEntity salva = musicaRepository.save(musica);
        return new ResponseEntity<>(salva, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Iterable<MusicaEntity>> listarTodas() {
        return new ResponseEntity<>(musicaRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MusicaEntity> buscarPorId(@PathVariable Integer id) {
        MusicaEntity musica = musicaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Música não encontrada para o id " + id));
        return new ResponseEntity<>(musica, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MusicaEntity> atualizar(@PathVariable Integer id, @Valid @RequestBody MusicaEntity musica) {
        MusicaEntity existente = musicaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Música não encontrada para o id " + id));

        existente.setTitulo(musica.getTitulo());
        existente.setArtista(musica.getArtista());
        existente.setAlbum(musica.getAlbum());
        existente.setDuracao(musica.getDuracao());
        existente.setGenero(musica.getGenero());

        return new ResponseEntity<>(musicaRepository.save(existente), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        if (!musicaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Música não encontrada para o id " + id);
        }
        musicaRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
