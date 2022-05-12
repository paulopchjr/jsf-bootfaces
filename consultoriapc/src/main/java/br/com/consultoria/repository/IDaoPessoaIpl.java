package br.com.consultoria.repository;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.consultoria.entidades.Estados;
import br.com.consultoria.entidades.Pessoa;
import br.com.consultoria.util.JpaUtil;

public class IDaoPessoaIpl implements IDaoPessoa {

	@Override
	public Pessoa consultarUsuario(String login, String senha) {

		Pessoa pessoa = null;
		/* Conexao ao banco */
		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		pessoa = (Pessoa) entityManager
				.createQuery("SELECT p from Pessoa  p where p.login = '" + login + "' and senha = '" + senha + "'")
				.getSingleResult();

		transaction.commit();
		entityManager.close();

		return pessoa;
	}

	@Override
	public List<SelectItem> listaEstados() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		List<Estados> estados = entityManager.createQuery("from Estados").getResultList();

		for (Estados estado : estados) {
			/* Passando o objeto para o combox e a label para o usuarios */
			selectItems.add(new SelectItem(estado, estado.getNome()));
		}

		return selectItems;
	}

}
