package com.example.farmer.canematrix.service;

import com.example.farmer.canematrix.entity.ShareTransfer;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ShareTransferService {
    // 3 पॅरामीटर्स असलेली मेथड इथे असायला हवी
    ShareTransfer createTransferRequest(String farmerCode, String transferReason, MultipartFile documentFile);

    ShareTransfer updateTransferStatus(Long id, ShareTransfer.TransferStatus status);
    List<ShareTransfer> getAllTransfers();
    List<ShareTransfer> getTransfersByFarmer(String farmerCode);
    byte[] generateTransferReceiptPdf(Long transferId);
}