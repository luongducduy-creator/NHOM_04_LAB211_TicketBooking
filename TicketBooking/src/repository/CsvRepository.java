package repository;

import model.BaseEntity;
import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Repository generic cho CRUD với CSV file.
 * @param <T> entity kế thừa từ BaseEntity
 */
public class CsvRepository<T extends BaseEntity> {
    private String filePath;
    private Supplier<T> entitySupplier;

    public CsvRepository(String filePath, Supplier<T> entitySupplier) {
        this.filePath = filePath;
        this.entitySupplier = entitySupplier;
    }

    // Đọc tất cả entity từ file CSV
    public List<T> findAll() {
        List<T> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                T entity = entitySupplier.get();
                entity.fromCsvLine(line);
                result.add(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Ghi toàn bộ entity vào file CSV
    public void saveAll(List<T> entities) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (T entity : entities) {
                bw.write(entity.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Tìm entity theo điều kiện
    public List<T> findByCondition(Predicate<T> condition) {
        List<T> all = findAll();
        List<T> filtered = new ArrayList<>();
        for (T entity : all) {
            if (condition.test(entity)) {
                filtered.add(entity);
            }
        }
        return filtered;
    }
}
