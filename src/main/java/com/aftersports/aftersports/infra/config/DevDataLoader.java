package com.aftersports.aftersports.infra.config;

import com.aftersports.aftersports.domain.model.Instructor;
import com.aftersports.aftersports.domain.model.Lesson;
import com.aftersports.aftersports.domain.model.User;
import com.aftersports.aftersports.domain.model.UserRole;
import com.aftersports.aftersports.domain.repo.InstructorRepository;
import com.aftersports.aftersports.domain.repo.LessonRepository;
import com.aftersports.aftersports.domain.repo.UserRepository;
import com.aftersports.aftersports.domain.service.PasswordService;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class DevDataLoader {

    @Bean
    CommandLineRunner loadData(InstructorRepository instructorRepository,
                              LessonRepository lessonRepository,
                              UserRepository userRepository,
                              PasswordService passwordService,
                              AdminProperties adminProperties) {
        return args -> {
            if (adminProperties.getEmail() != null && adminProperties.getPassword() != null) {
                boolean exists = userRepository.existsByEmailIgnoreCase(adminProperties.getEmail());
                if (!exists) {
                    User admin = new User();
                    admin.setName(adminProperties.getName());
                    admin.setEmail(adminProperties.getEmail().toLowerCase());
                    admin.setPasswordHash(passwordService.hash(adminProperties.getPassword()));
                    admin.setRole(UserRole.ADMIN);
                    userRepository.save(admin);
                }
            }

            if (instructorRepository.count() > 0) {
                return;
            }

            Instructor ana = new Instructor();
            ana.setName("Ana Souza");
            ana.setSport("SURF");
            ana.setBio("Instrutora experiente em surf");

            Instructor bruno = new Instructor();
            bruno.setName("Bruno Lima");
            bruno.setSport("TENIS");
            bruno.setBio("Treinador certificado");

            instructorRepository.save(ana);
            instructorRepository.save(bruno);

            Lesson surf = new Lesson();
            surf.setInstructor(ana);
            surf.setTitle("Aula de Surf - Iniciantes");
            surf.setDescription("Primeiro contato com o mar");
            surf.setDateTime(LocalDateTime.now().plusDays(3).withHour(9).withMinute(0));
            surf.setDurationMinutes(90);
            surf.setCapacity(6);
            surf.setPriceCents(12000L);
            surf.setLat(-23.993);
            surf.setLon(-46.307);

            Lesson tenis = new Lesson();
            tenis.setInstructor(bruno);
            tenis.setTitle("Treino de Tênis - Fundamentos");
            tenis.setDescription("Movimentação e golpes básicos");
            tenis.setDateTime(LocalDateTime.now().plusDays(5).withHour(8).withMinute(30));
            tenis.setDurationMinutes(75);
            tenis.setCapacity(4);
            tenis.setPriceCents(15000L);
            tenis.setLat(-23.564);
            tenis.setLon(-46.653);

            Lesson funcional = new Lesson();
            funcional.setInstructor(bruno);
            funcional.setTitle("Funcional na Praia");
            funcional.setDescription("Treino dinâmico ao ar livre");
            funcional.setDateTime(LocalDateTime.now().plusDays(7).withHour(7).withMinute(0));
            funcional.setDurationMinutes(60);
            funcional.setCapacity(10);
            funcional.setPriceCents(9000L);
            funcional.setLat(-23.991);
            funcional.setLon(-46.302);

            lessonRepository.save(surf);
            lessonRepository.save(tenis);
            lessonRepository.save(funcional);
        };
    }
}
