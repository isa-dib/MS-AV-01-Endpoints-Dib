package br.com.hpwm.playyourlist.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.hpwm.playyourlist.entity.PlaylistEntity;

public interface PlaylistRepository extends CrudRepository<PlaylistEntity, Integer> {
}
