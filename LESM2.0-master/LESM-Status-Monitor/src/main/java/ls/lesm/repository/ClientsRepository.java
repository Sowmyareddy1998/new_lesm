package ls.lesm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ls.lesm.model.Clients;

public interface ClientsRepository extends JpaRepository<Clients, Integer> {

	List<Clients> findByClientsNamesStartsWith(String clientsNames);

	Clients findByClientsNames(String clientsNames);

}
