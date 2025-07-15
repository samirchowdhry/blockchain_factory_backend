package com.example.blockchainfactory.Repository;

import com.example.blockchainfactory.Model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentRepo extends JpaRepository<Document,Integer> {

    @Query(value = "select * from document d where file_name = ?",nativeQuery = true)
    Document getDocsByFileName(String fileName);

    @Query(value = "select * from document d order by track_number desc, id asc",nativeQuery = true)
    List<Document> getDocsList();

    @Query(value = "select * from document d where order_status in (?1) and uploaded_by = ?2 or verify_by = ?2 or verify_by is null order by track_number desc, id asc",nativeQuery = true)
    List<Document> getDocsByStatus(List<String> status, Integer userId);

}
