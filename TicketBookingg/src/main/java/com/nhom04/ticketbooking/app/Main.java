package com.nhom04.ticketbooking.app;

import com.nhom04.ticketbooking.model.match.MatchStatus;
import com.nhom04.ticketbooking.repository.MatchRepository;
import com.nhom04.ticketbooking.repository.SeatRepository;
import com.nhom04.ticketbooking.service.MatchService;
import com.nhom04.ticketbooking.service.SeatService;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo repository
        MatchRepository matchRepository = new MatchRepository("matches.csv");
        SeatRepository seatRepository = new SeatRepository("seats.csv");

        // Khởi tạo service
        MatchService matchService = new MatchService(matchRepository);
        SeatService seatService = new SeatService(seatRepository);

        // Thêm trận đấu (dùng String date)
        matchService.addMatch("M001", "Team A", "Team B", "2026-06-20", MatchStatus.SCHEDULED);

        // Thêm ghế (truyền đủ 5 tham số)
        seatService.addSeat("S001", "A1", "VIP", 1, true);
        seatService.addSeat("S002", "A2", "VIP", 1, true);
        seatService.addSeat("S003", "B1", "Standard", 2, false);

        // In ra kết quả kiểm tra
        System.out.println("Match count: " + matchService.findAll().size());
        System.out.println("Seat count: " + seatService.findAll().size());
    }
}
