package com.ntnu.idatt2105.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name= "Room")

/**
 * Class for Room
 */

public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Transient
    @Column(name = "noOfSections")
    private int noOfSections;

    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Section> sections;

    /**
     * Constructor
     * @param id unique
     * @param name of the room
     */
    public Room(long id, String name, Set<Section> sections) {
        this.id = id;
        this.name = name;
        this.sections = sections;
    }

    public Room(String name, Set<Section> sections) {
        this.name = name;
        this.sections = sections;
    }

    public Room(String name) {
        this.name = name;
    }

    /**
     * Empty constructor
     */
    public Room(){

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

    public Set<Section> getSections() {
        return sections;
    }

    public void setSections(Set<Section> sections) {
        this.sections = sections;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNoOfSections() {
        return noOfSections;
    }

    public void setNoOfSections(int no){
        this.noOfSections = no;
    }

}
