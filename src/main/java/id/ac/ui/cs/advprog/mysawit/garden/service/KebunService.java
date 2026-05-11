package id.ac.ui.cs.advprog.mysawit.garden.service;

import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunCreateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunSupirAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunUpdateRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service interface untuk operasi kebun.
 * Milestone 75%: ditambah parameter token untuk integrasi Auth Service
 * dan method unassign mandor/supir.
 */
public interface KebunService {

    KebunDetailResponse createKebun(KebunCreateRequest request, String token);

    List<KebunResponse> getAllKebun(String nama, String kode, String token);

    KebunDetailResponse getKebunById(UUID id, String token);

    KebunDetailResponse updateKebun(UUID id, KebunUpdateRequest request, String token);

    KebunDetailResponse assignMandor(UUID kebunId, UUID mandorId, String token);

    KebunDetailResponse unassignMandor(UUID kebunId, String token);

    KebunSupirAssignmentResponse assignSupir(UUID kebunId, UUID supirId, String token);

    void unassignSupir(UUID kebunId, UUID supirId);

    void deleteKebun(UUID id);
}