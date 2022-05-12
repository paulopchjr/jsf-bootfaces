package br.com.consultoria.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.consultoria.entidades.Lancamento;
import br.com.consultoria.util.JpaUtil;

public class IDaoLancamentoImpl  implements IDaoLancamento{

	@Override
	public List<Lancamento> consultar(Long codUsuario) {
		List<Lancamento> lista = null;
		
		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		lista = entityManager.createQuery("from Lancamento where usuario.id= " + codUsuario).getResultList();
		transaction.commit();
		entityManager.close();
		
		
		return lista;
	}

}
