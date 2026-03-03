package id.ac.ui.cs.advprog.mysawit.Service;

import id.ac.ui.cs.advprog.mysawit.dto.HarvestDetailResponse;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestRequest;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestResponse;
import id.ac.ui.cs.advprog.mysawit.Model.HarvestStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HarvestService {

    HarvestResponse submitHarvest(
            HarvestRequest request,
            UUID buruhId,
            String role
    );

    List<HarvestResponse> getMyHarvest(
            UUID buruhId,
            LocalDate startDate,
            LocalDate endDate,
            HarvestStatus status
    );

    HarvestDetailResponse getDetail(UUID harvestId);
}