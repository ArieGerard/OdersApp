package com.gcu.activity1.data;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gcu.activity1.models.Mapper;
import com.gcu.activity1.models.UserModel;

@Service
public class UsersDataService implements DataAccessInterface<UserModel>, UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = usersRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        UserEntity userEntity = optionalUser.get();
        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                true,
                true,
                true,
                java.util.Collections.singletonList(new SimpleGrantedAuthority(userEntity.getRole()))
        );
    }

    @Override
    public UserModel getById(int id) {
        UserEntity userEntity = usersRepository.findById(id).orElse(null);
        if (userEntity == null) {
            return null;
        }
        return Mapper.toModel(userEntity);
    }

    public UserModel getByUsername(String username) {
        Optional<UserEntity> optionalUser = usersRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return null;
        }
        return Mapper.toModel(optionalUser.get());
    }

    @Override
    public Iterable<UserModel> getAll() {
        ArrayList<UserModel> userModels = new ArrayList<>();
        Iterable<UserEntity> userEntities = usersRepository.findAll();
        for (UserEntity userEntity : userEntities) {
            userModels.add(Mapper.toModel(userEntity));
        }
        return userModels;
    }

    @Override
    public UserModel create(UserModel user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());

        UserEntity userEntity = new UserEntity(
                0,
                user.getUsername(),
                hashedPassword,
                user.getRole(),
                user.isEnabled()
        );

        UserEntity savedEntity = usersRepository.save(userEntity);
        return Mapper.toModel(savedEntity);
    }

    @Override
    public UserModel update(UserModel user) {
        UserEntity existingEntity = usersRepository.findById(user.getId()).orElse(null);
        if (existingEntity == null) {
            return null;
        }

        String password = user.getPassword();
        if (!password.startsWith("$2a$")) {
            password = passwordEncoder.encode(password);
        }

        UserEntity userEntity = new UserEntity(
                user.getId(),
                user.getUsername(),
                password,
                user.getRole(),
                user.isEnabled()
        );

        UserEntity savedEntity = usersRepository.save(userEntity);
        return Mapper.toModel(savedEntity);
    }

    @Override
    public boolean deleteById(int id) {
        usersRepository.deleteById(id);
        return true;
    }

    public boolean usernameExists(String username) {
        return usersRepository.findByUsername(username).isPresent();
    }
}
