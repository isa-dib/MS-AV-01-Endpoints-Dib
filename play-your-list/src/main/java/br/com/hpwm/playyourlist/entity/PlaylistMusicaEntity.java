package br.com.hpwm.playyourlist.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "playlist_musicas")
public class PlaylistMusicaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer playlistid;

    private Integer musicaid;

    public PlaylistMusicaEntity() {
    }

    public PlaylistMusicaEntity(Integer playlistid, Integer musicaid) {
        this.playlistid = playlistid;
        this.musicaid = musicaid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlaylistid() {
        return playlistid;
    }

    public void setPlaylistid(Integer playlistid) {
        this.playlistid = playlistid;
    }

    public Integer getMusicaid() {
        return musicaid;
    }

    public void setMusicaid(Integer musicaid) {
        this.musicaid = musicaid;
    }
}
