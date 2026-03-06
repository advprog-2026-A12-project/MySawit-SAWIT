package id.ac.ui.cs.advprog.mysawit.garden.service;

import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunCreateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunUpdateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.exception.DuplicateKodeKebunException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.KebunHasMandorException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.KebunNotFoundException;
import id.ac.ui.cs.advprog.mysawit.garden.model.Kebun;
import id.ac.ui.cs.advprog.mysawit.garden.repository.KebunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class KebunServiceImpl implements KebunService {

    private final KebunRepository kebunRepository;

    public KebunServiceImpl(KebunRepository kebunRepository) {
        this.kebunRepository = kebunRepository;
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

        Kebun saved = kebunRepository.save(kebun);
        return toDetailResponse(saved);
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

    private KebunDetailResponse toDetailResponse(Kebun kebun) {
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
                .isActive(kebun.getActive())
                .createdAt(kebun.getCreatedAt())
                .updatedAt(kebun.getUpdatedAt())
                .build();
    }
}
