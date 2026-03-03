package id.ac.ui.cs.advprog.mysawit.Service;

import id.ac.ui.cs.advprog.mysawit.Model.HarvestStatus;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestDetailResponse;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestRequest;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestResponse;

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
}