package com.ntnu.idatt2105.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "Reservation")

public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "startTime")
    private String startTime;

    @Column(name = "endTime")
    private String endTime;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "roomName")
    private String roomName;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Section_Reservation",
            joinColumns = {@JoinColumn( name = "reservation_id")},
            inverseJoinColumns = {@JoinColumn (name = "section_id")} )
    Collection<Section> sections = new ArrayList<>();

    @ManyToOne()
    @JoinColumn(name="user_id")
    private User user;


    public Reservation(long id, String startTime, String endTime, LocalDate date, String roomName) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.roomName = roomName;
    }

    public Reservation(String startDate, String endTime, LocalDate date, String roomName, User user, Collection<Section> sections) {
        this.startTime = startDate;
        this.endTime = endTime;
        this.date = date;
        this.roomName = roomName;
        this.user = user;
        this.sections = sections;
    }

    public Reservation(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startDate) {
        this.startTime = startDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getRoomName() { return roomName; }

    public void setRoomName(String roomName) { this.roomName = roomName; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection<Section> getSections() {
        return sections;
    }

    public Collection<Section> getSectionsById() {
        return sections;
    }

    public void setSections(Collection<Section> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", date=" + date +
                '}';
    }
}




