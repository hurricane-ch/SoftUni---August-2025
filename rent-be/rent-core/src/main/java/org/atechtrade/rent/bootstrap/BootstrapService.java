package org.atechtrade.rent.bootstrap;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atechtrade.rent.enums.RentalItemType;
import org.atechtrade.rent.exception.ErrorObject;
import org.atechtrade.rent.exception.LibraryImportException;
import org.atechtrade.rent.model.Language;
import org.atechtrade.rent.model.MessageResource;
import org.atechtrade.rent.model.RentalHolder;
import org.atechtrade.rent.model.RentalItem;
import org.atechtrade.rent.model.Role;
import org.atechtrade.rent.model.Setting;
import org.atechtrade.rent.model.User;
import org.atechtrade.rent.repository.LanguageRepository;
import org.atechtrade.rent.repository.MessageResourceRepository;
import org.atechtrade.rent.repository.RentalHolderRepository;
import org.atechtrade.rent.repository.RentalItemRepository;
import org.atechtrade.rent.repository.RoleRepository;
import org.atechtrade.rent.repository.SettingRepository;
import org.atechtrade.rent.repository.UserRepository;
import org.atechtrade.rent.security.RolesConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class BootstrapService {

    private final Environment environment;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LanguageRepository languageRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageResourceRepository messageResourceRepository;
    private final SettingRepository settingRepository;
    private final RentalHolderRepository rentalHolderRepository;
    private final RentalItemRepository rentalItemRepository;

    @Value("${rent.bootstrap.message-resource-library:message-resource-library.tsv}")
    private String messageResourceLibrary;

    @PostConstruct
    public void postConstruct() {
        log.info("......... bootstrap start .......");
        final long startTime = System.currentTimeMillis();

        Role adminRole = createRoleIfNotFound(RolesConstants.ROLE_ADMIN);

        createLanguagesIfNotFound();

        loadMessageResourceLibrary();

        createUserIfNotFound("admin", "admin",
                "admin@domain.xyz", "Admin Admin", Set.of(adminRole));

        createRentalItemIfNotFount();

        createRentalHolderIfNotFount();

        initializeDefaultSettings();

        log.info("......... bootstrap FINISHED for {}ms .......", System.currentTimeMillis() - startTime);
    }

    private void createUserIfNotFound(final String username, final String password, final String email,
                                      final String fullName, final Set<Role> roles) {
        User user = userRepository.findByUsernameIgnoreCase(username.toLowerCase()).orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(roles);
            user.setEnabled(true);
            user.setCreatedBy("anonymousUser");
            user.setLastModifiedBy("anonymousUser");
            userRepository.save(user);
        }

        log.info("createUserIfNotFound: [{}]", username);
    }

    private void createRentalItemIfNotFount() {
        if (rentalItemRepository.count() == 0) {
            RentalItem item = new RentalItem();
            item.setRentalItemType(RentalItemType.BUNGALOW);
            item.setEnabled(true);
            item.setName("Бунгало 1");
            item.setPrice(200.00);
            item.setRecommendedVisitors(4);
            item.setRoom(2);

            rentalItemRepository.save(item);
        }
    }

    private void createRentalHolderIfNotFount() {
        if (rentalHolderRepository.count() == 0) {
            RentalHolder rentalHolder = new RentalHolder();
            rentalHolder.setName("Бунгала Раят - Ахтопол");
            rentalHolder.setOpenDate(LocalDate.of(2025, 6, 1));
            rentalHolder.setCloseDate(LocalDate.of(2025, 9, 15));
            rentalHolder.setDescription(null);
            rentalHolder.setAddress("ул.„Витошка №1, 8280 Ахтопол, България");
            rentalHolder.setLongitude(null);
            rentalHolder.setLatitude(null);
            rentalHolder.setPhone("+359 884 12 33 32");
            rentalHolder.setEmail("rayat_ahtopol@abv.bg");
            rentalHolder.setFacebookUrl(null);
            rentalHolder.setInstagramUrl(null);
            rentalHolder.setTiktokUrl(null);
            rentalHolder.setFoodPlacesDescription(null);
            rentalHolder.setEntertainmentPlacesDescription(null);

            rentalHolderRepository.save(rentalHolder);
        }
    }

    private Role createRoleIfNotFound(final String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = roleRepository.save(Role.builder().name(name).build());
        }
        log.info("createRoleIfNotFound: [{}]", name);
        return role;
    }

    private void createLanguagesIfNotFound() {
        if (languageRepository.count() <= 0) {
            languageRepository.save(
                    Language.builder()
                            .languageId("bg")
                            .name("Български")
                            .description("Български")
                            .locale("bg_BG.UTF-8,BG,bulgarian")
                            .createdBy("anonymousUser")
                            .lastModifiedBy("anonymousUser")
                            .main(true).enabled(true).build()
            );
            languageRepository.save(
                    Language.builder()
                            .languageId("en")
                            .name("English")
                            .description("English")
                            .locale("en_US.UTF-8,en_US,en-gb,english")
                            .createdBy("anonymousUser")
                            .lastModifiedBy("anonymousUser")
                            .main(false).enabled(true).build()
            );
        }
    }

    private void loadMessageResourceLibrary() {
        if (messageResourceRepository.count() <= 0) {
            try (InputStream ignored = this.getClass().getResourceAsStream(messageResourceLibrary)) {
                final List<ErrorObject> objectErrors = new ArrayList<>();
                final List<TsvReader.TsvMessageResource> tsvMessageResources =
                        TsvReader.tsvMessageResourceList(BootstrapService.class, messageResourceLibrary, objectErrors);
                log.info("Message Resource Library uploadFile using messageResourceLibrary={}", messageResourceLibrary);

                if (!CollectionUtils.isEmpty(tsvMessageResources)) {
                    List<MessageResource> records = messageResourceRepository.saveAll(TsvReader.TsvMessageResource.to(tsvMessageResources));
                    log.info("Message Resource Library uploadFile finished ({} message resource(s) processed).", records.size());

                    if (!objectErrors.isEmpty()) {
                        log.warn("Some Message Resource(s) were not saved because of the following errors: ");
                        objectErrors.forEach(e -> log.warn(e.toString()));
                    }
                }
            } catch (Exception e) {
                throw new LibraryImportException(e);
            }
        }
    }

    // Initialize default settings
    private void initializeDefaultSettings() {
        if (settingRepository.count() <= 0) {
            List<String> activeProfiles = List.of(environment.getActiveProfiles());

            Setting defaultSetting = new Setting();
            defaultSetting.setKey("global");
            defaultSetting.setValue("true");

            Setting globalAppSync = new Setting();
            globalAppSync.setKey("global.app-sync");
            globalAppSync.setValue("true");
            globalAppSync.setParent(defaultSetting);

            defaultSetting.getSubSettings().add(globalAppSync);

            Setting globalMarkAsFinished = new Setting();
            globalMarkAsFinished.setKey("global.mark-as-finished");
            globalMarkAsFinished.setValue(activeProfiles.contains("postgres-prod") ? "true" : "false");
            globalMarkAsFinished.setParent(defaultSetting);

            defaultSetting.getSubSettings().add(globalMarkAsFinished);

            settingRepository.save(defaultSetting);
        }
    }
}