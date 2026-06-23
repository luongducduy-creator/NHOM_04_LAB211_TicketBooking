package ticket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TicketCsvTest {

    @Test
    public void testCsvColumnAndEmptyData() throws IOException {
        String path = "data/tickets.csv"; // chỉnh lại nếu file nằm chỗ khác
        BufferedReader reader = new BufferedReader(new FileReader(path));

        String line;
        int lineNumber = 0;
        int invalidCount = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            // Bỏ qua dòng tiêu đề
            if (lineNumber == 1 && line.startsWith("ticketId"))
                continue;

            // 1️⃣ Kiểm tra số lượng cột
            String[] parts = line.split(",");
            if (parts.length != 7) {
                System.out.println("❌ Sai số cột ở dòng " + lineNumber + ": " + line);
                invalidCount++;
                continue; // bỏ qua kiểm tra tiếp theo nếu sai cột
            }

            // 2️⃣ Kiểm tra có ô nào bị bỏ trống
            boolean hasEmpty = false;
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].trim().isEmpty()) {
                    System.out.println("⚠️ Ô trống ở dòng " + lineNumber + ", cột " + (i + 1));
                    hasEmpty = true;
                }
            }
            if (hasEmpty)
                invalidCount++;
        }

        reader.close();

        System.out.println("✅ Kiểm tra hoàn tất. Số dòng lỗi: " + invalidCount);
        assertEquals(0, invalidCount, "File CSV có dòng thiếu cột hoặc ô trống!");
    }
}
