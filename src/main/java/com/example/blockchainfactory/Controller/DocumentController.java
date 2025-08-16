package com.example.blockchainfactory.Controller;


import com.example.blockchainfactory.Repository.DocumentRepo;
import com.example.blockchainfactory.Repository.PdfStagesRepo;
import com.example.blockchainfactory.Repository.UserInfoRepository;
import com.example.blockchainfactory.ResponseDTO.DocumentResponseDTO;
import com.example.blockchainfactory.Service.DocumentService;
import com.example.blockchainfactory.Service.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/document")
@Service
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class DocumentController {

    @Autowired
    DocumentRepo documentRepo;

    @Autowired
    BlockchainService fabricService;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    PdfStagesRepo pdfStagesRepo;

    @Autowired
    DocumentService documentService;

    @PostMapping("/uploadPdf")
    @PreAuthorize("hasAuthority('ROLE_Admin','ROLE_Warehouse','ROLE_Packing','ROLE_Transport','ROLE_Delivery')")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,@RequestParam Integer trackNumber,@RequestParam String fileName,@RequestParam Integer userId) throws Exception {

        String documentResponseDTOList = documentService.uploadPdf(file,trackNumber,fileName,userId);
        if(documentResponseDTOList != null){
            return ResponseEntity.ok(documentResponseDTOList);
        }
        else{
            return ResponseEntity.status(500).body("Something went wrong, Please try again");
        }

    }

    @PostMapping("/verifyPdf")
    @PreAuthorize("hasAuthority('ROLE_Admin','ROLE_Warehouse','ROLE_Packing','ROLE_Transport','ROLE_Delivery','ROLE_Verifier')")
    public ResponseEntity<String> verifyDocument(@RequestParam("file") MultipartFile file, @RequestParam String trackNumber,@RequestParam String fileName,@RequestParam Integer userId) throws Exception {

        String documentResponseDTOList = documentService.verifyPdf(file,trackNumber,fileName,userId);
        if(documentResponseDTOList != null){
            return ResponseEntity.ok(documentResponseDTOList);
        }
        else{
            return ResponseEntity.status(500).body("Something went wrong, Please try again");
        }

    }

    @PutMapping(value = "/updatePdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public ResponseEntity<String> updatePdf(@RequestParam("file") MultipartFile file,@RequestParam String trackNumber,@RequestParam String fileName,@RequestParam Integer userId) {

        String documentResponseDTOList = documentService.updatePdf(file,trackNumber,fileName,userId);
        if(documentResponseDTOList != null){
            return ResponseEntity.ok(documentResponseDTOList);
        }
        else{
            return ResponseEntity.status(500).body("Something went wrong, Please try again");
        }


    }

    @GetMapping("/getAllPdf")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public ResponseEntity<String> getAllPdf() {
        String pdfData = documentService.getAllPdf();
        if(pdfData != null){
            return ResponseEntity.ok(pdfData);
        }
        else{
            return ResponseEntity.status(500).body("Something went wrong, Please try again");
        }
    }

    @DeleteMapping("/deletePdf")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public ResponseEntity<String> deletePdf(@RequestParam String pdfData) {
            String deletePdf = documentService.deletePdf(pdfData);
            if(deletePdf != null){
                return ResponseEntity.ok("Pdf deleted successfully: " + pdfData);
            }
            else{
                return ResponseEntity.status(500).body("Something went wrong, Please try again");

            }
    }

    @GetMapping("/getDocument")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public ResponseEntity<List<DocumentResponseDTO>> getDocument() {
        List<DocumentResponseDTO> documentResponseDTOList = documentService.getDocumentList();
        if(documentResponseDTOList.size() > 0 && documentResponseDTOList != null){
            return new ResponseEntity<>(documentResponseDTOList, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/getDocumentByStatus")
    @PreAuthorize("hasAuthority('ROLE_Warehouse','ROLE_Packing','ROLE_Transport','ROLE_Delivery','ROLE_Verifier')")
    public ResponseEntity<List<DocumentResponseDTO>> getDocumentByStatus(@RequestParam String status, @RequestParam Integer userId) {
        List<DocumentResponseDTO> documentResponseDTOList = documentService.getDocumentByStatus(status,userId);
        if(documentResponseDTOList.size() > 0){
            return new ResponseEntity<>(documentResponseDTOList, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
