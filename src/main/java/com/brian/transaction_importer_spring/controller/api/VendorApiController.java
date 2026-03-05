package com.brian.transaction_importer_spring.controller.api;

import com.brian.transaction_importer_spring.dto.VendorDTO;
import com.brian.transaction_importer_spring.entity.Vendor;
import com.brian.transaction_importer_spring.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping
    public VendorDTO updateVendor(@RequestBody final VendorDTO vendorDTO) {
        Vendor vendor = vendorRepository.findById(vendorDTO.getId()).get();
        vendor.setName(vendorDTO.getName());
        vendor.setAliases(vendorDTO.getAliases());
        return new VendorDTO(vendorRepository.save(vendor));
    }

    @PostMapping
    public VendorDTO createVendor(@RequestBody final VendorDTO vendorDTO) {
        Vendor vendor = new Vendor();
        vendor.setName(vendorDTO.getName());
        vendor.setAliases(vendorDTO.getAliases());
        return new VendorDTO(vendorRepository.save(vendor));
    }
}
