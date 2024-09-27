package br.edu.ibmec.projeto_cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import br.edu.ibmec.projeto_cloud.model.Notificacao;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Integer> {
    
}
