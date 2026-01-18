package com.example.UberComp.service;

import com.example.UberComp.dto.account.*;
import com.example.UberComp.dto.driver.*;
import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.dto.user.GetProfileDTO;
import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.*;
import com.example.UberComp.utils.EmailUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DriverChangeRequestRepository driverChangeRequestRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailUtils emailUtils;
    @Autowired
    private SetPasswordTokenRepository sptRepository;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email);
        if (account == null){
            throw new UsernameNotFoundException(String.format("No user found with email '%s'.", email));
        } else {
            return account;
        }
    }

    public User register(RegisterDTO account) {
        if (accountRepository.findByEmail(account.getEmail()) == null) {
            Account newAccount = new Account(account.getEmail(), passwordEncoder.encode(account.getPassword()), account.getType());
            User newUser;
            if (account.getType() == AccountType.DRIVER) {
                newUser = new Driver();
            } else {
                newUser = new User();
            }
            newUser.setName(account.getName());
            newUser.setLastName(account.getLastName());
            newUser.setHomeAddress(account.getHomeAddress());
            newUser.setPhone(account.getPhone());
            newUser.setImage(account.getImage());

            newUser.setAccount(newAccount);
            newAccount.setUser(newUser);

            emailUtils.sendVerificationEmail(newAccount);

            accountRepository.save(newAccount);

            return newUser;
        }
        return null;
    }


    public Account login(LogAccountDTO accountDTO) {
        Account account = accountRepository.findByEmail(accountDTO.getEmail());
        return (account != null && account.getAccountStatus() == AccountStatus.VERIFIED && account.getPassword().equals(accountDTO.getPassword())) ? account : null;
    }

    public Account verify(String verification){
        Account account = accountRepository.findByVerification(verification);
        if(account != null){
            long now = new Date().getTime();
            if(now - account.getLastPasswordResetDate().getTime() < 86400000) {
                account.setAccountStatus(AccountStatus.VERIFIED);
            } else {
                account.setAccountStatus(AccountStatus.BLOCKED);
                account.setBlockingReason("Expired Verification");
            }
            account.setVerification(null);
            accountRepository.save(account);
            return account;
        }
        return null;
    }

    public boolean generatePasswordResetToken(String email){
        Account account = accountRepository.findByEmail(email);
        if(account == null){
            return false;
        }
        SetPasswordToken token = new SetPasswordToken(account);
        sptRepository.save(token);
        emailUtils.sendSetPasswordEmail(token);
        return true;
    }

    public boolean checkSetPasswordToken(String tokenString){
        SetPasswordToken token = sptRepository.getByToken(tokenString);
        return token != null;
    }

    public boolean setPassword(String tokenString, SetPasswordDTO passwordDTO){
        SetPasswordToken token = sptRepository.getByToken(tokenString);
        if(token == null){
            return false;
        }
        Account account = token.getAccount();
        // for drivers who are setting their password for the first time
        if(account.getAccountStatus() == AccountStatus.UNVERIFIED){
            account.setAccountStatus(AccountStatus.VERIFIED);
        }
        account.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
        accountRepository.save(account);
        sptRepository.delete(token);
        return true;
    }

    public GetAccountDTO getById(Long id) {
        return accountRepository.findById(id)
                .map(acc -> new GetAccountDTO(acc.getId(), acc.getEmail(), acc.getAccountType().toString()))
                .orElse(null);
    }

    public GetProfileDTO getProfile(Long id) {
        return accountRepository.findById(id).map(account -> {
            AccountDTO getAccount = new AccountDTO(account.getEmail());
            CreatedUserDTO getUser = new CreatedUserDTO(account.getUser());
            return new GetProfileDTO(getUser, getAccount);
        }).orElse(null);
    }

    @Transactional
    public GetProfileDTO updateProfile(Long accountId, CreateUserDTO updatedUser) throws Exception {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new Exception("Account not found"));

        User user = account.getUser();
        user.setName(updatedUser.getName());
        user.setLastName(updatedUser.getLastName());
        user.setHomeAddress(updatedUser.getHomeAddress());
        user.setPhone(updatedUser.getPhone());
        user.setImage(updatedUser.getImage());

        userRepository.save(user);
        return new GetProfileDTO(new CreatedUserDTO(user), new AccountDTO(account.getEmail()));
    }

    @Transactional
    public void approveDriverChange(Long requestId) throws Exception {
        DriverChangeRequest request = driverChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Driver change request not found"));

        Driver driver = request.getDriver();
        UpdateDriverDTO changes = objectMapper.readValue(request.getRequestedChanges(), UpdateDriverDTO.class);

        CreatedUserDTO newInfo = changes.getCreateUserDTO();
        if (newInfo != null) {
            if (newInfo.getName() != null) driver.setName(newInfo.getName());
            if (newInfo.getLastName() != null) driver.setLastName(newInfo.getLastName());
            if (newInfo.getHomeAddress() != null) driver.setHomeAddress(newInfo.getHomeAddress());
            if (newInfo.getPhone() != null) driver.setPhone(newInfo.getPhone());
            if (newInfo.getImage() != null) driver.setImage(newInfo.getImage());
        }

        VehicleDTO newVehicle = changes.getVehicleDTO();
        if (newVehicle != null) {
            Vehicle vehicle = driver.getVehicle();
            if (newVehicle.getModel() != null) vehicle.setModel(newVehicle.getModel());
            if (newVehicle.getPlate() != null) vehicle.setPlate(newVehicle.getPlate());
            if (newVehicle.getSeatNumber() != null) vehicle.setSeatNumber(newVehicle.getSeatNumber());
            if (newVehicle.getBabySeat() != null) vehicle.setBabySeat(newVehicle.getBabySeat());
            if (newVehicle.getPetFriendly() != null) vehicle.setPetFriendly(newVehicle.getPetFriendly());
        }

        driverRepository.save(driver);
        driverChangeRequestRepository.delete(request);
    }

    @Transactional
    public void rejectDriverChange(Long requestId) throws Exception {
        DriverChangeRequest request = driverChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Driver change request not found"));
        driverChangeRequestRepository.delete(request);
    }

    public List<DriverChangeRequestDTO> getAllPendingRequests() {
        List<DriverChangeRequest> requests = driverChangeRequestRepository.findByStatus("PENDING");
        List<DriverChangeRequestDTO> result = new ArrayList<>();

        System.out.println("Found " + requests.size() + " pending requests");

        for (DriverChangeRequest request : requests) {
            try {
                DriverChangeRequestDTO dto = convertToDTO(request);
                result.add(dto);
            } catch (Exception e) {
                System.err.println("Error converting request ID " + request.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        return result;
    }

    private DriverChangeRequestDTO convertToDTO(DriverChangeRequest request) {
        Driver driver = request.getDriver();

        UpdateDriverDTO newData;
        try {
            newData = objectMapper.readValue(request.getRequestedChanges(), UpdateDriverDTO.class);
        } catch (Exception e) {
            System.err.println("JSON parsing error: " + e.getMessage());
            throw new RuntimeException("JSON error", e);
        }

        Map<String, String> vOld = new HashMap<>();
        Map<String, String> vNew = new HashMap<>();
        Map<String, String> pOld = new HashMap<>();
        Map<String, String> pNew = new HashMap<>();

        boolean hasVehicleChanges = false;
        boolean hasProfileChanges = false;

        String oldImage = null;
        String newImage = null;

        if (newData.getVehicleDTO() != null) {
            Vehicle v = driver.getVehicle();
            VehicleDTO vDTO = newData.getVehicleDTO();

            if (addIfChanged(vOld, vNew, "Model",
                    (v != null ? v.getModel() : ""), vDTO.getModel())) {
                hasVehicleChanges = true;
            }

            String currentPlate = (v != null && v.getPlate() != null) ? v.getPlate() : "";
            String newPlate = vDTO.getPlate() != null ? vDTO.getPlate() : "";
            if (addIfChanged(vOld, vNew, "License Plate", currentPlate, newPlate)) {
                hasVehicleChanges = true;
            }

            if (vDTO.getSeatNumber() != null) {
                int currentSeats = (v != null ? v.getSeatNumber() : 0);
                if (!vDTO.getSeatNumber().equals(currentSeats)) {
                    vOld.put("Seats", String.valueOf(currentSeats));
                    vNew.put("Seats", String.valueOf(vDTO.getSeatNumber()));
                    hasVehicleChanges = true;
                }
            }

            if (vDTO.getBabySeat() != null) {
                boolean currentBS = (v != null && v.getBabySeat() != null) ? v.getBabySeat() : false;
                if (!vDTO.getBabySeat().equals(currentBS)) {
                    vOld.put("Baby Seat", currentBS ? "Yes" : "No");
                    vNew.put("Baby Seat", vDTO.getBabySeat() ? "Yes" : "No");
                    hasVehicleChanges = true;
                }
            }

            if (vDTO.getPetFriendly() != null) {
                boolean currentPF = (v != null && v.getPetFriendly() != null) ? v.getPetFriendly() : false;
                if (!vDTO.getPetFriendly().equals(currentPF)) {
                    vOld.put("Pet Friendly", currentPF ? "Yes" : "No");
                    vNew.put("Pet Friendly", vDTO.getPetFriendly() ? "Yes" : "No");
                    hasVehicleChanges = true;
                    }
            }

            if (vDTO.getVehicleTypeDTO() != null) {
                String currentType = (v != null && v.getVehicleType() != null)
                        ? v.getVehicleType().getName() : "";
                String newType = vDTO.getVehicleTypeDTO().getName();
                if (addIfChanged(vOld, vNew, "Vehicle Type", currentType, newType)) {
                    hasVehicleChanges = true;
                }
            }
        }

        if (newData.getCreateUserDTO() != null) {
            CreatedUserDTO uDTO = newData.getCreateUserDTO();

            if (addIfChanged(pOld, pNew, "Name", driver.getName(), uDTO.getName())) {
                hasProfileChanges = true;
            }

            if (addIfChanged(pOld, pNew, "Last Name", driver.getLastName(), uDTO.getLastName())) {
                hasProfileChanges = true;
            }

            if (addIfChanged(pOld, pNew, "Phone", driver.getPhone(), uDTO.getPhone())) {
                hasProfileChanges = true;
            }

            if (addIfChanged(pOld, pNew, "Address", driver.getHomeAddress(), uDTO.getHomeAddress())) {
                hasProfileChanges = true;
            }

            if (uDTO.getImage() != null && !uDTO.getImage().trim().isEmpty()) {
                String currentImage = driver.getImage() != null ? driver.getImage() : "";
                if (!currentImage.equals(uDTO.getImage())) {
                    oldImage = currentImage;
                    newImage = uDTO.getImage();
                    hasProfileChanges = true;
                }
            }
        }

        String type;
        Map<String, String> finalOld = new HashMap<>();
        Map<String, String> finalNew = new HashMap<>();

        if (hasVehicleChanges && hasProfileChanges) {
            type = "both";
            finalOld.putAll(vOld);
            finalOld.putAll(pOld);
            finalNew.putAll(vNew);
            finalNew.putAll(pNew);
        } else if (hasVehicleChanges) {
            type = "vehicle";
            finalOld = vOld;
            finalNew = vNew;
        } else if (hasProfileChanges) {
            type = "profile";
            finalOld = pOld;
            finalNew = pNew;
        } else {
            type = "unknown";
        }

        return new DriverChangeRequestDTO(
                request.getId(),
                type,
                driver.getName() + " " + driver.getLastName(),
                request.getRequestDate().toString(),
                request.getStatus().toLowerCase(),
                new DriverChangeRequestDTO.ChangesDTO(finalOld, finalNew),
                oldImage,
                newImage
        );
    }

    private boolean addIfChanged(Map<String, String> oldMap, Map<String, String> newMap,
                                 String key, String oldVal, String newVal) {
        if (newVal == null || newVal.trim().isEmpty()) {
            return false;
        }

        String normalizedOld = (oldVal != null ? oldVal.trim() : "");
        String normalizedNew = newVal.trim();

        if (normalizedOld.equals(normalizedNew)) {
            return false;
        }

        oldMap.put(key, normalizedOld);
        newMap.put(key, normalizedNew);
        return true;
    }

    public GetProfileDTO getProfileByEmail(String email) throws Exception {
        Account account = accountRepository.findByEmail(email);
        if (account == null) return null;
        User user = account.getUser();
        return new GetProfileDTO(new CreatedUserDTO(user), new AccountDTO(account.getEmail()));
    }
}