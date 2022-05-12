package br.com.consultoria.repository;

import java.util.List;

import br.com.consultoria.entidades.Lancamento;

public interface IDaoLancamento {
	
	List<Lancamento>consultar(Long codUsuario);

}
