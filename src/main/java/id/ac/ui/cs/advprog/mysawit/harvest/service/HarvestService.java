package id.ac.ui.cs.advprog.mysawit.harvest.service;

import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestDetailResponse;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestRequest;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestResponse;
import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HarvestService {

    // =========================
    // BURUH SUBMIT
    // =========================
    HarvestResponse submitHarvest(HarvestRequest request, UUID buruhId, UUID mandorId);
    // =========================
    // BURUH - MY HARVEST
    // =========================
    List<HarvestResponse> getMyHarvest(
            UUID buruhId,
            LocalDate startDate,
            LocalDate endDate,
            HarvestStatus status
    );

    // =========================
    // MANDOR - LIST BAWAHAN
    // =========================
    List<HarvestResponse> getPanenBawahan(
            UUID mandorId,
            UUID buruhId,
            LocalDate tanggalPanen
    );

    HarvestDetailResponse getDetail(UUID harvestId);

    HarvestResponse approvePanen(UUID harvestId, UUID mandorId);

    HarvestResponse rejectPanen(UUID harvestId, UUID mandorId, String alasan);

    void deleteHarvest(UUID harvestId);
}