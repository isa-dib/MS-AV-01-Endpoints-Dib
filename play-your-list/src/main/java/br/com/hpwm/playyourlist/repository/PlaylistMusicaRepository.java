package br.com.hpwm.playyourlist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import br.com.hpwm.playyourlist.entity.PlaylistMusicaEntity;

public interface PlaylistMusicaRepository extends CrudRepository<PlaylistMusicaEntity, Integer> {

    List<PlaylistMusicaEntity> findByPlaylistid(Integer playlistid);

    Optional<PlaylistMusicaEntity> findByPlaylistidAndMusicaid(Integer playlistid, Integer musicaid);

    void deleteByPlaylistid(Integer playlistid);

    void deleteByPlaylistidAndMusicaid(Integer playlistid, Integer musicaid);
}
