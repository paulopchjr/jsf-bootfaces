package br.com.consultoria.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import br.com.consultoria.dao.DaoGeneric;
import br.com.consultoria.entidades.Lancamento;
import br.com.consultoria.entidades.Pessoa;
import br.com.consultoria.repository.IDaoLancamento;
import br.com.consultoria.repository.IDaoLancamentoImpl;

@ViewScoped
@ManagedBean(name = "lancamentoBean")
public class LancamentoBean {

	/* Instanciando o modelo */
	private Lancamento lancamentos = new Lancamento();
	private DaoGeneric<Lancamento> daoGeneric = new DaoGeneric<Lancamento>();
	private List<Lancamento> listaLancamentos = new ArrayList<Lancamento>();

	private IDaoLancamento daoLancamento = new IDaoLancamentoImpl();

	/*Salvar Nota Fiscal*/
	public String salvar() {
		FacesContext context = FacesContext.getCurrentInstance(); // obter qualquer informação do jsf
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		lancamentos.setUsuario(pessoaUser);
		lancamentos = daoGeneric.merge(lancamentos);
		carregarLancamentos();
		
		return "";
	}

	@PostConstruct
	private void carregarLancamentos()  {
		FacesContext context = FacesContext.getCurrentInstance(); // obter qualquer informação do jsf
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		listaLancamentos = daoLancamento.consultar(pessoaUser.getId());
		
	}

	/* Novo */
	public String novo() {
		lancamentos = new Lancamento();
		carregarLancamentos();
		return "";
	}
	
	public String atualiza() {
		return "";
	}
	
	public String remove() {
		daoGeneric.deletepoId(lancamentos);
		lancamentos = new Lancamento();
		carregarLancamentos();
		return "";
	}
	
	
	
	

	public Lancamento getLancamento() {
		return lancamentos;
	}

	public void setLancamento(Lancamento lancamento) {
		this.lancamentos = lancamento;
	}

	public DaoGeneric<Lancamento> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Lancamento> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}

	public List<Lancamento> getListaLancamentos() {
		return listaLancamentos;
	}

	public void setListaLancamentos(List<Lancamento> listaLancamentos) {
		this.listaLancamentos = listaLancamentos;
	}

}
