package com.example.blockchainfactory.Service;

import com.example.blockchainfactory.Model.Document;
import com.example.blockchainfactory.Model.PdfStages;
import com.example.blockchainfactory.Model.UserInfo;
import com.example.blockchainfactory.Repository.DocumentRepo;
import com.example.blockchainfactory.Repository.PdfStagesRepo;
import com.example.blockchainfactory.Repository.UserInfoRepository;
import com.example.blockchainfactory.ResponseDTO.DocumentResponseDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    DocumentRepo documentRepo;

    @Autowired
    PdfStagesRepo pdfStagesRepo;

    @Autowired
    BlockchainService blockchainService;

    @Autowired
    UserInfoRepository userInfoRepository;

    public String getSha256Hash(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
    }

    public List<DocumentResponseDTO> getDocumentList() {

        List<DocumentResponseDTO> documentResponseDTOList = new ArrayList<>();
        List<Document> documentList = documentRepo.getDocsList();

        for (Document document:documentList) {

            DocumentResponseDTO documentResponseDTO = new DocumentResponseDTO();

            documentResponseDTO.setCreatedAt(document.getCreatedAt());
            documentResponseDTO.setFileName(document.getFileName());
            documentResponseDTO.setOrderStatus(document.getOrderStatus());
            documentResponseDTO.setTrackNumber(document.getTrackNumber());
            documentResponseDTO.setUpdatedAt(document.getUpdatedAt());
            documentResponseDTO.setUploadedBy(document.getUserInfoUploaded() != null ? document.getUserInfoUploaded().getDisplayName() : "");
            documentResponseDTO.setVerifyBy(document.getUserInfoVerify() != null ? document.getUserInfoVerify().getDisplayName() : "");
            documentResponseDTOList.add(documentResponseDTO);

        }

        return documentResponseDTOList;

    }

    public List<DocumentResponseDTO> getDocumentByStatus(String status, Integer userId) {

        List<DocumentResponseDTO> documentResponseDTOList = new ArrayList<>();
        List<String> statusList = new ArrayList<>();
        if(!status.equals("")) {
            Integer pdfId = pdfStagesRepo.getPdfId(status);
            PdfStages pdfStages = pdfStagesRepo.findById(pdfId-1).get();
            statusList.add(status);
            statusList.add(pdfStages.getStage());
        }
        else{
            statusList.add("Delivery Note");
        }

        List<Document> documentList = documentRepo.getDocsByStatus(statusList,userId);

        for (Document document:documentList) {

            DocumentResponseDTO documentResponseDTO = new DocumentResponseDTO();

            documentResponseDTO.setCreatedAt(document.getCreatedAt());
            documentResponseDTO.setFileName(document.getFileName());
            documentResponseDTO.setOrderStatus(document.getOrderStatus());
            documentResponseDTO.setTrackNumber(document.getTrackNumber());
            documentResponseDTO.setUpdatedAt(document.getUpdatedAt());
            documentResponseDTO.setUploadedBy(document.getUserInfoUploaded() != null ? document.getUserInfoUploaded().getDisplayName() : "");
            documentResponseDTO.setVerifyBy(document.getUserInfoVerify() != null ? document.getUserInfoVerify().getDisplayName() : "");
            documentResponseDTOList.add(documentResponseDTO);

        }

        return documentResponseDTOList;

    }

    public String uploadPdf(MultipartFile file,Integer trackNumber,String fileName,Integer userId){

        try {

            String hash = getSha256Hash(file.getBytes());
            Document documentExists = documentRepo.getDocsByFileName(trackNumber+"-"+fileName+".pdf");
            String blockHash = blockchainService.readPdf(hash);
            if(documentExists != null && blockHash.contains("assetID")){
                return "PDF Already Uploaded";
            }

            if(!fileName.equals("Order")){
                Integer pdfId = pdfStagesRepo.getPdfId(fileName);
                PdfStages pdfStages = pdfStagesRepo.findById(pdfId-1).get();
                Document documentPre = documentRepo.getDocsByFileName(trackNumber+"-"+pdfStages.getStage()+".pdf");
                if(documentPre == null){
                    return "Please Upload "+pdfStages.getStage()+" first";
                }
                else{
                    if(documentPre.getUserInfoVerify() == null){
                        return "Please Verify "+pdfStages.getStage()+" first";
                    }
                }
            }

            UserInfo userInfo = userInfoRepository.findById(userId).get();

            byte[] bytes  = file.getBytes();
            OutputStream out = new FileOutputStream("C:\\Blockchain\\"+trackNumber+"-"+fileName+".pdf");
            out.write(bytes);
            out.close();
            Document document = new Document();
            document.setHash(hash);
            document.setFileName(trackNumber+"-"+fileName+".pdf");
            document.setUserInfoUploaded(userInfo);
            document.setTrackNumber(trackNumber);
            document.setOrderStatus(fileName);
            documentRepo.save(document);

            Date currentDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateCurrent = format.format(currentDate);

            blockchainService.createPdf(hash, trackNumber+"-"+fileName+".pdf", bytes.length,dateCurrent,userId);

            return "PDF uploaded and stored on blockchain";
        } catch (Exception e) {
            return "Something Went Wrong, Please try again";
        }

    }

    public String verifyPdf(MultipartFile file,String trackNumber,String fileName,Integer userId){

        try {
            String currentHash = getSha256Hash(file.getBytes());

            Document document = documentRepo.getDocsByFileName(trackNumber+"-"+fileName+".pdf");
            if(document == null){
                return fileName +" PDF is not Uploaded";
            }

            UserInfo userInfo = userInfoRepository.findById(userId).get();

            document.setUserInfoVerify(userInfo);
            documentRepo.save(document);

            String blockHash = blockchainService.readPdf(currentHash);

            if(currentHash.equals(document.getHash()) && blockHash.contains("assetID")){
                return fileName +" PDF Verified";
            }

            return fileName +" PDF is Not Verified";
        }
        catch (Exception e){
            return "Something Went Wrong, Please try again";
        }


    }

    public String updatePdf(MultipartFile file,String trackNumber,String fileName,Integer userId){

        try {

            Document documentExists = documentRepo.getDocsByFileName(trackNumber+"-"+fileName+".pdf");
            if(documentExists == null) {
                return "PDF is not Uploaded";
            }

            Path path = Paths.get("C:\\Blockchain\\" + trackNumber + "-" + fileName + ".pdf");
            byte[] prevBytes = Files.readAllBytes(path);
            String pdfHash = getSha256Hash(prevBytes);
            blockchainService.deletePdf(pdfHash);

            byte[] bytes  = file.getBytes();
            OutputStream out = new FileOutputStream("C:\\Blockchain\\"+trackNumber+"-"+fileName+".pdf");
            out.write(bytes);
            out.close();

            documentExists.setHash(getSha256Hash(bytes));
            documentExists.setUserInfoVerify(null);

            UserInfo userInfo = userInfoRepository.findById(userId).get();
            documentExists.setUserInfoUploaded(userInfo);
            documentRepo.save(documentExists);

            String dateCurrent = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            blockchainService.createPdf(getSha256Hash(bytes), trackNumber+"-"+fileName+".pdf", bytes.length,dateCurrent,userId);

            return "PDF updated successfully and stored on blockchain.";

        } catch (Exception e) {
            return "Something Went Wrong, Please try again";
        }


    }

    public String getAllPdf(){

        try {
            JSONArray pdfList = blockchainService.getAllPdf();
            JSONArray pdfArray = new JSONArray();

            for (int i=0;i<pdfList.length();i++) {
                JSONObject jsonObject1 = pdfList.getJSONObject(i);
                System.out.println(jsonObject1);
                JSONObject pdfObject = new JSONObject();
                pdfObject.put("Hash",jsonObject1.get("assetID"));
                pdfObject.put("Size",jsonObject1.get("size"));
                pdfObject.put("Upload Date",jsonObject1.get("owner"));
                pdfObject.put("File Name",jsonObject1.get("color"));
                pdfObject.put("Uploaded By",jsonObject1.get("appraisedValue"));
                pdfArray.put(pdfObject);
            }
            return pdfArray.toString();
        } catch (Exception e) {
            return "Something Went Wrong, Please try again";
        }


    }

    public String deletePdf(String pdfData){

        try {
            String deletePdf = blockchainService.deletePdf(pdfData);
            return "Pdf deleted successfully: " + pdfData;

        } catch (Exception e) {
            return "Something Went Wrong, Please try again";
        }

    }


}
