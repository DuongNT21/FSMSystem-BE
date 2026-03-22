package com.swp391_be.SWP391_be.controller;

import com.swp391_be.SWP391_be.constant.ApiConstant;
import com.swp391_be.SWP391_be.service.IPaymentService;
import com.swp391_be.SWP391_be.service.impl.PaymentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(ApiConstant.API)
@RequiredArgsConstructor
public class PaymentController {
    private final IPaymentService paymentService;

    @GetMapping(ApiConstant.PAYMENT.CALLBACK)
    public ResponseEntity<Boolean> paymentCallback(@RequestParam Map<String, String> queryParams, HttpServletResponse response) throws IOException, IOException {
        String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
        int orderId = Integer.parseInt(queryParams.get("vnp_OrderInfo"));

        if ("00".equals(vnp_ResponseCode)) {
            paymentService.paymentSuccess(orderId);
            response.sendRedirect("http://localhost:5173/payment/success/"+orderId);
        } else {
            response.sendRedirect("http://localhost:5173/payment/failed/" + orderId);
        }
        return ResponseEntity.ok(false);
    }

}
