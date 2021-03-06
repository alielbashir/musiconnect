package Home.Modules;

import java.sql.Date;

public class Song {
    int ID;
    String name;
    String album;
    Integer streams;
    Integer order;
    int length;
    Date releaseDate;


    public Song(Integer order, Integer ID, String name, Integer streams) {
        setOrder(order);
        setID(ID);
        setName(name);
        setStreams(streams);
    }

    public Song(int i, String name) {
        setOrder(i);
        setName(name);
    }

    public Song(int i, int songID, String name) {
        setOrder(i);
        setID(songID);
        setName(name);
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
    public Integer getStreams() {
        return streams;
    }

    public void setStreams(Integer streams) {
        this.streams = streams;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Song(int ID, String name, String album, int length ,Date releaseDate) {
        this.ID = ID;
        this.name = name;
        this.album = album;
        this.length=length;
        this.releaseDate = releaseDate;
    }


}
