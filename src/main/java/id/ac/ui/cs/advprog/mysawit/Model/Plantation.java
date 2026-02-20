package id.ac.ui.cs.advprog.mysawit.Model;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Table(name = "plantations")
public class Plantation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;
    @Setter
    private String location;

    public Plantation() {}

    public Plantation(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }

}