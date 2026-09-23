package com.example.farmer.canematrix.service.impl;
//
//import com.example.farmer.canematrix.service.SmsService;
//import com.twilio.Twilio;
//import com.twilio.rest.api.v3.account.Message;
//import com.twilio.type.PhoneNumber;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import jakarta.annotation.PostConstruct;
//
//@Service
//public class SmsServiceImpl implements SmsService {
//
//    @Value("${twilio.account.sid:dummy}")
//    private String accountSid;
//
//    @Value("${twilio.auth.token:dummy}")
//    private String authToken;
//
//    @Value("${twilio.phone.number:+10000000000}")
//    private String fromPhoneNumber;
//
//    @PostConstruct
//    public void initTwilio() {
//        try {
//            if (!"dummy".equals(accountSid)) {
//                Twilio.init(accountSid, authToken);
//                System.out.println("Twilio initialized successfully.");
//            }
//        } catch (Exception e) {
//            System.err.println("Twilio Init Error: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public void sendSms(String mobileNumber, String messageContent) {
//        try {
//            if (mobileNumber == null || mobileNumber.isBlank()) {
//                System.out.println("Mobile number is empty. Skipping SMS.");
//                return;
//            }
//
//            // भारतीय नंबरसाठी +91 जोडणे
//            String formattedNumber = mobileNumber.startsWith("+") ? mobileNumber : "+91" + mobileNumber;
//
//            // Twilio द्वारे SMS पाठवणे
//            Message message = Message.creator(
//                    new PhoneNumber(formattedNumber),
//                    new PhoneNumber(fromPhoneNumber),
//                    messageContent
//            ).create();
//
//            System.out.println("SMS sent successfully! SID: " + message.getSid());
//
//        } catch (Exception e) {
//            // अन-व्हेरिफाय नंबर किंवा API एरर आल्यास सिस्टीम क्रॅश होऊ नये म्हणून catch मधे हँडल केले आहे.
//            System.err.println("SMS sending failed for " + mobileNumber + ": " + e.getMessage());
//            System.out.println("[Fallback Log] Message was intended to be: \n" + messageContent);
//        }
//    }
//}

import com.example.farmer.canematrix.service.SmsService;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public void sendSms(String mobileNumber, String message) {
        // सध्या फक्त कन्सोलवर लॉगिंग ठेवू
        System.out.println("Notification to " + mobileNumber + ": " + message);
    }
}