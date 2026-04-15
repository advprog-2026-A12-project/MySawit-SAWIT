package id.ac.ui.cs.advprog.mysawit.harvest.service;

import id.ac.ui.cs.advprog.mysawit.harvest.dto.*;
import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HarvestService {

    // Buruh submit panen
    HarvestResponse submitHarvest(HarvestRequest request, UUID buruhId, UUID mandorId);

    // Buruh lihat panen miliknya
    List<HarvestResponse> getMyHarvest(UUID buruhId, LocalDate startDate,
                                       LocalDate endDate, HarvestStatus status);

    // Mandor lihat panen bawahannya
    List<HarvestResponse> getPanenBawahan(UUID mandorId, UUID buruhId, LocalDate tanggalPanen);

    // Detail satu panen
    HarvestDetailResponse getDetail(UUID harvestId);

    // Mandor approve
    HarvestResponse approvePanen(UUID harvestId, UUID mandorId);

    // Mandor reject + wajib alasan
    HarvestResponse rejectPanen(UUID harvestId, UUID mandorId, String alasan);

    // Delete (untuk testing)
    void deleteHarvest(UUID harvestId);
}