package com.paypal.paypal_user_service.service;

import com.paypal.paypal_user_service.client.WalletClient;
import com.paypal.paypal_user_service.dto.CreateWalletRequest;
import com.paypal.paypal_user_service.dto.WalletResponse;
import com.paypal.paypal_user_service.entity.User;
import com.paypal.paypal_user_service.repository.UserRepository;
import com.paypal.paypal_user_service.utils.ShortIdGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService{

    private UserRepository userRepository;


    private final WalletClient walletClient;
    @Override
    public User createUser(User user) {
        int attempt = 1;
        int maxAttempt = 5;
        String userId = ShortIdGenerator.generateId();
        User save = null;
        while(attempt <= maxAttempt){
            try{

                user.setUserId(userId);
                save  = userRepository.saveAndFlush(user);
                log.info("user is created");
            }catch (DataIntegrityViolationException ex){
                log.warn("duplicate userId : "+userId+", found while creating user, trying to create new userId : "+ex.getMessage());
                userId = ShortIdGenerator.generateId();
                attempt++;
            }
        }
        if(save == null)
            throw new IllegalStateException("Error occured while creating user, where duplicates userId found");

        try{
            CreateWalletRequest request = new CreateWalletRequest();
            request.setUserId(userId);
            request.setCurrency("Dol");
            WalletResponse wallet = walletClient.createWallet(request);
            log.info("wallet is created");
        }catch (Exception e){
            log.error("while creating wallet, error occured: "+e.getMessage());
        }
        return save;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
