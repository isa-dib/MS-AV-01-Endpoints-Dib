package br.com.hpwm.playyourlist.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.hpwm.playyourlist.entity.MusicaEntity;

@FeignClient(name = "musicas-service", url = "${app.musicas.url}")
public interface MusicaFeignClient {

    @GetMapping("/musicas/{id}")
    ResponseEntity<MusicaEntity> buscarPorId(@PathVariable("id") Integer id);
}
