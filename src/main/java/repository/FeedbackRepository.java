package repository;

import model.feedback.Feedback;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackRepository {
    private final String filePath;

    public FeedbackRepository() {
        this.filePath = System.getProperty("user.dir") + "/data/feedback.csv";
        initFile();
    }

    private void initFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write("feedbackId,fanId,content,response");
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("[ERROR] Cannot initialize feedback file: " + e.getMessage());
            }
        }
    }

    public List<Feedback> findAll() {
        List<Feedback> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists())
            return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Feedback fb = Feedback.fromCsvLine(line);
                if (fb != null)
                    list.add(fb);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot read feedback: " + e.getMessage());
        }
        return list;
    }

    public void saveAll(List<Feedback> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("feedbackId,fanId,content,response");
            bw.newLine();
            for (Feedback fb : list) {
                bw.write(fb.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot save feedback: " + e.getMessage());
        }
    }

    public void addFeedback(Feedback feedback) {
        try (FileWriter fw = new FileWriter(filePath, true)) {
            fw.write(feedback.toCsvLine());
            fw.write(System.lineSeparator());
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot add feedback: " + e.getMessage());
        }
    }

    public String generateNextFeedbackId() {
        List<Feedback> list = findAll();
        int max = 1000;
        for (Feedback fb : list) {
            String idStr = fb.getFeedbackId().replaceAll("[^0-9]", "");
            if (!idStr.isEmpty()) {
                try {
                    int val = Integer.parseInt(idStr);
                    if (val > max)
                        max = val;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return "FB" + (max + 1);
    }
}
