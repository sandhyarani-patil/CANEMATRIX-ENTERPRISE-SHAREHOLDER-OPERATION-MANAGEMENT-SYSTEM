package com.example.farmer.canematrix.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "share_transfers")
@Data
public class ShareTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String farmerCode;     // मूळ शेतकरी कोड
    private String farmerName;     // मुख्य Farmers टेबलमधून ऑटोमॅटिक फेच होणार

    private String transfereeId;   // ज्याच्या नावावर शेअर्स ट्रान्सफर करायचे आहेत (नवीन सदस्याचा कोड)
    private String transfereeName; // नवीन सदस्याचे नाव (हवं तर फेच करू शकतो किंवा इनपुट घेऊ शकतो)

    private String nomineeName;    // नॉमिनीचे नाव

    private LocalDate transferDate;// ट्रान्सफर तारीख

    private String transferReason; // ट्रान्सफरचे कारण (उदा. Death, Voluntary Sale, etc.)

    private String documentPath;   // Death Certificate किंवा इतर डॉक्युमेंट सेव्ह केल्याचा पाथ

    @Enumerated(EnumType.STRING)
    private TransferStatus status; // PENDING, APPROVED, REJECTED

    public enum TransferStatus {
        PENDING, APPROVED, REJECTED
    }
}