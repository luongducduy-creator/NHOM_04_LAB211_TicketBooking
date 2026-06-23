package com.nhom04.ticketbooking.model.transaction;

public enum TransactionStatus {
    PENDING,     // giao dịch đang chờ xử lý
    COMPLETED,   // giao dịch thành công
    FAILED,      // giao dịch thất bại
    CANCELLED    // giao dịch bị hủy
}
