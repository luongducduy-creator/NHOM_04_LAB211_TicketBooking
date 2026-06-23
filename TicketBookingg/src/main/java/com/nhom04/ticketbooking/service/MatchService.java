package com.nhom04.ticketbooking.service;

import com.nhom04.ticketbooking.model.match.Match;
import com.nhom04.ticketbooking.model.match.MatchStatus;
import com.nhom04.ticketbooking.repository.MatchRepository;

import java.util.List;

public class MatchService {
    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    // Thêm trận đấu mới
    public void addMatch(String id, String homeTeam, String awayTeam, String date, MatchStatus status) {
        Match match = new Match(id, homeTeam, awayTeam, date, status);
        matchRepository.save(match);
    }

    // Tìm trận đấu theo ID
    public Match findById(String id) {
        return matchRepository.getById(id);
    }

    // Lấy tất cả trận đấu
    public List<Match> findAll() {
        return matchRepository.getAll();
    }

    // Cập nhật trạng thái trận đấu
    public void updateStatus(String id, MatchStatus status) {
        Match match = matchRepository.getById(id);
        if (match != null) {
            match.setStatus(status);
            matchRepository.save(match);
        }
    }

    // Lưu trận đấu
    public void saveMatch(Match match) {
        matchRepository.save(match);
    }
}
