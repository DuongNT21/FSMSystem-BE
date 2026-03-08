package com.swp391_be.SWP391_be.dto.request.order;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GetOrderCriteriaRequest {

    private LocalDate fromDate;

    private LocalDate toDate;

}