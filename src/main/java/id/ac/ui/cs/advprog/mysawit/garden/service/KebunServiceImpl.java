package id.ac.ui.cs.advprog.mysawit.garden.service;

import id.ac.ui.cs.advprog.mysawit.garden.client.AuthClient;
import id.ac.ui.cs.advprog.mysawit.garden.client.dto.AuthUserResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunCreateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunSupirAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunUpdateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.exception.DuplicateKodeKebunException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.InvalidKebunPolygonException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.KebunHasMandorException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.KebunNotFoundException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.KebunOverlapException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.MandorAlreadyAssignedException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.SupirAlreadyAssignedException;
import id.ac.ui.cs.advprog.mysawit.garden.model.Kebun;
import id.ac.ui.cs.advprog.mysawit.garden.model.KebunSupir;
import id.ac.ui.cs.advprog.mysawit.garden.repository.KebunRepository;
import id.ac.ui.cs.advprog.mysawit.garden.repository.KebunSupirRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class KebunServiceImpl implements KebunService {

    private static final Logger log = LoggerFactory.getLogger(KebunServiceImpl.class);

    private final KebunRepository kebunRepository;
    private final KebunSupirRepository kebunSupirRepository;
    private final AuthClient authClient;

    public KebunServiceImpl(
            KebunRepository kebunRepository,
            KebunSupirRepository kebunSupirRepository,
            @Qualifier("gardenAuthClient") AuthClient authClient) {
        this.kebunRepository = kebunRepository;
        this.kebunSupirRepository = kebunSupirRepository;
        this.authClient = authClient;
    }

    @Override
    @Transactional
    public KebunDetailResponse createKebun(KebunCreateRequest request, String token) {
        if (kebunRepository.existsByKodeAndActiveTrue(request.getKode())) {
            throw new DuplicateKodeKebunException(request.getKode());
        }

        Kebun kebun = buildKebunFromRequest(request);
        validatePolygonAndOverlap(kebun, null);

        Kebun saved = kebunRepository.save(kebun);
        log.info("Kebun created: id={}, kode={}", saved.getId(), saved.getKode());
        return toDetailResponse(saved, token);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KebunResponse> getAllKebun(String nama, String kode, String token) {
        return kebunRepository.searchActiveKebun(nama, kode).stream()
                .map(kebun -> toResponse(kebun, token))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public KebunDetailResponse getKebunById(UUID id, String token) {
        Kebun kebun = findActiveKebunOrThrow(id);
        return toDetailResponse(kebun, token);
    }

    @Override
    @Transactional
    public KebunDetailResponse updateKebun(UUID id, KebunUpdateRequest request, String token) {
        Kebun kebun = findActiveKebunOrThrow(id);
        applyUpdates(kebun, request);
        validatePolygonAndOverlap(kebun, id);

        Kebun saved = kebunRepository.save(kebun);
        log.info("Kebun updated: id={}", saved.getId());
        return toDetailResponse(saved, token);
    }

    @Override
    @Transactional
    public KebunDetailResponse assignMandor(UUID kebunId, UUID mandorId, String token) {
        Kebun kebun = findActiveKebunOrThrow(kebunId);

        if (mandorId.equals(kebun.getMandorId())) {
            return toDetailResponse(kebun, token);
        }

        AuthUserResponse mandor = authClient.validateMandor(mandorId, token);

        if (kebunRepository.existsByMandorIdAndActiveTrue(mandorId)) {
            throw new MandorAlreadyAssignedException(mandor.getName());
        }

        kebun.setMandorId(mandorId);
        Kebun saved = kebunRepository.save(kebun);
        log.info("Mandor assigned to kebun: kebunId={}, mandorId={}", kebunId, mandorId);
        return toDetailResponse(saved, token);
    }

    @Override
    @Transactional
    public KebunDetailResponse unassignMandor(UUID kebunId, String token) {
        Kebun kebun = findActiveKebunOrThrow(kebunId);

        if (kebun.getMandorId() == null) {
            return toDetailResponse(kebun, token);
        }

        log.info("Mandor unassigned from kebun: kebunId={}", kebunId);
        kebun.setMandorId(null);
        Kebun saved = kebunRepository.save(kebun);
        return toDetailResponse(saved, token);
    }

    @Override
    @Transactional
    public KebunSupirAssignmentResponse assignSupir(UUID kebunId, UUID supirId, String token) {
        findActiveKebunOrThrow(kebunId);

        AuthUserResponse supir = authClient.validateSupirTruk(supirId, token);

        if (kebunSupirRepository.existsBySupirIdAndActiveTrue(supirId)) {
            throw new SupirAlreadyAssignedException(supir.getName());
        }

        KebunSupir assignment = KebunSupir.builder()
                .kebunId(kebunId)
                .supirId(supirId)
                .build();

        KebunSupir saved = kebunSupirRepository.save(assignment);
        log.info("Supir assigned to kebun: kebunId={}, supirId={}", kebunId, supirId);

        return KebunSupirAssignmentResponse.builder()
                .assignmentId(saved.getId())
                .kebunId(saved.getKebunId())
                .supirId(saved.getSupirId())
                .isActive(saved.getActive())
                .assignedAt(saved.getAssignedAt())
                .build();
    }

    @Override
    @Transactional
    public void unassignSupir(UUID kebunId, UUID supirId) {
        findActiveKebunOrThrow(kebunId);

        KebunSupir assignment = kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId).stream()
                .filter(a -> a.getSupirId().equals(supirId))
                .findFirst()
                .orElseThrow(() -> new KebunNotFoundException(kebunId));

        assignment.setActive(false);
        assignment.setUnassignedAt(Instant.now());
        kebunSupirRepository.save(assignment);
        log.info("Supir unassigned from kebun: kebunId={}, supirId={}", kebunId, supirId);
    }

    @Override
    @Transactional
    public void deleteKebun(UUID id) {
        Kebun kebun = findActiveKebunOrThrow(id);

        if (kebun.getMandorId() != null) {
            throw new KebunHasMandorException(kebun.getNama());
        }

        deactivateAllSupirAssignments(id);
        kebun.setActive(false);
        kebunRepository.save(kebun);
        log.info("Kebun soft-deleted: id={}", id);
    }

    // === Private Helpers ===

    private Kebun findActiveKebunOrThrow(UUID id) {
        return kebunRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new KebunNotFoundException(id));
    }

    private Kebun buildKebunFromRequest(KebunCreateRequest request) {
        return Kebun.builder()
                .nama(request.getNama())
                .kode(request.getKode())
                .luasHektare(request.getLuasHektare())
                .coord1Lat(request.getCoord1Lat()).coord1Lng(request.getCoord1Lng())
                .coord2Lat(request.getCoord2Lat()).coord2Lng(request.getCoord2Lng())
                .coord3Lat(request.getCoord3Lat()).coord3Lng(request.getCoord3Lng())
                .coord4Lat(request.getCoord4Lat()).coord4Lng(request.getCoord4Lng())
                .build();
    }

    private void applyUpdates(Kebun kebun, KebunUpdateRequest request) {
        if (request.getNama() != null)      kebun.setNama(request.getNama());
        if (request.getLuasHektare() != null) kebun.setLuasHektare(request.getLuasHektare());
        if (request.getCoord1Lat() != null) kebun.setCoord1Lat(request.getCoord1Lat());
        if (request.getCoord1Lng() != null) kebun.setCoord1Lng(request.getCoord1Lng());
        if (request.getCoord2Lat() != null) kebun.setCoord2Lat(request.getCoord2Lat());
        if (request.getCoord2Lng() != null) kebun.setCoord2Lng(request.getCoord2Lng());
        if (request.getCoord3Lat() != null) kebun.setCoord3Lat(request.getCoord3Lat());
        if (request.getCoord3Lng() != null) kebun.setCoord3Lng(request.getCoord3Lng());
        if (request.getCoord4Lat() != null) kebun.setCoord4Lat(request.getCoord4Lat());
        if (request.getCoord4Lng() != null) kebun.setCoord4Lng(request.getCoord4Lng());
    }

    private void deactivateAllSupirAssignments(UUID kebunId) {
        List<KebunSupir> activeSupirs = kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId);
        Instant now = Instant.now();
        activeSupirs.forEach(ks -> {
            ks.setActive(false);
            ks.setUnassignedAt(now);
        });
        kebunSupirRepository.saveAll(activeSupirs);
    }

    private KebunResponse toResponse(Kebun kebun, String token) {
        String mandorName = null;
        if (kebun.getMandorId() != null && token != null) {
            mandorName = fetchUserNameSafe(kebun.getMandorId(), token);
        }

        int totalSupir = kebunSupirRepository.findByKebunIdAndActiveTrue(kebun.getId()).size();

        return KebunResponse.builder()
                .id(kebun.getId())
                .nama(kebun.getNama())
                .kode(kebun.getKode())
                .luasHektare(kebun.getLuasHektare())
                .mandorId(kebun.getMandorId())
                .mandorName(mandorName)
                .totalSupir(totalSupir)
                .isActive(kebun.getActive())
                .createdAt(kebun.getCreatedAt())
                .build();
    }

    private KebunDetailResponse toDetailResponse(Kebun kebun, String token) {
        String mandorName = null;
        String mandorEmail = null;
        if (kebun.getMandorId() != null && token != null) {
            try {
                AuthUserResponse mandorUser = authClient.getUserById(kebun.getMandorId(), token);
                mandorName = mandorUser.getName();
                mandorEmail = mandorUser.getEmail();
            } catch (Exception e) {
                log.warn("Gagal mengambil detail mandor: {}", e.getMessage());
            }
        }

        List<KebunDetailResponse.SupirDetail> supirDetails = buildSupirDetailList(kebun.getId(), token);

        return KebunDetailResponse.builder()
                .id(kebun.getId())
                .nama(kebun.getNama())
                .kode(kebun.getKode())
                .luasHektare(kebun.getLuasHektare())
                .coord1Lat(kebun.getCoord1Lat()).coord1Lng(kebun.getCoord1Lng())
                .coord2Lat(kebun.getCoord2Lat()).coord2Lng(kebun.getCoord2Lng())
                .coord3Lat(kebun.getCoord3Lat()).coord3Lng(kebun.getCoord3Lng())
                .coord4Lat(kebun.getCoord4Lat()).coord4Lng(kebun.getCoord4Lng())
                .mandorId(kebun.getMandorId())
                .mandorName(mandorName)
                .mandorEmail(mandorEmail)
                .supirList(supirDetails)
                .totalSupir(supirDetails.size())
                .isActive(kebun.getActive())
                .createdAt(kebun.getCreatedAt())
                .updatedAt(kebun.getUpdatedAt())
                .build();
    }

    private List<KebunDetailResponse.SupirDetail> buildSupirDetailList(UUID kebunId, String token) {
        List<KebunSupir> activeSupirs = kebunSupirRepository.findByKebunIdAndActiveTrue(kebunId);
        List<KebunDetailResponse.SupirDetail> details = new ArrayList<>();

        for (KebunSupir ks : activeSupirs) {
            KebunDetailResponse.SupirDetail detail = KebunDetailResponse.SupirDetail.builder()
                    .id(ks.getSupirId())
                    .assignedAt(ks.getAssignedAt())
                    .build();

            if (token != null) {
                try {
                    AuthUserResponse supirUser = authClient.getUserById(ks.getSupirId(), token);
                    detail.setName(supirUser.getName());
                    detail.setEmail(supirUser.getEmail());
                } catch (Exception e) {
                    log.warn("Gagal mengambil detail supir: {}", e.getMessage());
                }
            }
            details.add(detail);
        }
        return details;
    }

    private String fetchUserNameSafe(UUID userId, String token) {
        try {
            return authClient.getUserById(userId, token).getName();
        } catch (Exception e) {
            log.warn("Gagal mengambil nama user: {}", e.getMessage());
            return null;
        }
    }

    // === Polygon Validation ===

    private void validatePolygonAndOverlap(Kebun kebun, UUID excludeId) {
        if (!isValidPolygon(kebun)) {
            throw new InvalidKebunPolygonException();
        }
        if (kebunRepository.existsOverlappingActiveKebun(toPolygonWkt(kebun), excludeId)) {
            throw new KebunOverlapException();
        }
    }

    private boolean isValidPolygon(Kebun kebun) {
        if (hasNullCoordinates(kebun)) return false;
        if (countUniquePoints(kebun) < 4) return false;
        return Math.abs(computeTwiceArea(kebun)) > 1e-12;
    }

    private boolean hasNullCoordinates(Kebun kebun) {
        return kebun.getCoord1Lat() == null || kebun.getCoord1Lng() == null
            || kebun.getCoord2Lat() == null || kebun.getCoord2Lng() == null
            || kebun.getCoord3Lat() == null || kebun.getCoord3Lng() == null
            || kebun.getCoord4Lat() == null || kebun.getCoord4Lng() == null;
    }

    private int countUniquePoints(Kebun kebun) {
        Set<String> points = new HashSet<>();
        points.add(kebun.getCoord1Lat() + "," + kebun.getCoord1Lng());
        points.add(kebun.getCoord2Lat() + "," + kebun.getCoord2Lng());
        points.add(kebun.getCoord3Lat() + "," + kebun.getCoord3Lng());
        points.add(kebun.getCoord4Lat() + "," + kebun.getCoord4Lng());
        return points.size();
    }

    private double computeTwiceArea(Kebun kebun) {
        double[] xs = { kebun.getCoord1Lng(), kebun.getCoord2Lng(), kebun.getCoord3Lng(), kebun.getCoord4Lng() };
        double[] ys = { kebun.getCoord1Lat(), kebun.getCoord2Lat(), kebun.getCoord3Lat(), kebun.getCoord4Lat() };
        double area = 0.0;
        for (int i = 0; i < 4; i++) {
            int next = (i + 1) % 4;
            area += xs[i] * ys[next] - xs[next] * ys[i];
        }
        return area;
    }

    private String toPolygonWkt(Kebun kebun) {
        return String.format(
                "POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",
                kebun.getCoord1Lng(), kebun.getCoord1Lat(),
                kebun.getCoord2Lng(), kebun.getCoord2Lat(),
                kebun.getCoord3Lng(), kebun.getCoord3Lat(),
                kebun.getCoord4Lng(), kebun.getCoord4Lat(),
                kebun.getCoord1Lng(), kebun.getCoord1Lat()
        );
    }
}
