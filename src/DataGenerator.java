package com.ticketbooking.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {

    static Random random = new Random();

    static final int STADIUM_COUNT = 20;
    static final int SECTION_PER_STADIUM = 5;
    static final int SEAT_PER_SECTION = 120;

    public static void main(String[] args) {

        generateStadiums();
        generateSections();
        generateSeats();

        System.out.println("Generate completed!");
    }

    // =========================
    // STADIUMS
    // =========================
    public static void generateStadiums() {

        try (FileWriter fw = new FileWriter("data/stadiums.csv")) {

            fw.write("stadiumId,name,capacity,location\n");

            for (int i = 1; i <= STADIUM_COUNT; i++) {

                String id = String.format("STD%03d", i);
                String name = "Stadium " + i;
                int capacity = 20000 + random.nextInt(30000);
                String location = "City " + i;

                fw.write(id + "," + name + "," + capacity + "," + location + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // SECTIONS
    // =========================
    public static void generateSections() {

        try (FileWriter fw = new FileWriter("data/sections.csv")) {

            fw.write("sectionId,stadiumId,name,capacity\n");

            int sectionCounter = 1;

            for (int s = 1; s <= STADIUM_COUNT; s++) {

                String stadiumId = String.format("STD%03d", s);

                for (int i = 1; i <= SECTION_PER_STADIUM; i++) {

                    String sectionId = String.format("SEC%03d", sectionCounter++);

                    String name = "Section " + i;

                    int capacity = 1000 + random.nextInt(500);

                    fw.write(sectionId + "," +
                            stadiumId + "," +
                            name + "," +
                            capacity + "\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // SEATS
    // =========================
    public static void generateSeats() {

        try (FileWriter fw = new FileWriter("data/seats.csv")) {

            fw.write("seatId,sectionId,rowNumber,seatNumber,type,status\n");

            int seatCounter = 1;

            int totalSections = STADIUM_COUNT * SECTION_PER_STADIUM;

            for (int sec = 1; sec <= totalSections; sec++) {

                String sectionId = String.format("SEC%03d", sec);

                for (int i = 1; i <= SEAT_PER_SECTION; i++) {

                    String seatId = String.format("SEAT%05d", seatCounter++);

                    char row = (char) ('A' + (i / 20));

                    int seatNumber = i;

                    String type;

                    if (i <= 20) {
                        type = "VIP";
                    } else if (i <= 80) {
                        type = "Normal";
                    } else {
                        type = "Economy";
                    }

                    String status = "Available";

                    fw.write(seatId + "," +
                            sectionId + "," +
                            row + "," +
                            seatNumber + "," +
                            type + "," +
                            status + "\n");
                }
            }

            System.out.println("Generated more than 10,000 seats!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
