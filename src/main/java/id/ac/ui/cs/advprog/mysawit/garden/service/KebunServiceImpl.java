package id.ac.ui.cs.advprog.mysawit.garden.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class KebunServiceImpl implements KebunService {

    private final KebunRepository kebunRepository;
    private final KebunSupirRepository kebunSupirRepository;

    public KebunServiceImpl(KebunRepository kebunRepository, KebunSupirRepository kebunSupirRepository) {
        this.kebunRepository = kebunRepository;
        this.kebunSupirRepository = kebunSupirRepository;
    }

    @Override
    @Transactional
    public KebunDetailResponse createKebun(KebunCreateRequest request) {
        if (kebunRepository.existsByKodeAndActiveTrue(request.getKode())) {
            throw new DuplicateKodeKebunException(request.getKode());
        }

        Kebun kebun = Kebun.builder()
                .nama(request.getNama())
                .kode(request.getKode())
                .luasHektare(request.getLuasHektare())
                .coord1Lat(request.getCoord1Lat())
                .coord1Lng(request.getCoord1Lng())
                .coord2Lat(request.getCoord2Lat())
                .coord2Lng(request.getCoord2Lng())
                .coord3Lat(request.getCoord3Lat())
                .coord3Lng(request.getCoord3Lng())
                .coord4Lat(request.getCoord4Lat())
                .coord4Lng(request.getCoord4Lng())
                .build();

            validatePolygonAndOverlap(kebun, null);

        Kebun saved = kebunRepository.save(kebun);
        return toDetailResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KebunResponse> getAllKebun(String nama, String kode) {
        List<Kebun> kebuns = kebunRepository.searchActiveKebun(nama, kode);
        return kebuns.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public KebunDetailResponse getKebunById(UUID id) {
        Kebun kebun = findActiveKebunOrThrow(id);
        return toDetailResponse(kebun);
    }

    @Override
    @Transactional
    public KebunDetailResponse updateKebun(UUID id, KebunUpdateRequest request) {
        Kebun kebun = findActiveKebunOrThrow(id);

        if (request.getNama() != null) {
            kebun.setNama(request.getNama());
        }
        if (request.getLuasHektare() != null) {
            kebun.setLuasHektare(request.getLuasHektare());
        }
        if (request.getCoord1Lat() != null) {
            kebun.setCoord1Lat(request.getCoord1Lat());
        }
        if (request.getCoord1Lng() != null) {
            kebun.setCoord1Lng(request.getCoord1Lng());
        }
        if (request.getCoord2Lat() != null) {
            kebun.setCoord2Lat(request.getCoord2Lat());
        }
        if (request.getCoord2Lng() != null) {
            kebun.setCoord2Lng(request.getCoord2Lng());
        }
        if (request.getCoord3Lat() != null) {
            kebun.setCoord3Lat(request.getCoord3Lat());
        }
        if (request.getCoord3Lng() != null) {
            kebun.setCoord3Lng(request.getCoord3Lng());
        }
        if (request.getCoord4Lat() != null) {
            kebun.setCoord4Lat(request.getCoord4Lat());
        }
        if (request.getCoord4Lng() != null) {
            kebun.setCoord4Lng(request.getCoord4Lng());
        }

        validatePolygonAndOverlap(kebun, id);

        Kebun saved = kebunRepository.save(kebun);
        return toDetailResponse(saved);
    }

    @Override
    @Transactional
    public KebunDetailResponse assignMandor(UUID kebunId, UUID mandorId) {
        Kebun kebun = findActiveKebunOrThrow(kebunId);

        if (mandorId.equals(kebun.getMandorId())) {
            return toDetailResponse(kebun);
        }

        if (kebunRepository.existsByMandorIdAndActiveTrue(mandorId)) {
            throw new MandorAlreadyAssignedException(mandorId);
        }

        kebun.setMandorId(mandorId);
        Kebun saved = kebunRepository.save(kebun);
        return toDetailResponse(saved);
    }

    @Override
    @Transactional
    public KebunSupirAssignmentResponse assignSupir(UUID kebunId, UUID supirId) {
        findActiveKebunOrThrow(kebunId);

        if (kebunSupirRepository.existsBySupirIdAndActiveTrue(supirId)) {
            throw new SupirAlreadyAssignedException(supirId);
        }

        KebunSupir assignment = KebunSupir.builder()
                .kebunId(kebunId)
                .supirId(supirId)
                .build();

        KebunSupir saved = kebunSupirRepository.save(assignment);
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
    public void deleteKebun(UUID id) {
        Kebun kebun = findActiveKebunOrThrow(id);

        if (kebun.getMandorId() != null) {
            throw new KebunHasMandorException(id);
        }

        kebun.setActive(false);
        kebunRepository.save(kebun);
    }

    private Kebun findActiveKebunOrThrow(UUID id) {
        return kebunRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new KebunNotFoundException(id));
    }

    private KebunResponse toResponse(Kebun kebun) {
        return KebunResponse.builder()
                .id(kebun.getId())
                .nama(kebun.getNama())
                .kode(kebun.getKode())
                .luasHektare(kebun.getLuasHektare())
                .mandorId(kebun.getMandorId())
                .isActive(kebun.getActive())
                .createdAt(kebun.getCreatedAt())
                .build();
    }

    private void validatePolygonAndOverlap(Kebun kebun, UUID excludeId) {
        if (!isValidPolygon(kebun)) {
            throw new InvalidKebunPolygonException();
        }

        String polygonWkt = toPolygonWkt(kebun);
        boolean overlap = kebunRepository.existsOverlappingActiveKebun(polygonWkt, excludeId);
        if (overlap) {
            throw new KebunOverlapException();
        }
    }

    private boolean isValidPolygon(Kebun kebun) {
        if (kebun.getCoord1Lat() == null || kebun.getCoord1Lng() == null
                || kebun.getCoord2Lat() == null || kebun.getCoord2Lng() == null
                || kebun.getCoord3Lat() == null || kebun.getCoord3Lng() == null
                || kebun.getCoord4Lat() == null || kebun.getCoord4Lng() == null) {
            return false;
        }

        Set<String> uniquePoints = new HashSet<>();
        uniquePoints.add(kebun.getCoord1Lat() + "," + kebun.getCoord1Lng());
        uniquePoints.add(kebun.getCoord2Lat() + "," + kebun.getCoord2Lng());
        uniquePoints.add(kebun.getCoord3Lat() + "," + kebun.getCoord3Lng());
        uniquePoints.add(kebun.getCoord4Lat() + "," + kebun.getCoord4Lng());
        if (uniquePoints.size() < 4) {
            return false;
        }

        double[] xs = {
                kebun.getCoord1Lng(), kebun.getCoord2Lng(), kebun.getCoord3Lng(), kebun.getCoord4Lng()
        };
        double[] ys = {
                kebun.getCoord1Lat(), kebun.getCoord2Lat(), kebun.getCoord3Lat(), kebun.getCoord4Lat()
        };

        double twiceArea = 0.0;
        for (int i = 0; i < 4; i++) {
            int next = (i + 1) % 4;
            twiceArea += xs[i] * ys[next] - xs[next] * ys[i];
        }

        return Math.abs(twiceArea) > 1e-12;
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

    private KebunDetailResponse toDetailResponse(Kebun kebun) {
        List<UUID> supirIds = kebunSupirRepository.findByKebunIdAndActiveTrue(kebun.getId())
                .stream()
                .map(KebunSupir::getSupirId)
                .toList();

        return KebunDetailResponse.builder()
                .id(kebun.getId())
                .nama(kebun.getNama())
                .kode(kebun.getKode())
                .luasHektare(kebun.getLuasHektare())
                .coord1Lat(kebun.getCoord1Lat())
                .coord1Lng(kebun.getCoord1Lng())
                .coord2Lat(kebun.getCoord2Lat())
                .coord2Lng(kebun.getCoord2Lng())
                .coord3Lat(kebun.getCoord3Lat())
                .coord3Lng(kebun.getCoord3Lng())
                .coord4Lat(kebun.getCoord4Lat())
                .coord4Lng(kebun.getCoord4Lng())
                .mandorId(kebun.getMandorId())
                .supirIds(supirIds)
                .isActive(kebun.getActive())
                .createdAt(kebun.getCreatedAt())
                .updatedAt(kebun.getUpdatedAt())
                .build();
    }
}
