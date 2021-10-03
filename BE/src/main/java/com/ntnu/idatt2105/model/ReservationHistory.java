package com.ntnu.idatt2105.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table
public class ReservationHistory {
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

    @ManyToOne()
    @JoinColumn(name="section_id")
    private Section section;

    public ReservationHistory(long id, String startTime, String endTime, LocalDate date, Section section) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.section = section;
    }

    public ReservationHistory(String startTime, String endTime, LocalDate date, Section section) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.section = section;
    }


    public ReservationHistory(Reservation reservation, Section section){
        this.startTime = reservation.getStartTime();
        this.endTime = reservation.getEndTime();
        this.date = reservation.getDate();
        this.section = section;

    }

    public ReservationHistory(){}


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public LocalTime getReservationLength(){
        LocalTime startTimeH = LocalTime.parse(this.startTime);
        LocalTime endTimeH = LocalTime.parse(this.endTime);

        return endTimeH.minusHours(startTimeH.getHour()).minusMinutes(startTimeH.getMinute());
    }

    @Override
    public String toString() {
        return "ReservationHistory{" +
                "id=" + id +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", date=" + date +
                '}';
    }
}
