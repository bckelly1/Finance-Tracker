package com.brian.transaction_importer_spring.service;

import com.brian.transaction_importer_spring.dto.BillDTO;
import com.brian.transaction_importer_spring.entity.Bill;
import com.brian.transaction_importer_spring.entity.Transaction;
import com.brian.transaction_importer_spring.enums.CalendarPeriod;
import com.brian.transaction_importer_spring.repository.BillRepository;
import com.brian.transaction_importer_spring.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillService {
    private final TransactionRepository transactionRepository;

    private final BillRepository billRepository;

    public List<BillDTO> getAllBills() {
        List<Bill> all = billRepository.findAll();
        List<BillDTO> billDTOS = new ArrayList<>();
        for (Bill bill : all) {
            BillDTO billDTO = getBillDTO(bill);
            billDTOS.add(billDTO);
        }
        return billDTOS;
    }

    public BillDTO getBill(final long id) {
        Bill bill = billRepository.findById(id);
        return getBillDTO(bill);
    }

    public BillDTO createBill(final BillDTO billDTO) {
        Bill bill = getBillEntity(billDTO);
        Bill save = billRepository.save(bill);

        return getBillDTO(save);
    }

    public BillDTO updateBill(final BillDTO billDTO) {
        Bill bill = billRepository.findById(billDTO.getId()).orElse(null);

        if (bill == null) {
            log.error("Bill id {} not found", billDTO.getId());
            return null;
        }
        Bill save = billRepository.save(bill);

        return getBillDTO(save);
    }

    public void deleteBill(final long id) {
        billRepository.deleteById(id);
    }

    private boolean hasBillArrived(final long id) {
        BillDTO bill = getBill(id);
        if (bill == null) {
            log.error("Bill id {} not found", id);
            return false;
        }

        List<Transaction> transactionsByDescriptionAfter = transactionRepository.findTransactionsByDescriptionAfter(bill.getTransactionDescription(), calculateStart(bill.getPeriod()));
        return !transactionsByDescriptionAfter.isEmpty();
    }

    private LocalDateTime calculateStart(final CalendarPeriod calendarPeriod) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        return switch (calendarPeriod) {
            case MONTHLY -> {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                yield LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault());
            }
            case WEEKLY -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                yield LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault());
            }
            case YEARLY -> {
                cal.set(Calendar.DAY_OF_YEAR, 1);
                yield LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault());
            }
        };
    }

    private Bill getBillEntity(BillDTO billDTO) {
        Bill bill = new Bill();
        bill.setName(billDTO.getName());
        bill.setAmount(billDTO.getAmount());
        bill.setDate(billDTO.getDate());
        bill.setPeriod(billDTO.getPeriod());
        bill.setTransactionDescription(billDTO.getTransactionDescription());
        return bill;
    }

    private BillDTO getBillDTO(Bill save) {
        BillDTO saveDTO = new BillDTO();
        saveDTO.setId(save.getId());
        saveDTO.setName(save.getName());
        saveDTO.setAmount(save.getAmount());
        saveDTO.setDate(save.getDate());
        saveDTO.setPeriod(save.getPeriod());
        saveDTO.setTransactionDescription(save.getTransactionDescription());

        saveDTO.setBillArrived(hasBillArrived(save.getId()));
        return saveDTO;
    }
}
