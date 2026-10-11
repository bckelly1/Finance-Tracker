package com.brian.transaction_importer_spring.controller.api;

import com.brian.transaction_importer_spring.dto.BillDTO;
import com.brian.transaction_importer_spring.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    @GetMapping("/{id}")
    public BillDTO getStatus(@PathVariable Long id) {
        return billService.getBill(id);
    }

    @GetMapping("/all")
    public List<BillDTO> getAllBills() {
        return billService.getAllBills();
    }

    @PostMapping
    public BillDTO createBill(@RequestBody BillDTO request) {
        return billService.createBill(request);
    }

    @PutMapping
    public BillDTO updateBill(@RequestBody BillDTO request) {
        return billService.updateBill(request);
    }

    @DeleteMapping("/{id}")
    public void deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
    }
}