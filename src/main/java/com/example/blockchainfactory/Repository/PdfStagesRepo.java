package com.example.blockchainfactory.Repository;

import com.example.blockchainfactory.Model.PdfStages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PdfStagesRepo extends JpaRepository<PdfStages,Integer> {

    @Query(value = "select id from pdf_stages where stage = ?",nativeQuery = true)
    Integer getPdfId(String stage);

}
