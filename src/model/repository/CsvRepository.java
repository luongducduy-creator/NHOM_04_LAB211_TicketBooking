package repository;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public abstract class CsvRepository<T> {
    private final String filePath;
    private final Function<String, T> fromCsv;
    private final Function<T, String> toCsv;

    protected CsvRepository(String filePath, Function<String, T> fromCsv, Function<T, String> toCsv) {
        this.filePath = filePath;
        this.fromCsv = fromCsv;
        this.toCsv = toCsv;
    }

    public List<T> findAll() {
        try {
            return new BufferedReader(new FileReader(filePath))
                    .lines()
                    .map(fromCsv)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void save(T item) {
        try (FileWriter fw = new FileWriter(filePath, true)) {
            fw.write(toCsv.apply(item) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(T item) {
        List<T> all = findAll();
        List<String> lines = all.stream().map(toCsv).collect(Collectors.toList());
        try (FileWriter fw = new FileWriter(filePath)) {
            for (String line : lines)
                fw.write(line + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected List<T> findByCondition(Predicate<T> condition) {
        return findAll().stream().filter(condition).collect(Collectors.toList());
    }
}
