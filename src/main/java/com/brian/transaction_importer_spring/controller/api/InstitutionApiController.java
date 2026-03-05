package com.brian.transaction_importer_spring.controller.api;

import com.brian.transaction_importer_spring.dto.InstitutionDTO;
import com.brian.transaction_importer_spring.entity.Institution;
import com.brian.transaction_importer_spring.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/institutions")
public class InstitutionApiController {

    private final InstitutionRepository institutionRepository;

    @GetMapping("/")
    public List<InstitutionDTO> findAllInstitutions() {
        return institutionRepository.findAll().stream().map(InstitutionDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public InstitutionDTO findInstitutionJsonById(@PathVariable final Long id) {
        Institution institution = institutionRepository.findById(id).get();
        if (institution == null) {
            return null;
        }

        return new InstitutionDTO(institution);
    }

    @PutMapping
    public InstitutionDTO updateInstitution(@RequestBody final InstitutionDTO institutionDTO) {
        Institution institution = institutionRepository.findById(institutionDTO.getId()).get();
        institution.setName(institutionDTO.getName());
        return new InstitutionDTO(institutionRepository.save(institution));
    }

    @PostMapping
    public InstitutionDTO createInstitution(@RequestBody final InstitutionDTO institutionDTO) {
        Institution institution = new Institution();
        institution.setName(institutionDTO.getName());
        return new InstitutionDTO(institutionRepository.save(institution));
    }
}

