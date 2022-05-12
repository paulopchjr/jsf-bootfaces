package br.com.consultoria.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.consultoria.util.JpaUtil;

public class DaoGeneric<E> {

	public void save(E entidade) {
		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.persist(entidade);
		transaction.commit();
		entityManager.close();
	}

	/* Salva, atualiza e retorna os dados */
	public E merge(E entidade) {

		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		E pesquisar_Entidade = entityManager.merge(entidade);
		entityTransaction.commit();
		entityManager.close();
		return pesquisar_Entidade;
	}

	public void delete(E entidade) {

		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		entityManager.remove(entidade);
		entityTransaction.commit();
		entityManager.close();

	}

	public void deletepoId(E entidade) {

		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		Object id = JpaUtil.getPrimaryKey(entidade);
		/* pessoa */
		entityManager.createQuery("delete from " + entidade.getClass().getCanonicalName() + " WHERE id = " + id)
				.executeUpdate();
		entityTransaction.commit();
		entityManager.close();

	}

	/* Meotod que consulta no banco */
	public List<E> getListEntity(Class<E> entidade) {
		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		/*
		 * SELECT * FROM pessoa mas ela está generecia para todos os objtos, classes etc
		 */
		List<E> lista = entityManager.createQuery("FROM " + entidade.getName()).getResultList();
		entityTransaction.commit();
		entityManager.close();

		return lista;
	}

}
