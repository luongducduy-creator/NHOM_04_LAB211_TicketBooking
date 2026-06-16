package test.fan;

import model.fan.Fan;
import repository.FanRepository;

public class FanRepositoryTest {
        public static void main(String[] args) {

                FanRepository repo = new FanRepository();

                // CREATE
                Fan fan = new Fan(
                                "F004",
                                "Duy",
                                "duy@gmail.com");

                repo.addFan(fan);

                System.out.println("Them fan thanh cong!");

                // READ ALL
                System.out.println("\nDanh sach fan:");

                for (Fan f : repo.getAllFans()) {

                        System.out.println(
                                        f.getId() + " - " +
                                                        f.getName() + " - " +
                                                        f.getEmail());
                }

                // READ BY ID
                Fan result = repo.findById("F001");

                if (result != null) {

                        System.out.println("\nTim thay:");

                        System.out.println(
                                        result.getId() + " - " +
                                                        result.getName() + " - " +
                                                        result.getEmail());

                } else {

                        System.out.println("\nKhong tim thay fan");
                }

                // UPDATE
                Fan updatedFan = new Fan(
                                "F001",
                                "Duy Updated",
                                "new@gmail.com");

                repo.updateFan(updatedFan);

                System.out.println("\nDa cap nhat F001");

                // DELETE
                repo.deleteFan("F004");

                System.out.println("Da xoa F004");

                // FIND BY CONDITION
                System.out.println("\nFan co gmail:");

                for (Fan f : repo.findByCondition(
                                fanItem -> fanItem.getEmail().contains("gmail"))) {

                        System.out.println(
                                        f.getId() + " - " +
                                                        f.getName() + " - " +
                                                        f.getEmail());
                }
        }
}
