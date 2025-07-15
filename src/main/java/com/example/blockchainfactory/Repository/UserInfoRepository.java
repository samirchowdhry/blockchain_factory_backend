package com.example.blockchainfactory.Repository;

import com.example.blockchainfactory.Model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    @Query(value = "select * from user_info ui where username = ? and IFNULL(delete_soft,0)=0 ",nativeQuery = true)
    Optional<UserInfo> findByUsername(String username);

    @Query(value = "select * from user_info ui where username = ? and IFNULL(delete_soft,0)=0 ",nativeQuery = true)
    UserInfo findByEmail(String username);

    @Query(value = "select id,created_at,display_name,password,updated_at,username,delete_soft,\n" +
            "REPLACE(roles , 'ROLE_', '') AS roles \n" +
            "from user_info ui where IFNULL(delete_soft,0)=0",nativeQuery = true)
    List<UserInfo> getAllActiveUsers();

}
