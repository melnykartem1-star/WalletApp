package org.my.walletapp.repository;

import org.my.walletapp.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    List<Merchant> findAllByUserIdAndIsActiveTrue(Long userId);
    Optional<Merchant> findByIdAndUserIdAndIsActiveTrue(Long merchantId, Long userId);

}
