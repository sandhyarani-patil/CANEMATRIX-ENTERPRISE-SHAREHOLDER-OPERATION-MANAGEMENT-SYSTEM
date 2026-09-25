package com.example.farmer.canematrix.service;

import com.example.farmer.canematrix.dto.TonnesSugarReceiptDto;
import com.example.farmer.canematrix.entity.TonnesSugarAllocation;
import com.example.farmer.canematrix.entity.TonnesSugarHistory;
import java.math.BigDecimal;
import java.util.List;

public interface TonnesSugarAllocationService {
    TonnesSugarAllocation createAllocation(String farmerCode, Double totalTonnes);
    TonnesSugarReceiptDto liftTonnesSugar(String farmerCode, double quantityToLift);
    byte[] generateTonnesReceiptPdf(TonnesSugarReceiptDto receipt);
    byte[] generateTonnesReceiptPdfByHistoryId(Long historyId);

    // या दोन मेथड्स ॲड करा (ज्यामुळे इम्प्लिमेंटेशन क्लासमधील एरर निघून जाईल)
    List<TonnesSugarAllocation> getAllAllocations();
    List<TonnesSugarHistory> getFarmerTonnesHistory(String farmerCode);
}