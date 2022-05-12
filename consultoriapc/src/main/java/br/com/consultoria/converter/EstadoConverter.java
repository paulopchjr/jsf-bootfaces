package br.com.consultoria.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.consultoria.entidades.Estados;
import br.com.consultoria.util.JpaUtil;

@FacesConverter(forClass = Estados.class, value="estadoConverter")
public class EstadoConverter  implements Converter, Serializable{

 static final long serialVersionUID = 1L;

	@Override /*Retorna o objeto inteiro*/
	public Object getAsObject(FacesContext context, UIComponent component, String codigoEstado) {
		
		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		/*Consulta no banco */
		Estados estados = (Estados) entityManager.find(Estados.class, Long.parseLong(codigoEstado));
		
		return estados;
	}

	@Override /*Retorna apenas o código em String*/
	public String getAsString(FacesContext context, UIComponent component, Object estado) {
		
		if(estado == null) {
			return null;
		}
		if(estado instanceof Estados) {
			return ((Estados) estado).getId().toString();
			
		}else {
			return estado.toString();
		}
	}

	
}
