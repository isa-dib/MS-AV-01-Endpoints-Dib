package br.com.hpwm.playyourlist.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import br.com.hpwm.playyourlist.entity.ReproducaoEntity;

public interface ReproducaoRepository extends CrudRepository<ReproducaoEntity, Integer> {

    List<ReproducaoEntity> findByPlaylistid(Integer playlistid);

    long countByPlaylistid(Integer playlistid);
}
