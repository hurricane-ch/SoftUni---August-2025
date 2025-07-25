package org.atechtrade.rent.repository;

import org.atechtrade.rent.model.MessageResource;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Hidden
@Repository
public interface MessageResourceRepository extends JpaRepository<MessageResource, MessageResource.MessageResourceIdentity> {
    Page<MessageResource> findAllByMessageResourceIdentityLanguageIdOrderByMessageResourceIdentity_code(final String languageId, final Pageable pageable);

    List<MessageResource> findAllByMessageResourceIdentity_Code(final String code);
}