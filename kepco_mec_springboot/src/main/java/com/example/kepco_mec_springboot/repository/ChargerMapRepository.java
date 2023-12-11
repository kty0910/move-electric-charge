package com.example.kepco_mec_springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kepco_mec_springboot.model.ChargerMap;


@Repository
public interface ChargerMapRepository extends JpaRepository<ChargerMap,String> {
    List<ChargerMap> findAllByLatBetweenAndLngBetween(float lat_start,float lat_end,float lng_start,float lng_end);
    ChargerMap findByStchId(String stchId);
}
