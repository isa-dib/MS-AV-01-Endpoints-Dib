package br.com.hpwm.playyourlist.dto;

/**
 * DTO retornado por GET /playlists/{playlistid}/musicas.
 * Conforme especificação, apenas o id da música é exposto
 * (sem nome, descrição, etc).
 */
public class MusicaIdDTO {

    private Integer musicaId;

    public MusicaIdDTO() {
    }

    public MusicaIdDTO(Integer musicaId) {
        this.musicaId = musicaId;
    }

    public Integer getMusicaId() {
        return musicaId;
    }

    public void setMusicaId(Integer musicaId) {
        this.musicaId = musicaId;
    }
}
