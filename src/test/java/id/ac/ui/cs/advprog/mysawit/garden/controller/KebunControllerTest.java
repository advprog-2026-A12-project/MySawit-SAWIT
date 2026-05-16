package id.ac.ui.cs.advprog.mysawit.garden.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.mysawit.garden.dto.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.garden.dto.AssignSupirRequest;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunCreateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunSupirAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunUpdateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.exception.KebunExceptionHandler;
import id.ac.ui.cs.advprog.mysawit.garden.exception.KebunNotFoundException;
import id.ac.ui.cs.advprog.mysawit.garden.service.KebunService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class KebunControllerTest {

    @Mock
    private KebunService kebunService;

    @InjectMocks
    private KebunController kebunController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UUID kebunId;
    private KebunDetailResponse detailResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(kebunController)
                .setControllerAdvice(new KebunExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        kebunId = UUID.randomUUID();
        detailResponse = KebunDetailResponse.builder()
                .id(kebunId)
                .nama("Kebun Test")
                .kode("KT-001")
                .luasHektare(25.0)
                .coord1Lat(1.0).coord1Lng(101.0)
                .coord2Lat(1.0).coord2Lng(102.0)
                .coord3Lat(2.0).coord3Lng(102.0)
                .coord4Lat(2.0).coord4Lng(101.0)
                .isActive(true)
                .supirList(Collections.emptyList())
                .totalSupir(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createKebunReturns201() throws Exception {
        KebunCreateRequest request = KebunCreateRequest.builder()
                .nama("Kebun Test")
                .kode("KT-001")
                .luasHektare(25.0)
                .coord1Lat(1.0).coord1Lng(101.0)
                .coord2Lat(1.0).coord2Lng(102.0)
                .coord3Lat(2.0).coord3Lng(102.0)
                .coord4Lat(2.0).coord4Lng(101.0)
                .build();

        when(kebunService.createKebun(any(), isNull())).thenReturn(detailResponse);

        mockMvc.perform(post("/api/kebun")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.nama").value("Kebun Test"));
    }

    @Test
    void getAllKebunReturns200() throws Exception {
        KebunResponse summary = KebunResponse.builder()
                .id(kebunId)
                .nama("Kebun Test")
                .kode("KT-001")
                .luasHektare(25.0)
                .isActive(true)
                .totalSupir(0)
                .build();

        when(kebunService.getAllKebun(isNull(), isNull(), isNull()))
                .thenReturn(List.of(summary));

        mockMvc.perform(get("/api/kebun"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data[0].kode").value("KT-001"));
    }

    @Test
    void getKebunByIdReturns200() throws Exception {
        when(kebunService.getKebunById(eq(kebunId), isNull()))
                .thenReturn(detailResponse);

        mockMvc.perform(get("/api/kebun/{id}", kebunId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(kebunId.toString()));
    }

    @Test
    void getKebunByIdNotFoundReturns404() throws Exception {
        when(kebunService.getKebunById(eq(kebunId), isNull()))
                .thenThrow(new KebunNotFoundException(kebunId));

        mockMvc.perform(get("/api/kebun/{id}", kebunId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void updateKebunReturns200() throws Exception {
        KebunUpdateRequest updateRequest = KebunUpdateRequest.builder()
                .nama("Updated Name")
                .luasHektare(30.0)
                .coord1Lat(1.0).coord1Lng(101.0)
                .coord2Lat(1.0).coord2Lng(102.0)
                .coord3Lat(2.0).coord3Lng(102.0)
                .coord4Lat(2.0).coord4Lng(101.0)
                .build();

        when(kebunService.updateKebun(eq(kebunId), any(), isNull()))
                .thenReturn(detailResponse);

        mockMvc.perform(put("/api/kebun/{id}", kebunId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void deleteKebunReturns200() throws Exception {
        doNothing().when(kebunService).deleteKebun(kebunId);

        mockMvc.perform(delete("/api/kebun/{id}", kebunId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void deleteKebunNotFoundReturns404() throws Exception {
        doThrow(new KebunNotFoundException(kebunId))
                .when(kebunService).deleteKebun(kebunId);

        mockMvc.perform(delete("/api/kebun/{id}", kebunId))
                .andExpect(status().isNotFound());
    }

    @Test
    void assignMandorReturns200() throws Exception {
        UUID mandorId = UUID.randomUUID();
        AssignMandorRequest request = new AssignMandorRequest();
        request.setMandorId(mandorId);

        when(kebunService.assignMandor(eq(kebunId), eq(mandorId), isNull()))
                .thenReturn(detailResponse);

        mockMvc.perform(post("/api/kebun/{id}/assign-mandor", kebunId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void assignSupirReturns201() throws Exception {
        UUID supirId = UUID.randomUUID();
        AssignSupirRequest request = new AssignSupirRequest();
        request.setSupirId(supirId);

        KebunSupirAssignmentResponse assignResponse = KebunSupirAssignmentResponse.builder()
                .assignmentId(UUID.randomUUID())
                .kebunId(kebunId)
                .supirId(supirId)
                .isActive(true)
                .assignedAt(Instant.now())
                .build();

        when(kebunService.assignSupir(eq(kebunId), eq(supirId), isNull()))
                .thenReturn(assignResponse);

        mockMvc.perform(post("/api/kebun/{id}/assign-supir", kebunId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void unassignSupirReturns200() throws Exception {
        UUID supirId = UUID.randomUUID();
        doNothing().when(kebunService).unassignSupir(kebunId, supirId);

        mockMvc.perform(delete("/api/kebun/{id}/supir/{supirId}", kebunId, supirId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}
