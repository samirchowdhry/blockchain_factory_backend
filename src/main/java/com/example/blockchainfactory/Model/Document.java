package com.example.blockchainfactory.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fileName;
    private String hash;
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", referencedColumnName = "id")
    private UserInfo userInfoUploaded;

    @ManyToOne
    @JoinColumn(name = "verify_by", referencedColumnName = "id")
    private UserInfo userInfoVerify;

    private Integer trackNumber;
    private String orderStatus;


}
