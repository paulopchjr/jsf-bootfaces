package br.com.consultoria.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import br.com.consultoria.dao.DaoGeneric;
import br.com.consultoria.entidades.Cidades;

@ViewScoped
@ManagedBean(name = "cidadeBean")
public class CidadeBean {
	
	private Cidades cidades = new Cidades();
	private DaoGeneric<Cidades>daoGeneric = new DaoGeneric<Cidades>();
	private List<Cidades>listadeCidades = new ArrayList<Cidades>();
	
	
	public String novo() {
		cidades = new Cidades();
		return "";
	}
	
	public String limpar() {
		cidades = new Cidades();
		return "";
	}
	
	public String save() {
		cidades = daoGeneric.merge(cidades);
		carregarListaCidades();
		mostrarMensagem("Cidade Cadastrada com Sucesso");
		return "";
	}
	
	public String  delete(){
		daoGeneric.deletepoId(cidades);
		cidades = new Cidades();
		carregarListaCidades();
		mostrarMensagem("Cidade exlcuída com sucesso");
		
		
		return "";
	}
	
	private  void mostrarMensagem(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message =new FacesMessage(msg);
		context.addMessage(msg, message);
	}
	
	@PostConstruct
	public void carregarListaCidades() {
		listadeCidades = daoGeneric.getListEntity(Cidades.class);
	}
	
	
	public Cidades getCidades() {
		return cidades;
	}
	public void setCidades(Cidades cidades) {
		this.cidades = cidades;
	}
	public DaoGeneric<Cidades> getDaoGeneric() {
		return daoGeneric;
	}
	public void setDaoGeneric(DaoGeneric<Cidades> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}
	public List<Cidades> getListadeCidades() {
		return listadeCidades;
	}
	public void setListadeCidades(List<Cidades> listadeCidades) {
		this.listadeCidades = listadeCidades;
	}
	
	
	

}
