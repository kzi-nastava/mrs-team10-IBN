package com.example.UberComp.dto.user;

import com.example.UberComp.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedUserDTO {
    private Long Id;
    @NotBlank(message = "Name is required")
    @Size(min = 2, message = "Name must be at least 2 characters long")
    private String name;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, message = "Last name must be at least 2 characters long")
    private String lastName;

    @NotBlank(message = "Home address is required")
    private String homeAddress;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\d{9,15}$", message = "Phone must be 9-15 digits")
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
