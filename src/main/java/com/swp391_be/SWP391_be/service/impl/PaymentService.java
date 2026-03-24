package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.orderItems.CreateOrderItemsRequest;
import com.swp391_be.SWP391_be.entity.*;
import com.swp391_be.SWP391_be.enums.EActionType;
import com.swp391_be.SWP391_be.enums.EOrderStatus;
import com.swp391_be.SWP391_be.exception.BadHttpRequestException;
import com.swp391_be.SWP391_be.exception.NotFoundException;
import com.swp391_be.SWP391_be.repository.*;
import com.swp391_be.SWP391_be.service.IPaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RawMaterialBatchRepository rawMaterialBatchRepository;
    private final BouquetRepository bouquetRepository;
    private final InventoryLogRepository inventoryLogs;


//    @Override
//    public void paymentSuccess(int id) {
//        Order order = orderRepository.findById(id).orElseThrow(()-> new RuntimeException("Order not found"));
//        order.setOrderStatus(EOrderStatus.Accepted);
//
//        orderRepository.save(order);
//    }

    @Override
    @Transactional
    public void paymentSuccess(int id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔥 Check + trừ kho
        for (OrderItem item : order.getOrderItems()) {

            Bouquet bouquet = item.getBouquet();

            for (BouquetsMaterial bm : bouquet.getBouquetsMaterials()) {

                RawMaterial rawMaterial = bm.getRawMaterial();

                int requiredQuantity = bm.getQuantity() * item.getQuantity();

                //lấy batch theo FIFO
                List<RawMaterialBatches> batches =
                        rawMaterialBatchRepository
                                .findByRawMaterialIdOrderByImportDateAsc(rawMaterial.getId());

                int totalRemain = batches.stream()
                        .mapToInt(RawMaterialBatches::getRemainQuantity)
                        .sum();

                // Không đủ hàng
                if (totalRemain < requiredQuantity) {
                    throw new RuntimeException("Not enough material: " + rawMaterial.getName());
                }

                // Trừ kho
                int remainToDeduct = requiredQuantity;

                for (RawMaterialBatches batch : batches) {

                    if (batch.getRemainQuantity() <= 0) continue;

                    int deduct = Math.min(batch.getRemainQuantity(), remainToDeduct);

                    batch.setRemainQuantity(batch.getRemainQuantity() - deduct);
                    rawMaterialBatchRepository.save(batch);

                    // log
                    InventoryLogs log = new InventoryLogs();
                    log.setRawMaterialBatches(batch);
                    log.setActionType(EActionType.Export);
                    log.setQuantity(deduct);
                    log.setCreatedAt(LocalDateTime.now());

                    inventoryLogs.save(log);

                    remainToDeduct -= deduct;

                    if (remainToDeduct == 0) break;
                }
            }
        }

        // cập nhật trạng thái order
        order.setOrderStatus(EOrderStatus.Accepted);
        orderRepository.save(order);
    }
}
