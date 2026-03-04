package id.ac.ui.cs.advprog.mysawit.garden.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "kebun")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Kebun {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nama;

    @Column(nullable = false, unique = true)
    private String kodeUnik;

    @Column(nullable = false)
    private Double luas;

    // 4 titik koordinat (sementara aku buat yg simple dulu)
    @Column(nullable = false)
    private Double lat1;

    @Column(nullable = false)
    private Double long1;

    @Column(nullable = false)
    private Double lat2;

    @Column(nullable = false)
    private Double long2;

    @Column(nullable = false)
    private Double lat3;

    @Column(nullable = false)
    private Double long3;

    @Column(nullable = false)
    private Double lat4;

    @Column(nullable = false)
    private Double long4;

}