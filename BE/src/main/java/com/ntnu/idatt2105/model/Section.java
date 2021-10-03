package com.ntnu.idatt2105.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "Section")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "maxPeople")
    private int suggestedAmountPeople;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @JsonIgnore
    @ManyToMany(mappedBy = "sections")
    private Collection<Reservation> reservations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "section")
    private Set<ReservationHistory> reservationHistories;

    /**
     * Constructor for section
     * @param id unique
     * @param name of the section
     * @param suggestedAmountPeople recommended amount of people for the section
     * @param room that the section is inside of
     */
    public Section(long id, String name, int suggestedAmountPeople, Room room) {
        this.id = id;
        this.name = name;
        this.suggestedAmountPeople = suggestedAmountPeople;
        this.room = room;
    }

    public Section(String name, int suggestedAmountPeople, Room room) {
        this.name = name;
        this.suggestedAmountPeople = suggestedAmountPeople;
        this.room = room;
    }

    /**
     * Empty constructor
     */
    public Section(){}

    public Set<ReservationHistory> getReservationHistories() {
        return reservationHistories;
    }

    public void setReservationHistories(Set<ReservationHistory> reservationHistories) {
        this.reservationHistories = reservationHistories;
    }

    /**
     * Getters and setters.
     */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSuggestedAmountPeople() {
        return suggestedAmountPeople;
    }

    public void setSuggestedAmountPeople(int maxPeople) {
        this.suggestedAmountPeople = maxPeople;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Collection<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Collection<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", suggestedAmountPeople=" + suggestedAmountPeople +
                ", room=" + room +
                '}';
    }
}
