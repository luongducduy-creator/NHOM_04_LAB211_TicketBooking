package repository;

import model.notification.Notification;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {
    private final String filePath;

    public NotificationRepository() {
        this.filePath = System.getProperty("user.dir") + "/data/notifications.csv";
        initFile();
    }

    private void initFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write("notificationId,message,date");
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("[ERROR] Cannot initialize notifications file: " + e.getMessage());
            }
        }
    }

    public List<Notification> findAll() {
        List<Notification> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists())
            return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Notification nt = Notification.fromCsvLine(line);
                if (nt != null)
                    list.add(nt);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot read notifications: " + e.getMessage());
        }
        return list;
    }

    public void saveAll(List<Notification> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("notificationId,message,date");
            bw.newLine();
            for (Notification nt : list) {
                bw.write(nt.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot save notifications: " + e.getMessage());
        }
    }

    public void addNotification(Notification notification) {
        try (FileWriter fw = new FileWriter(filePath, true)) {
            fw.write(notification.toCsvLine());
            fw.write(System.lineSeparator());
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot add notification: " + e.getMessage());
        }
    }

    public String generateNextNotificationId() {
        List<Notification> list = findAll();
        int max = 1000;
        for (Notification nt : list) {
            String idStr = nt.getNotificationId().replaceAll("[^0-9]", "");
            if (!idStr.isEmpty()) {
                try {
                    int val = Integer.parseInt(idStr);
                    if (val > max)
                        max = val;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return "NT" + (max + 1);
    }
}
