package com.example.UberComp.dto.user;

import com.example.UberComp.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedUserDTO {
    private Long Id;
    private String name;
    private String lastName;
    private String homeAddress;
    private String phone;
    private String image;

    public CreatedUserDTO(User user){
        this.Id = user.getId();
        this.name = user.getName();
        this.lastName = user.getLastName();
        this.homeAddress = user.getHomeAddress();
        this.phone = user.getPhone();
        this.image = user.getImage();
    }
}
