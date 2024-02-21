package com.driver.model;

import javax.persistence.*;

@Entity
public class Cab {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int Id;

    int perKmRate;
    boolean available;

    @OneToOne
    @JoinColumn
    private Driver driver;

    public Cab( int perKmRate, boolean available) {
        this.perKmRate = perKmRate;
        this.available = available;
    }

    public Cab() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(int perKmRate) {
        this.perKmRate = perKmRate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}