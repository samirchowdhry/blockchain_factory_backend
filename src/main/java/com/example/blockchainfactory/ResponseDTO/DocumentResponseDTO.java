package com.example.blockchainfactory.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class DocumentResponseDTO {

    private String fileName;
    private String orderStatus;
    private Integer trackNumber;
    private String uploadedBy;
    private String verifyBy;
    private Instant createdAt;
    private Instant updatedAt;

}
