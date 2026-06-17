package app;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int choice;

        do {

            System.out.println(
                    "\n===== FOOTBALL TICKET MANAGEMENT SYSTEM =====");

            System.out.println(
                    "1. Fan Management");
            System.out.println(
                    "2. Seat Management");
            System.out.println(
                    "3. Ticket Management");
            System.out.println(
                    "4. Match Management");
            System.out.println(
                    "5. Transaction Management");
            System.out.println(
                    "0. Exit");

            System.out.print("Choose: ");

            choice =
                    Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1:
                    System.out.println(
                            "Fan Management");
                    break;

                case 2:
                    System.out.println(
                            "Seat Management");
                    break;

                case 3:
                    System.out.println(
                            "Ticket Management");
                    break;

                case 4:
                    System.out.println(
                            "Match Management");
                    break;

                case 5:
                    System.out.println(
                            "Transaction Management");
                    break;

                case 0:
                    System.out.println(
                            "Goodbye!");
                    break;

                default:
                    System.out.println(
                            "Invalid choice!");
            }

        } while (choice != 0);

        sc.close();
    }
}