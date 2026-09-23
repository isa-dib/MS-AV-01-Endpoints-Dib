package br.com.hpwm.playyourlist.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.hpwm.playyourlist.entity.ReproducaoEntity;

/**
 * Client Feign para o serviço "reproducoes".
 * Obs.: o enunciado do MS-AV-01 menciona o endpoint POST /statistic para o
 * registro de execução, mas a especificação de endpoints do próprio
 * microsserviço "reproducoes" define esse registro como POST /reproducao.
 * Optou-se por seguir a especificação de endpoints (POST /reproducao),
 * mantendo o comentário aqui para deixar a divergência documentada.
 */
@FeignClient(name = "reproducoes-service", url = "${app.reproducoes.url}")
public interface ReproducaoFeignClient {

    @PostMapping("/reproducao")
    ResponseEntity<ReproducaoEntity> registrar(@RequestBody ReproducaoEntity reproducao);
}
