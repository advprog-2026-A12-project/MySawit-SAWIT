package id.ac.ui.cs.advprog.mysawit.harvest.service;

import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestStatus;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestDetailResponse;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestRequest;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HarvestService {

    // Tambah hasil panen
    HarvestResponse submitHarvest(HarvestRequest request, UUID userId, String role);

    // Ambil list hasil panen user
    List<HarvestResponse> getMyHarvest(UUID userId, LocalDate startDate, LocalDate endDate, HarvestStatus status);

    // Ambil detail hasil panen
    HarvestDetailResponse getDetail(UUID harvestId);

    // delete hasil panen untuk testing karena ada validasi 1 submit hasil panen sehari
    void deleteHarvest(UUID harvestId);
}