package TicketBooking.test.fan;

import TicketBooking.model.fan.Fan;
import TicketBooking.repository.FanRepository;

public class FanRepositoryTest {

    public static void main(String[] args) {

        FanRepository repo = new FanRepository();

        Fan fan = new Fan(
                "F004",
                "Duy",
                "duy@gmail.com",
                "0912345678",
                2004);

        repo.addFan(fan);

        System.out.println("Them fan thanh cong!");

        System.out.println("\nDanh sach fan:");

        for (Fan f : repo.getAllFans()) {

            System.out.println(
                    f.getId() + " - " +
                    f.getName() + " - " +
                    f.getEmail());
        }

        Fan result = repo.findById("F001");

        if (result != null) {

            System.out.println(
                    "\nTim thay: " +
                    result.getName());
        }

        Fan updatedFan = new Fan(
                "F001",
                "Duy Updated",
                "new@gmail.com",
                "0999999999",
                2000);

        repo.updateFan(updatedFan);

        System.out.println("\nDa cap nhat F001");

        repo.deleteFan("F004");

        System.out.println("Da xoa F004");
    }
}