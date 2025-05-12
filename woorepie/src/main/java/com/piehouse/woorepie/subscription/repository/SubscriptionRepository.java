package com.piehouse.woorepie.subscription.repository;

import com.piehouse.woorepie.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findBySubState(short subState);
}
