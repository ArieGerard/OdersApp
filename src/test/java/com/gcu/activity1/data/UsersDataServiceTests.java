package com.gcu.activity1.data;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gcu.activity1.models.UserModel;

@ExtendWith(MockitoExtension.class)
class UsersDataServiceTests {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersDataService usersDataService;

    private UserEntity testUserEntity;
    private UserModel testUserModel;

    @BeforeEach
    void setUp() {
        testUserEntity = new UserEntity(1, "testuser", "$2a$12$hashedpassword", "ROLE_USER", true);
        testUserModel = new UserModel(1, "testuser", "plainpassword", "ROLE_USER", true);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ReturnsUserDetails() {
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUserEntity));

        UserDetails userDetails = usersDataService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_WhenUserNotFound_ThrowsException() {
        when(usersRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            usersDataService.loadUserByUsername("unknown");
        });
    }

    @Test
    void create_ShouldHashPasswordBeforeStoring() {
        String hashedPassword = "$2a$12$saltandhashvaluehere";
        when(passwordEncoder.encode("plainpassword")).thenReturn(hashedPassword);
        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            return new UserEntity(1, entity.getUsername(), entity.getPassword(), entity.getRole(), entity.isEnabled());
        });

        UserModel result = usersDataService.create(testUserModel);

        verify(passwordEncoder).encode("plainpassword");
        verify(usersRepository).save(argThat(entity -> entity.getPassword().equals(hashedPassword)));
    }

    @Test
    void usernameExists_WhenUserExists_ReturnsTrue() {
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUserEntity));

        assertTrue(usersDataService.usernameExists("testuser"));
    }

    @Test
    void usernameExists_WhenUserNotFound_ReturnsFalse() {
        when(usersRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertFalse(usersDataService.usernameExists("unknown"));
    }

    @Test
    void getById_WhenUserExists_ReturnsUserModel() {
        when(usersRepository.findById(1)).thenReturn(Optional.of(testUserEntity));

        UserModel result = usersDataService.getById(1);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getById_WhenUserNotFound_ReturnsNull() {
        when(usersRepository.findById(999)).thenReturn(Optional.empty());

        UserModel result = usersDataService.getById(999);

        assertNull(result);
    }

    @Test
    void bcryptPasswordFormat_ShouldProduceValidHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String rawPassword = "testPassword123";

        String hash = encoder.encode(rawPassword);

        assertTrue(hash.startsWith("$2a$12$"), "BCrypt hash should start with $2a$12$");
        assertEquals(60, hash.length(), "BCrypt hash should be exactly 60 characters");
        assertTrue(encoder.matches(rawPassword, hash), "Password should match the hash");
    }

    @Test
    void bcryptSalting_SamePasswordProducesDifferentHashes() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String rawPassword = "testPassword123";

        String hash1 = encoder.encode(rawPassword);
        String hash2 = encoder.encode(rawPassword);

        assertNotEquals(hash1, hash2, "Same password should produce different hashes due to salting");
        assertTrue(encoder.matches(rawPassword, hash1));
        assertTrue(encoder.matches(rawPassword, hash2));
    }

    @Test
    void getByUsername_WhenUserExists_ReturnsUserModel() {
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUserEntity));

        UserModel result = usersDataService.getByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(1, result.getId());
    }

    @Test
    void getByUsername_WhenUserNotFound_ReturnsNull() {
        when(usersRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        UserModel result = usersDataService.getByUsername("unknown");

        assertNull(result);
    }

    @Test
    void update_ShouldHashPasswordWhenNotAlreadyHashed() {
        String rawPassword = "newpassword";
        String hashedPassword = "$2a$12$newhashvaluehere";

        when(usersRepository.findById(1)).thenReturn(Optional.of(testUserEntity));
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserModel updateModel = new UserModel(1, "testuser", rawPassword, "ROLE_USER", true);
        usersDataService.update(updateModel);

        verify(passwordEncoder).encode(rawPassword);
        verify(usersRepository).save(argThat(entity -> entity.getPassword().equals(hashedPassword)));
    }

    @Test
    void update_ShouldNotRehashAlreadyHashedPassword() {
        String alreadyHashedPassword = "$2a$12$existinghashvalue";

        when(usersRepository.findById(1)).thenReturn(Optional.of(testUserEntity));
        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserModel updateModel = new UserModel(1, "testuser", alreadyHashedPassword, "ROLE_USER", true);
        usersDataService.update(updateModel);

        verify(passwordEncoder, never()).encode(anyString());
    }
}
