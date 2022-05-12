package br.com.consultoria.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaUtil {
	
	private static EntityManagerFactory factory = null;
	
	static {
		if(factory == null) {
			factory = Persistence.createEntityManagerFactory("consultoriapc");
			
		}
	}
	
	
	/*retornando o factory*/

	public static EntityManager getEntityManager() {
		return  factory.createEntityManager() ;
	}
	
	
	/*pegando o id do obejto para remover*/
	public static Object getPrimaryKey(Object entity) {
		return factory.getPersistenceUnitUtil().getIdentifier(entity);
	}

}
