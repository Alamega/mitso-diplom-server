package com.alamega.alamegaspringapp.record;

import com.alamega.alamegaspringapp.info.Info;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, String> {
    List<Record> findAllByInfo(Info info);
    void deleteAllByDate(Date date);
}
