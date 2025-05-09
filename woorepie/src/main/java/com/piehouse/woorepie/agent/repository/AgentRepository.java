package com.piehouse.woorepie.agent.repository;

import com.piehouse.woorepie.agent.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    boolean existsByAgentEmail(String email);

    boolean existsByAgentPhoneNumber(String phoneNumber);

}
