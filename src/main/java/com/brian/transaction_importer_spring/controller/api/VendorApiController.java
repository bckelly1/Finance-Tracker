package com.brian.transaction_importer_spring.controller.api;

import com.brian.transaction_importer_spring.dto.VendorDTO;
import com.brian.transaction_importer_spring.entity.Vendor;
import com.brian.transaction_importer_spring.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vendors")
public class VendorApiController {

    private final VendorRepository vendorRepository;

    @GetMapping("/")
    public List<VendorDTO> findAllVendors() {
        return vendorRepository.findAll().stream().map(VendorDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public VendorDTO findTransactionJsonById(@PathVariable final Long id) {
        Vendor vendor = vendorRepository.findById(id).get();
        if (vendor == null) {
            return null;
        }

        return new VendorDTO(vendor);
    }
}
