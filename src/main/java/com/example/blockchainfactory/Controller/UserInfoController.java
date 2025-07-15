package com.example.blockchainfactory.Controller;

import com.example.blockchainfactory.Model.AuthRequest;
import com.example.blockchainfactory.Model.UserInfo;
import com.example.blockchainfactory.RequestDTO.UserRequestDTO;
import com.example.blockchainfactory.ResponseDTO.UserResponseDTO;
import com.example.blockchainfactory.Service.JwtService;
import com.example.blockchainfactory.Service.UserInfoService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/admin/addNewUser")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public ResponseEntity<UserResponseDTO> UserResponseDTO(@RequestBody UserRequestDTO userRequestDTO) {

        UserResponseDTO userResponseDTO = userInfoService.addUser(userRequestDTO);

         if(userResponseDTO != null){
             return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
         }
         else{
             return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
         }
    }

    @GetMapping("/admin/getAllUsers")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> userResponseDTOList = userInfoService.getAllUsers();
        if(userResponseDTOList != null){
            return new ResponseEntity<>(userResponseDTOList, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/admin/deleteUser")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public ResponseEntity<UserResponseDTO> deleteUser(@RequestParam Integer userId) {
        UserResponseDTO userResponseDTO = userInfoService.deleteUser(userId);
        if(userResponseDTO != null){
            return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/generateToken")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return  new ResponseEntity<>(new JSONObject().put("token",  "Bearer " +jwtService.generateToken(userDetails)).toString(), HttpStatus.OK);

        } else {
            return  new ResponseEntity<>(new JSONObject().put("message",  "inavalid username/password").toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/getUserByEmailUser")
    @PreAuthorize("hasAuthority('ROLE_Admin','ROLE_Warehouse','ROLE_Packing','ROLE_Transport','ROLE_Delivery','ROLE_Verifier')")
    public ResponseEntity<UserResponseDTO> getUserByEmailUser(@RequestParam String username) {
        UserResponseDTO userResponseDTOList = userInfoService.getUserByEmail(username);
        if(userResponseDTOList != null){
            return new ResponseEntity<>(userResponseDTOList, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
