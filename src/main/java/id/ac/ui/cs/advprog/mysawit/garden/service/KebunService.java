package id.ac.ui.cs.advprog.mysawit.garden.service;

import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunCreateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunSupirAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface KebunService {

    KebunDetailResponse createKebun(KebunCreateRequest request);

    List<KebunResponse> getAllKebun(String nama, String kode);

    KebunDetailResponse getKebunById(UUID id);

    KebunDetailResponse updateKebun(UUID id, KebunUpdateRequest request);

    KebunDetailResponse assignMandor(UUID kebunId, UUID mandorId);

    KebunSupirAssignmentResponse assignSupir(UUID kebunId, UUID supirId);

    void deleteKebun(UUID id);
}