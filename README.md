# Play Your List — MS-AV-01 (HPWM)

Implementação dos 4 "microsserviços" descritos no `MS-AV-01-Endpoints.md`,
seguindo os padrões ensinados no `README-ms.md` (camada de persistência com
`CrudRepository`, validação com Bean Validation, orquestração com Open Feign).

## Arquitetura

Como o próprio enunciado permite ("para facilitar, criar todos os endpoints
dentro do mesmo projeto"), os 4 serviços foram implementados como módulos
lógicos dentro de **um único projeto Spring Boot**, todos expostos na porta
`8080`:

```
br.com.hpwm.playyourlist
├── PlayYourListApplication.java      # @SpringBootApplication + @EnableFeignClients
├── entity/            # MusicaEntity, PlaylistEntity, PlaylistMusicaEntity, ReproducaoEntity
├── repository/        # CrudRepository para cada entidade (consultas derivadas)
├── controller/
│   ├── MusicaController      -> /musicas          (serviço "musicas")
│   ├── PlaylistController    -> /playlists         (serviço "playlists")
│   ├── ReproducaoController  -> /reproducao         (serviço "reproducoes")
│   └── ApiController         -> /api                (serviço "api", orquestrador)
├── feign/              # Clients Open Feign usados pelo ApiController
├── dto/                # MusicaIdDTO (retorno enxuto de /playlists/{id}/musicas)
└── exception/          # Tratamento global de erros (400 validação / 404 não encontrado)
```

O serviço **api** não acessa os repositórios diretamente: ele consome os
outros três serviços via **Open Feign**, exatamente como pedido no enunciado,
apontando para `localhost:8080` (a mesma instância). Em uma implantação real
com projetos/portas separados, bastaria trocar as URLs em
`application.properties` (`app.musicas.url`, `app.playlists.url`,
`app.reproducoes.url`).

## Endpoints

| Serviço      | Método | Caminho                                          |
|--------------|--------|---------------------------------------------------|
| musicas      | POST   | `/musicas`                                        |
| musicas      | GET    | `/musicas`                                        |
| musicas      | GET    | `/musicas/{id}`                                   |
| musicas      | PUT    | `/musicas/{id}`                                   |
| musicas      | DELETE | `/musicas/{id}`                                   |
| playlists    | POST   | `/playlists`                                      |
| playlists    | GET    | `/playlists`                                      |
| playlists    | GET    | `/playlists/{playlistid}`                         |
| playlists    | PUT    | `/playlists/{playlistid}`                         |
| playlists    | DELETE | `/playlists/{playlistid}`                         |
| playlists    | POST   | `/playlists/{playlistid}/musicas/{musicaId}`      |
| playlists    | DELETE | `/playlists/{playlistid}/musicas/{musicaId}`      |
| playlists    | GET    | `/playlists/{playlistid}/musicas` (só ids)        |
| reproducoes  | POST   | `/reproducao`                                     |
| reproducoes  | GET    | `/reproducao/{playlistid}`                        |
| reproducoes  | GET    | `/reproducao/total/{playlistid}`                  |
| api          | POST   | `/api/adicionar/{playlistId}/musicas/{musicaId}`  |
| api          | PUT    | `/api/executar/{playlistId}`                      |

## Validações implementadas

- **Música**: `titulo` e `artista` obrigatórios (`@NotBlank`); `album` opcional
  até 150 caracteres; `duracao` obrigatória e `> 0`; `genero` opcional até 50
  caracteres.
- **Playlist**: `nome` obrigatório (`@NotBlank`); `descricao` opcional até 255
  caracteres.
- Erros de validação retornam `400` com o detalhamento por campo; recursos
  inexistentes retornam `404` (tratados em `GlobalExceptionHandler`).

## Testando as regras de validação

Todas as chamadas abaixo assumem a aplicação rodando em `http://localhost:8080`
e usam o formato **testado e confirmado no `cmd.exe`** (Prompt de Comando do
Windows): comando inteiro em **uma única linha**, sem `\`, `` ` `` ou `^` de
continuação, e o corpo JSON entre aspas duplas com `\"` escapando as aspas
internas. Se você usa bash/Git Bash/macOS/Linux ou PowerShell, o mesmo comando
funciona — só troque as aspas duplas externas do `-d` por aspas simples
(`-d '{"titulo":...}'`) e pode quebrar em várias linhas com `\` (bash) ou
`` ` `` (PowerShell) se preferir.

### Música (`POST /musicas` e `PUT /musicas/{id}`)

Regras: `titulo` obrigatório e não vazio/só espaços; `artista` obrigatório e
não vazio/só espaços; `album` opcional (máx. 150 caracteres); `duracao`
obrigatória e `> 0`; `genero` opcional (máx. 50 caracteres).

**Cadastro válido** (`201 Created`):
```cmd
curl -i -X POST http://localhost:8080/musicas -H "Content-Type: application/json" -d "{\"titulo\":\"Hotel California\",\"artista\":\"Eagles\",\"album\":\"Hotel California\",\"duracao\":391,\"genero\":\"Rock\"}"
```

**Título vazio (viola `@NotBlank`) → `400 Bad Request`**:
```cmd
curl -i -X POST http://localhost:8080/musicas -H "Content-Type: application/json" -d "{\"titulo\":\"   \",\"artista\":\"Eagles\",\"duracao\":391}"
```

**Artista ausente → `400 Bad Request`**:
```cmd
curl -i -X POST http://localhost:8080/musicas -H "Content-Type: application/json" -d "{\"titulo\":\"Hotel California\",\"duracao\":391}"
```

**Duração zerada/negativa (viola `@Positive`) → `400 Bad Request`**:
```cmd
curl -i -X POST http://localhost:8080/musicas -H "Content-Type: application/json" -d "{\"titulo\":\"Hotel California\",\"artista\":\"Eagles\",\"duracao\":0}"
```

**Duração ausente (viola `@NotNull`) → `400 Bad Request`**:
```cmd
curl -i -X POST http://localhost:8080/musicas -H "Content-Type: application/json" -d "{\"titulo\":\"Hotel California\",\"artista\":\"Eagles\"}"
```

**Gênero acima de 50 caracteres → `400 Bad Request`**:
```cmd
curl -i -X POST http://localhost:8080/musicas -H "Content-Type: application/json" -d "{\"titulo\":\"Hotel California\",\"artista\":\"Eagles\",\"duracao\":391,\"genero\":\"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}"
```

**Álbum acima de 150 caracteres → `400 Bad Request`**: mesma ideia, enviando
uma string de `album` com mais de 150 caracteres no mesmo formato acima.

Todas as respostas `400` seguem o formato:
```json
{
  "timestamp": "2026-09-23T21:00:00",
  "status": 400,
  "mensagem": "Erro de validação",
  "erros": {
    "duracao": "Duração deve ser maior que zero",
    "artista": "Artista é obrigatório e não pode ser vazio ou conter apenas espaços"
  }
}
```

A validação vale tanto para `POST /musicas` quanto para `PUT /musicas/{id}`
(troque `-X POST .../musicas` por `-X PUT .../musicas/{id}` com um `{id}`
existente, mantendo o corpo JSON no mesmo formato).

**Atualização válida** (`PUT`, `200 OK`) — exemplo usando o id `1`:
```cmd
curl -i -X PUT http://localhost:8080/musicas/1 -H "Content-Type: application/json" -d "{\"titulo\":\"Imagine (Remasterizado)\",\"artista\":\"John Lennon\",\"album\":\"Imagine\",\"duracao\":183,\"genero\":\"Rock\"}"
```

### Playlist (`POST /playlists` e `PUT /playlists/{playlistid}`)

Regras: `nome` obrigatório e não vazio/só espaços; `descricao` opcional
(máx. 255 caracteres).

**Cadastro válido** (`201 Created`):
```cmd
curl -i -X POST http://localhost:8080/playlists -H "Content-Type: application/json" -d "{\"nome\":\"Road Trip\",\"descricao\":\"Musicas para viagens longas de carro\"}"
```

**Nome vazio (viola `@NotBlank`) → `400 Bad Request`**:
```cmd
curl -i -X POST http://localhost:8080/playlists -H "Content-Type: application/json" -d "{\"nome\":\"   \",\"descricao\":\"Sem nome valido\"}"
```

**Nome ausente → `400 Bad Request`**:
```cmd
curl -i -X POST http://localhost:8080/playlists -H "Content-Type: application/json" -d "{\"descricao\":\"Playlist sem nome\"}"
```

**Descrição acima de 255 caracteres → `400 Bad Request`**:
```cmd
curl -i -X POST http://localhost:8080/playlists -H "Content-Type: application/json" -d "{\"nome\":\"Teste\",\"descricao\":\"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}"
```
(a `descricao` acima tem mais de 255 caracteres, propositalmente, para violar a regra)

**Atualização válida** (`PUT`, `200 OK`) — exemplo usando o `playlistid` `1`:
```cmd
curl -i -X PUT http://localhost:8080/playlists/1 -H "Content-Type: application/json" -d "{\"nome\":\"Classicos do Rock (Atualizado)\",\"descricao\":\"Descricao atualizada\"}"
```

### Recursos inexistentes → `404 Not Found`

Além das validações de campo, endpoints que referenciam um `id`/`playlistid`
inexistente retornam `404`, por exemplo:
```cmd
curl -i http://localhost:8080/musicas/9999
```
```cmd
curl -i http://localhost:8080/playlists/9999
```
```cmd
curl -i -X POST http://localhost:8080/api/adicionar/9999/musicas/1
```

## Executando

```bash
mvn spring-boot:run
```

- API em `http://localhost:8080`
- Console H2 em `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:playyourlist`, user `sa`, senha `password`)
- `data.sql` popula automaticamente músicas, playlists e reproduções de exemplo.

## Observação sobre `/api/executar`

O enunciado cita que a execução deve gerar o registro "pelo endpoint
`POST /statistic`", mas a especificação do próprio serviço de reproduções
define esse endpoint como `POST /reproducao`. O `ReproducaoFeignClient` foi
implementado apontando para `/reproducao` (a especificação de endpoints
prevalece), com o comentário da divergência deixado no código-fonte.