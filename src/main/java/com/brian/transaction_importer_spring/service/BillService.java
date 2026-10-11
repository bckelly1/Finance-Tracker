package com.brian.transaction_importer_spring.service;

import com.brian.transaction_importer_spring.dto.BillDTO;
import com.brian.transaction_importer_spring.entity.Bill;
import com.brian.transaction_importer_spring.entity.Transaction;
import com.brian.transaction_importer_spring.enums.ScheduleType;
import com.brian.transaction_importer_spring.repository.BillRepository;
import com.brian.transaction_importer_spring.repository.TransactionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillService {

    private final ObjectMapper objectMapper;

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

    private boolean hasBillArrived(final Bill bill) {
        List<Transaction> transactionsByDescriptionAfter = transactionRepository.findTransactionsByDescriptionIsAfter(bill.getTransactionDescription(), calculateStart(bill.getScheduleType(), bill.getScheduleValue()));
        return !transactionsByDescriptionAfter.isEmpty();
    }

    private LocalDateTime calculateStart(final ScheduleType scheduleType, final String scheduleValue) {
        return LocalDateTime.now();
    }

    private Bill getBillEntity(BillDTO billDTO) {
        Bill bill = new Bill();
        bill.setName(billDTO.getName());
        bill.setAmount(billDTO.getAmount());
        bill.setDate(billDTO.getDate());
        bill.setScheduleType(billDTO.getScheduleType());
        bill.setScheduleValue(billDTO.getScheduleValue());
        bill.setTransactionDescription(billDTO.getTransactionDescription());
        return bill;
    }

    private BillDTO getBillDTO(Bill save) {
        BillDTO saveDTO = new BillDTO();
        saveDTO.setId(save.getId());
        saveDTO.setName(save.getName());
        saveDTO.setAmount(save.getAmount());
        saveDTO.setDate(save.getDate());
        saveDTO.setScheduleType(save.getScheduleType());
        saveDTO.setScheduleValue(save.getScheduleValue());
        saveDTO.setTransactionDescription(save.getTransactionDescription());

        saveDTO.setBillArrived(hasBillArrived(save));
        return saveDTO;
    }

    /**
     * Returns all due dates for a bill within the given month.
     * DOM bills return one date; NTH_WEEKDAY bills may return multiple.
     *
     * @param bill The bill for the month
     * @param month The month of the year for the bill
     *
     * @return All due dates for the bill for this month
     */
    public List<LocalDate> getDueDatesForMonth(Bill bill, YearMonth month) {
        return switch (bill.getScheduleType()) {
            case DAY_OF_MONTH         -> resolveDom(bill.getScheduleValue(), month);
            case NTH_WEEKDAY -> resolveNthWeekday(bill.getScheduleValue(), month);
        };
    }

    // --- DOM -----------------------------------------------------------------

    private List<LocalDate> resolveDom(String scheduleValue, YearMonth month) {
        int day = Integer.parseInt(scheduleValue);

        // Clamp to the last valid day of the month (e.g. "31" in February → 28/29)
        int actualDay = Math.min(day, month.lengthOfMonth());
        return List.of(LocalDate.of(month.getYear(), month.getMonth(), actualDay));
    }

    // --- NTH_WEEKDAY ---------------------------------------------------------

    private List<LocalDate> resolveNthWeekday(String scheduleValue, YearMonth month) {
        List<NthWeekdayRule> rules = parseRules(scheduleValue);
        List<LocalDate> dates = new ArrayList<>();

        for (NthWeekdayRule rule : rules) {
            LocalDate date = nthWeekdayOfMonth(month, rule.n(), rule.wd());
            if (date != null) {
                dates.add(date);
            }
        }

        dates.sort(null); // ascending order
        return dates;
    }

    /**
     * Returns the nth occurrence of a given ISO weekday in the month,
     * or null if that occurrence doesn't exist (e.g. 5th Friday in a short month).
     *
     * @param month  the target month
     * @param n      occurrence index (1=first, 2=second, …)
     * @param isoWd  ISO weekday (1=Monday, 7=Sunday)
     *
     * @return LocalDate which day the bill is due
     */
    private LocalDate nthWeekdayOfMonth(YearMonth month, int n, int isoWd) {
        DayOfWeek dow = DayOfWeek.of(isoWd);

        // Find the FIRST occurrence of this weekday in the month
        LocalDate first = LocalDate.of(month.getYear(), month.getMonth(), 1)
                .with(TemporalAdjusters.nextOrSame(dow));

        // Step forward (n-1) weeks
        LocalDate result = first.plusWeeks(n - 1);

        // Guard: make sure we haven't spilled into the next month
        return result.getMonth() == month.getMonth() ? result : null;
    }

    // --- JSON helpers --------------------------------------------------------

    private List<NthWeekdayRule> parseRules(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<NthWeekdayRule>>() { });
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid NTH_WEEKDAY schedule_value: " + json, e);
        }
    }

    // A single "nth weekday" rule, e.g. {n=1, wd=5} = "1st Friday"
    public record NthWeekdayRule(int n, int wd) {
        // n  : 1–4 (1st, 2nd, 3rd, 4th)
        // wd : 1–7 (ISO: 1=Monday, 7=Sunday)
    }
}
