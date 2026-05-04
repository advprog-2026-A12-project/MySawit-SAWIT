package id.ac.ui.cs.advprog.mysawit.harvest.event;

import java.util.UUID;

public record HarvestApprovedEvent(
        UUID harvestId,
        UUID buruhId,
        UUID mandorId,
        Double kilogram
) {}