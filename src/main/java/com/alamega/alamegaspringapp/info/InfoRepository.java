package com.alamega.alamegaspringapp.info;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoRepository extends JpaRepository<Info, String> {
    Info findByMac(String mac);
}
