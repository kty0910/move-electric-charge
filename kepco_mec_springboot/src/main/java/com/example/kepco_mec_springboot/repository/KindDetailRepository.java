package com.example.kepco_mec_springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kepco_mec_springboot.model.KindDetail;


@Repository
public interface KindDetailRepository extends JpaRepository<KindDetail,String> {
    
}
