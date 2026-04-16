package id.ac.ui.cs.advprog.mysawit.harvest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserProfile {
    private UUID id;
    private String role;
    private UUID mandorId;
}