package com.nhom04.ticketbooking.repository;

import com.nhom04.ticketbooking.model.base.BaseEntity;
import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Repository cơ bản để đọc/ghi CSV cho mọi entity.
 */
public class CsvRepository<T extends BaseEntity> {
    private final String filePath;
    private final Supplier<T> factory;

    public CsvRepository(String filePath, Supplier<T> factory) {
        this.filePath = filePath;
        this.factory = factory;
    }

    // Đọc toàn bộ dữ liệu từ CSV
    public List<T> findAll() {
        List<T> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                T entity = factory.get();
                entity.fromCsvLine(line);
                list.add(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Ghi toàn bộ dữ liệu ra CSV
    public void saveAll(List<T> entities) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (T e : entities) {
                pw.println(e.toCsvLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Tìm theo điều kiện
    public List<T> findByCondition(Predicate<T> condition) {
        List<T> all = findAll();
        List<T> result = new ArrayList<>();
        for (T e : all) {
            if (condition.test(e)) {
                result.add(e);
            }
        }
        return result;
    }
}
