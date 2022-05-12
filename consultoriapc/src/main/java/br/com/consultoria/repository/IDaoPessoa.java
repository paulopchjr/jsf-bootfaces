package br.com.consultoria.repository;

import java.util.List;

import javax.faces.model.SelectItem;

import br.com.consultoria.entidades.Pessoa;

public interface IDaoPessoa {

	Pessoa consultarUsuario(String login, String senha);
	
	List<SelectItem>listaEstados();

}
