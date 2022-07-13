package org.foolProof.PasswordVault.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c WHERE c.email = ?1") Client findClientByEmail( String email );

    @Query(value = "SELECT MAX(id) FROM client", nativeQuery = true) int findClientById();

}
