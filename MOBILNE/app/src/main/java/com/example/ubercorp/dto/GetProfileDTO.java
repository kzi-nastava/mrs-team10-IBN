package com.example.ubercorp.dto;
public class GetProfileDTO {
    private CreatedUserDTO createdUserDTO;
    private AccountDTO accountDTO;

    public CreateUserDTO getCreatedUserDTO() { return new CreateUserDTO(createdUserDTO.getName(), createdUserDTO.getLastName(), createdUserDTO.getHomeAddress(), createdUserDTO.getPhone(), createdUserDTO.getImage()); }
    public void setCreatedUserDTO(CreatedUserDTO createdUserDTO) { this.createdUserDTO = createdUserDTO; }
    public AccountDTO getAccountDTO() { return accountDTO; }
    public void setAccountDTO(AccountDTO accountDTO) { this.accountDTO = accountDTO; }
}