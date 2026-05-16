package id.ac.ui.cs.advprog.mysawit.garden.service;

import id.ac.ui.cs.advprog.mysawit.garden.client.AuthClient;
import id.ac.ui.cs.advprog.mysawit.garden.client.dto.AuthUserResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunCreateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunSupirAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunUpdateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.exception.DuplicateKodeKebunException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.KebunHasMandorException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.KebunNotFoundException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.MandorAlreadyAssignedException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.SupirAlreadyAssignedException;
import id.ac.ui.cs.advprog.mysawit.garden.model.Kebun;
import id.ac.ui.cs.advprog.mysawit.garden.model.KebunSupir;
import id.ac.ui.cs.advprog.mysawit.garden.repository.KebunRepository;
import id.ac.ui.cs.advprog.mysawit.garden.repository.KebunSupirRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KebunServiceImplTest {

    @Mock
    private KebunRepository kebunRepository;

    @Mock
    private KebunSupirRepository kebunSupirRepository;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private KebunServiceImpl kebunService;

    private UUID kebunId;
    private UUID mandorId;
    private UUID supirId;
    private Kebun sampleKebun;
    private KebunCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        kebunId = UUID.randomUUID();
        mandorId = UUID.randomUUID();
        supirId = UUID.randomUUID();

        sampleKebun = Kebun.builder()
                .id(kebunId)
                .nama("Kebun Sawit Utara")
                .kode("KSU-001")
                .luasHektare(50.0)
                .coord1Lat(1.0).coord1Lng(101.0)
                .coord2Lat(1.0).coord2Lng(102.0)
                .coord3Lat(2.0).coord3Lng(102.0)
                .coord4Lat(2.0).coord4Lng(101.0)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        createRequest = KebunCreateRequest.builder()
                .nama("Kebun Sawit Utara")
                .kode("KSU-001")
                .luasHektare(50.0)
                .coord1Lat(1.0).coord1Lng(101.0)
                .coord2Lat(1.0).coord2Lng(102.0)
                .coord3Lat(2.0).coord3Lng(102.0)
                .coord4Lat(2.0).coord4Lng(101.0)
                .build();
    }

    // === CREATE ===

    @Test
    void createKebunSuccessReturnsDetailResponse() {
        when(kebunRepository.existsByKodeAndActiveTrue("KSU-001")).thenReturn(false);
        when(kebunRepository.existsOverlappingActiveKebun(anyString(), any())).thenReturn(false);
        when(kebunRepository.save(any(Kebun.class))).thenReturn(sampleKebun);
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(Collections.emptyList());

        KebunDetailResponse result = kebunService.createKebun(createRequest, null);

        assertNotNull(result);
        assertEquals("Kebun Sawit Utara", result.getNama());
        assertEquals("KSU-001", result.getKode());
        verify(kebunRepository).save(any(Kebun.class));
    }

    @Test
    void createKebunDuplicateKodeThrowsException() {
        when(kebunRepository.existsByKodeAndActiveTrue("KSU-001")).thenReturn(true);

        assertThrows(DuplicateKodeKebunException.class,
                () -> kebunService.createKebun(createRequest, null));

        verify(kebunRepository, never()).save(any());
    }

    // === GET ALL ===

    @Test
    void getAllKebunReturnsFilteredList() {
        when(kebunRepository.searchActiveKebun("Utara", null))
                .thenReturn(List.of(sampleKebun));
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(Collections.emptyList());

        List<KebunResponse> results = kebunService.getAllKebun("Utara", null, null);

        assertEquals(1, results.size());
        assertEquals("KSU-001", results.get(0).getKode());
    }

    @Test
    void getAllKebunEmptyResultReturnsEmptyList() {
        when(kebunRepository.searchActiveKebun(null, null))
                .thenReturn(Collections.emptyList());

        List<KebunResponse> results = kebunService.getAllKebun(null, null, null);

        assertTrue(results.isEmpty());
    }

    // === GET BY ID ===

    @Test
    void getKebunByIdFoundReturnsDetail() {
        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(Collections.emptyList());

        KebunDetailResponse result = kebunService.getKebunById(kebunId, null);

        assertNotNull(result);
        assertEquals(kebunId, result.getId());
    }

    @Test
    void getKebunByIdNotFoundThrowsException() {
        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.empty());

        assertThrows(KebunNotFoundException.class,
                () -> kebunService.getKebunById(kebunId, null));
    }

    // === UPDATE ===

    @Test
    void updateKebunPartialUpdateOnlyChangesProvidedFields() {
        KebunUpdateRequest updateRequest = KebunUpdateRequest.builder()
                .nama("Kebun Sawit Selatan")
                .build();

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(kebunRepository.existsOverlappingActiveKebun(anyString(), eq(kebunId)))
                .thenReturn(false);
        when(kebunRepository.save(any(Kebun.class))).thenReturn(sampleKebun);
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(Collections.emptyList());

        KebunDetailResponse result = kebunService.updateKebun(kebunId, updateRequest, null);

        assertNotNull(result);
        verify(kebunRepository).save(any(Kebun.class));
    }

    @Test
    void updateKebunNotFoundThrowsException() {
        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.empty());

        assertThrows(KebunNotFoundException.class,
                () -> kebunService.updateKebun(kebunId,
                        KebunUpdateRequest.builder().nama("New").build(), null));
    }

    // === ASSIGN MANDOR ===

    @Test
    void assignMandorSuccessSetsMandorId() {
        AuthUserResponse mandorUser = AuthUserResponse.builder()
                .id(mandorId.toString())
                .name("Budi Mandor")
                .role("MANDOR")
                .isActive(true)
                .build();

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(authClient.validateMandor(mandorId, "token"))
                .thenReturn(mandorUser);
        when(kebunRepository.existsByMandorIdAndActiveTrue(mandorId))
                .thenReturn(false);
        when(kebunRepository.save(any(Kebun.class))).thenReturn(sampleKebun);
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(Collections.emptyList());

        KebunDetailResponse result = kebunService.assignMandor(kebunId, mandorId, "token");

        assertNotNull(result);
        verify(kebunRepository).save(any(Kebun.class));
    }

    @Test
    void assignMandorAlreadyAssignedToOtherKebunThrowsException() {
        AuthUserResponse mandorUser = AuthUserResponse.builder()
                .id(mandorId.toString())
                .name("Budi Mandor")
                .role("MANDOR")
                .isActive(true)
                .build();

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(authClient.validateMandor(mandorId, "token"))
                .thenReturn(mandorUser);
        when(kebunRepository.existsByMandorIdAndActiveTrue(mandorId))
                .thenReturn(true);

        assertThrows(MandorAlreadyAssignedException.class,
                () -> kebunService.assignMandor(kebunId, mandorId, "token"));
    }

    @Test
    void assignMandorIdempotentReturnsSameKebun() {
        sampleKebun.setMandorId(mandorId);

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(Collections.emptyList());

        KebunDetailResponse result = kebunService.assignMandor(kebunId, mandorId, null);

        assertNotNull(result);
        verify(authClient, never()).validateMandor(any(), any());
    }

    // === UNASSIGN MANDOR ===

    @Test
    void unassignMandorSuccessClearsMandorId() {
        sampleKebun.setMandorId(mandorId);

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(kebunRepository.save(any(Kebun.class))).thenAnswer(inv -> {
            Kebun saved = inv.getArgument(0);
            assertNull(saved.getMandorId());
            return saved;
        });
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(Collections.emptyList());

        kebunService.unassignMandor(kebunId, null);

        verify(kebunRepository).save(any(Kebun.class));
    }

    @Test
    void unassignMandorNoMandorReturnsAsIs() {
        sampleKebun.setMandorId(null);

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(Collections.emptyList());

        KebunDetailResponse result = kebunService.unassignMandor(kebunId, null);

        assertNotNull(result);
        verify(kebunRepository, never()).save(any());
    }

    // === ASSIGN SUPIR ===

    @Test
    void assignSupirSuccessCreatesAssignment() {
        AuthUserResponse supirUser = AuthUserResponse.builder()
                .id(supirId.toString())
                .name("Dedi Supir")
                .role("SUPIR_TRUK")
                .isActive(true)
                .build();

        KebunSupir savedAssignment = KebunSupir.builder()
                .id(UUID.randomUUID())
                .kebunId(kebunId)
                .supirId(supirId)
                .active(true)
                .assignedAt(Instant.now())
                .build();

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(authClient.validateSupirTruk(supirId, "token"))
                .thenReturn(supirUser);
        when(kebunSupirRepository.existsBySupirIdAndActiveTrue(supirId))
                .thenReturn(false);
        when(kebunSupirRepository.save(any(KebunSupir.class)))
                .thenReturn(savedAssignment);

        KebunSupirAssignmentResponse result = kebunService.assignSupir(kebunId, supirId, "token");

        assertNotNull(result);
        assertEquals(kebunId, result.getKebunId());
        assertEquals(supirId, result.getSupirId());
        assertTrue(result.getIsActive());
    }

    @Test
    void assignSupirAlreadyAssignedThrowsException() {
        AuthUserResponse supirUser = AuthUserResponse.builder()
                .id(supirId.toString())
                .name("Dedi Supir")
                .role("SUPIR_TRUK")
                .isActive(true)
                .build();

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(authClient.validateSupirTruk(supirId, "token"))
                .thenReturn(supirUser);
        when(kebunSupirRepository.existsBySupirIdAndActiveTrue(supirId))
                .thenReturn(true);

        assertThrows(SupirAlreadyAssignedException.class,
                () -> kebunService.assignSupir(kebunId, supirId, "token"));
    }

    // === UNASSIGN SUPIR ===

    @Test
    void unassignSupirSuccessDeactivatesAssignment() {
        KebunSupir assignment = KebunSupir.builder()
                .id(UUID.randomUUID())
                .kebunId(kebunId)
                .supirId(supirId)
                .active(true)
                .build();

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(List.of(assignment));

        kebunService.unassignSupir(kebunId, supirId);

        assertFalse(assignment.getActive());
        assertNotNull(assignment.getUnassignedAt());
        verify(kebunSupirRepository).save(assignment);
    }

    @Test
    void unassignSupirNotFoundThrowsException() {
        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(Collections.emptyList());

        assertThrows(KebunNotFoundException.class,
                () -> kebunService.unassignSupir(kebunId, supirId));
    }

    // === DELETE ===

    @Test
    void deleteKebunSuccessSoftDeletes() {
        sampleKebun.setMandorId(null);

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(Collections.emptyList());

        kebunService.deleteKebun(kebunId);

        assertFalse(sampleKebun.getActive());
        verify(kebunRepository).save(sampleKebun);
    }

    @Test
    void deleteKebunHasMandorThrowsException() {
        sampleKebun.setMandorId(mandorId);

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));

        assertThrows(KebunHasMandorException.class,
                () -> kebunService.deleteKebun(kebunId));
    }

    @Test
    void deleteKebunDeactivatesActiveSupirAssignments() {
        sampleKebun.setMandorId(null);

        KebunSupir activeSupir = KebunSupir.builder()
                .id(UUID.randomUUID())
                .kebunId(kebunId)
                .supirId(supirId)
                .active(true)
                .build();

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(List.of(activeSupir));

        kebunService.deleteKebun(kebunId);

        assertFalse(activeSupir.getActive());
        assertNotNull(activeSupir.getUnassignedAt());
    }

    // === RESPONSE ENRICHMENT ===

    @Test
    void toDetailResponseWithTokenEnrichesMandorInfo() {
        sampleKebun.setMandorId(mandorId);

        AuthUserResponse mandorUser = AuthUserResponse.builder()
                .id(mandorId.toString())
                .name("Budi Mandor")
                .email("budi@sawit.com")
                .role("MANDOR")
                .isActive(true)
                .build();

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(authClient.getUserById(mandorId, "token"))
                .thenReturn(mandorUser);
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(Collections.emptyList());

        KebunDetailResponse result = kebunService.getKebunById(kebunId, "token");

        assertEquals("Budi Mandor", result.getMandorName());
        assertEquals("budi@sawit.com", result.getMandorEmail());
    }

    @Test
    void toDetailResponseAuthFailsReturnsNullMandorInfo() {
        sampleKebun.setMandorId(mandorId);

        when(kebunRepository.findByIdAndActiveTrue(kebunId))
                .thenReturn(Optional.of(sampleKebun));
        when(authClient.getUserById(mandorId, "token"))
                .thenThrow(new RuntimeException("Auth unavailable"));
        when(kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId))
                .thenReturn(Collections.emptyList());

        KebunDetailResponse result = kebunService.getKebunById(kebunId, "token");

        assertNull(result.getMandorName());
        assertNull(result.getMandorEmail());
    }
}
