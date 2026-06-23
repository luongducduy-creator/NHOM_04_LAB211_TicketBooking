package com.nhom04.ticketbooking.service;

import com.nhom04.ticketbooking.model.match.Match;
import com.nhom04.ticketbooking.model.match.MatchStatus;
import com.nhom04.ticketbooking.repository.MatchRepository;
import com.nhom04.ticketbooking.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchServiceTest {
    private MatchService matchService;
    private MatchRepository matchRepository;

    @BeforeEach
    void setUp() {
        matchRepository = new MatchRepository("matches_test.csv");
        matchService = new MatchService(matchRepository);
    }

    @Test
    void testAddMatch() {
        matchService.addMatch("M001", "Team A", "Team B", "2026-06-20", MatchStatus.SCHEDULED);
        Match match = matchService.findById("M001");
        assertNotNull(match);
        assertEquals("Team A", match.getHomeTeam());
        assertEquals("Team B", match.getAwayTeam());
        assertEquals("2026-06-20", match.getDate());
        assertEquals(MatchStatus.SCHEDULED, match.getStatus());
    }

    @Test
    void testFindById() {
        matchService.addMatch("M002", "Team C", "Team D", "2026-06-21", MatchStatus.SCHEDULED);
        Match match = matchService.findById("M002");
        assertNotNull(match);
        assertEquals("Team C", match.getHomeTeam());
    }

    @Test
    void testFindAll() {
        matchService.addMatch("M003", "Team E", "Team F", "2026-06-22", MatchStatus.SCHEDULED);
        matchService.addMatch("M004", "Team G", "Team H", "2026-06-23", MatchStatus.SCHEDULED);
        assertTrue(matchService.findAll().size() >= 2);
    }

    @Test
    void testUpdateStatus() {
        matchService.addMatch("M005", "Team I", "Team J", "2026-06-24", MatchStatus.SCHEDULED);
        matchService.updateStatus("M005", MatchStatus.COMPLETED);
        Match match = matchService.findById("M005");
        assertEquals(MatchStatus.COMPLETED, match.getStatus());
    }

    @Test
    void testSaveMatch() {
        Match match = new Match("M006", "Team K", "Team L", "2026-06-25", MatchStatus.SCHEDULED);
        matchService.saveMatch(match);
        Match found = matchService.findById("M006");
        assertEquals("Team K", found.getHomeTeam());
    }
}
