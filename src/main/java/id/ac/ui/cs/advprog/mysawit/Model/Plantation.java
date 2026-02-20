package id.ac.ui.cs.advprog.mysawit.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Setter;

@Entity
@Table(name = "plantations")
public class Plantation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Setter
    private String code;

    @Setter
    private String name;

    @Setter
    private String location;

    @Column(name = "areaHectare", nullable = false)
    @Setter
    private Double areaHectare;

    public Plantation() {}

    public Plantation(String code, String name, String location, Double areaHectare) {
        this.code = code;
        this.name = name;
        this.location = location;
        this.areaHectare = areaHectare;
    }

    // Getters
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public Double getAreaHectare() { return areaHectare; }
}