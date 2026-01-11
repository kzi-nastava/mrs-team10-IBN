package com.example.UberComp.service;

import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.account.GetAccountDTO;
import com.example.UberComp.dto.account.LogAccountDTO;
import com.example.UberComp.dto.account.RegisterDTO;
import com.example.UberComp.dto.driver.*;
import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.dto.user.GetProfileDTO;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.AccountRepository;
import com.example.UberComp.repository.DriverChangeRequestRepository;
import com.example.UberComp.repository.DriverRepository;
import com.example.UberComp.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountService {
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

    public User register(RegisterDTO account) {
        if (accountRepository.findByEmail(account.getEmail()) == null) {
            Account newAccount = new Account(account.getEmail(), account.getPassword(), account.getType());
            User newUser = new User();
            newUser.setName(account.getName());
            newUser.setLastName(account.getLastName());
            newUser.setHomeAddress(account.getHomeAddress());
            newUser.setPhone(account.getPhone());
            newUser.setImage(account.getImage());
            newUser.setAccount(newAccount);
            newAccount.setUser(newUser);
            accountRepository.save(newAccount);
            return userRepository.save(newUser);
        }
        return null;
    }

    public Account login(LogAccountDTO accountDTO) {
        Account account = accountRepository.findByEmail(accountDTO.getEmail());
        return (account != null && account.getPassword().equals(accountDTO.getPassword())) ? account : null;
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

        System.out.println("=== CONVERTING REQUEST " + request.getId() + " ===");
        System.out.println("Raw JSON from DB: " + request.getRequestedChanges());

        UpdateDriverDTO newData;
        try {
            newData = objectMapper.readValue(request.getRequestedChanges(), UpdateDriverDTO.class);
            System.out.println("Parsed CreateUserDTO: " + newData.getCreateUserDTO());
            System.out.println("Parsed VehicleDTO: " + newData.getVehicleDTO());
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

        if (newData.getVehicleDTO() != null) {
            Vehicle v = driver.getVehicle();
            VehicleDTO vDTO = newData.getVehicleDTO();

            System.out.println("Processing vehicle changes...");
            System.out.println("Current vehicle: " + (v != null ? v.toString() : "null"));
            System.out.println("New vehicle DTO: model=" + vDTO.getModel() +
                    ", plate=" + vDTO.getPlate() +
                    ", seats=" + vDTO.getSeatNumber() +
                    ", babySeat=" + vDTO.getBabySeat() +
                    ", petFriendly=" + vDTO.getPetFriendly());

            if (addIfChanged(vOld, vNew, "Model",
                    (v != null ? v.getModel() : ""), vDTO.getModel())) {
                hasVehicleChanges = true;
                System.out.println("✓ Model changed");
            }

            String currentPlate = (v != null && v.getPlate() != null) ? v.getPlate() : "";
            String newPlate = vDTO.getPlate() != null ? vDTO.getPlate() : "";
            if (addIfChanged(vOld, vNew, "License Plate", currentPlate, newPlate)) {
                hasVehicleChanges = true;
                System.out.println("✓ License plate changed");
            }

            if (vDTO.getSeatNumber() != null) {
                int currentSeats = (v != null ? v.getSeatNumber() : 0);
                if (!vDTO.getSeatNumber().equals(currentSeats)) {
                    vOld.put("Seats", String.valueOf(currentSeats));
                    vNew.put("Seats", String.valueOf(vDTO.getSeatNumber()));
                    hasVehicleChanges = true;
                    System.out.println("✓ Seat number changed: " + currentSeats + " -> " + vDTO.getSeatNumber());
                }
            }

            if (vDTO.getBabySeat() != null) {
                boolean currentBS = (v != null && v.getBabySeat() != null) ? v.getBabySeat() : false;
                if (!vDTO.getBabySeat().equals(currentBS)) {
                    vOld.put("Baby Seat", currentBS ? "Yes" : "No");
                    vNew.put("Baby Seat", vDTO.getBabySeat() ? "Yes" : "No");
                    hasVehicleChanges = true;
                    System.out.println("✓ Baby seat changed: " + currentBS + " -> " + vDTO.getBabySeat());
                }
            }

            if (vDTO.getPetFriendly() != null) {
                boolean currentPF = (v != null && v.getPetFriendly() != null) ? v.getPetFriendly() : false;
                if (!vDTO.getPetFriendly().equals(currentPF)) {
                    vOld.put("Pet Friendly", currentPF ? "Yes" : "No");
                    vNew.put("Pet Friendly", vDTO.getPetFriendly() ? "Yes" : "No");
                    hasVehicleChanges = true;
                    System.out.println("✓ Pet friendly changed: " + currentPF + " -> " + vDTO.getPetFriendly());
                }
            }

            if (vDTO.getVehicleTypeDTO() != null) {
                String currentType = (v != null && v.getVehicleType() != null)
                        ? v.getVehicleType().getName() : "";
                String newType = vDTO.getVehicleTypeDTO().getName();
                if (addIfChanged(vOld, vNew, "Vehicle Type", currentType, newType)) {
                    hasVehicleChanges = true;
                    System.out.println("✓ Vehicle type changed: " + currentType + " -> " + newType);
                }
            }
        }

        if (newData.getCreateUserDTO() != null) {
            CreatedUserDTO uDTO = newData.getCreateUserDTO();

            System.out.println("Processing profile changes...");
            System.out.println("Current driver: name=" + driver.getName() +
                    ", lastName=" + driver.getLastName() +
                    ", phone=" + driver.getPhone() +
                    ", address=" + driver.getHomeAddress());
            System.out.println("New user DTO: name=" + uDTO.getName() +
                    ", lastName=" + uDTO.getLastName() +
                    ", phone=" + uDTO.getPhone() +
                    ", address=" + uDTO.getHomeAddress());

            if (addIfChanged(pOld, pNew, "Name", driver.getName(), uDTO.getName())) {
                hasProfileChanges = true;
                System.out.println("✓ Name changed");
            }

            if (addIfChanged(pOld, pNew, "Last Name", driver.getLastName(), uDTO.getLastName())) {
                hasProfileChanges = true;
                System.out.println("✓ Last name changed");
            }

            if (addIfChanged(pOld, pNew, "Phone", driver.getPhone(), uDTO.getPhone())) {
                hasProfileChanges = true;
                System.out.println("✓ Phone changed");
            }

            if (addIfChanged(pOld, pNew, "Address", driver.getHomeAddress(), uDTO.getHomeAddress())) {
                hasProfileChanges = true;
                System.out.println("✓ Address changed");
            }
        }

        String type;
        Map<String, String> finalOld = new HashMap<>();
        Map<String, String> finalNew = new HashMap<>();

        System.out.println("Has vehicle changes: " + hasVehicleChanges);
        System.out.println("Has profile changes: " + hasProfileChanges);

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
            System.out.println("⚠️ WARNING: No changes detected!");
        }

        System.out.println("Final type: " + type);
        System.out.println("Final old data: " + finalOld);
        System.out.println("Final new data: " + finalNew);
        System.out.println("=== END CONVERSION ===\n");

        return new DriverChangeRequestDTO(
                request.getId(),
                type,
                driver.getName() + " " + driver.getLastName(),
                request.getRequestDate().toString(),
                request.getStatus().toLowerCase(),
                new DriverChangeRequestDTO.ChangesDTO(finalOld, finalNew)
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
}