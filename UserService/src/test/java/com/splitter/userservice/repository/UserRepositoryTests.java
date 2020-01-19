package com.splitter.userservice.repository;

import com.splitter.userservice.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void save_userWithSameEmailExists_shouldThrowException() throws Exception {
        testEntityManager.persist(User.builder()
                .firstName("Artem")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(1994, 8, 13))
                .username("b.artem")
                .email("artem2549@gmail.com")
                .build());

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(User.builder()
                    .firstName("Vasa")
                    .lastName("Baranovskyi")
                    .dateOfBirth(LocalDate.of(1994, 8, 13))
                    .username("b.artem")
                    .email("artem2549@gmail.com")
                    .build());
        });
    }

    @Test
    public void save_userWithSameUsernameExists_shouldThrowException() throws Exception {
        testEntityManager.persist(User.builder()
                .firstName("Artem")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(1994, 8, 13))
                .username("b.artem")
                .email("artem2549@gmail.com")
                .build());

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(User.builder()
                    .firstName("Vasa")
                    .lastName("Baranovskyi")
                    .dateOfBirth(LocalDate.of(1994, 8, 13))
                    .username("b.artem")
                    .email("artem@gmail.com")
                    .build());
        });
    }
}
