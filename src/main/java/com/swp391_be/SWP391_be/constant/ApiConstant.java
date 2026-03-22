package com.swp391_be.SWP391_be.constant;


public class ApiConstant {
  public static final Integer BOUQUET_STATUS_INACTIVE = 0;
  public static final Integer BOUQUET_STATUS_ACTIVE = 1;
    public static final String API = "/api";

    public static class AUTH {
        public static final String AUTH = "/auth";
    }

    public static class USER {
        public static final String USER = "/register";
    }

    public static class EMPLOYEE {
        public static final String EMPLOYEE = "/employee";
    }

    public static class ORDER {
        public static final String ORDER = "/order";
        public static final String ID = ORDER + "/{id}";
        public static final String PAY = ID + "/payment";
        public static final String STATUS = ID + "/status";
    }

    public static class PAYMENT {
        public static final String PAYMENT = "/payment";
        public static final String CALLBACK = PAYMENT + "/payment-callback";
    }
    public static class REVIEW {
        public static final String REVIEW = "/review";
    }
}
