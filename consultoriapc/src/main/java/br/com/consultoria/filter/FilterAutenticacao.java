package br.com.consultoria.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.consultoria.entidades.Pessoa;
import br.com.consultoria.util.JpaUtil;

@WebFilter("/*")
public class FilterAutenticacao extends HttpFilter implements Filter {


	private static final long serialVersionUID = 1L;

	public FilterAutenticacao() {

	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();

		Pessoa usuarioLogado = (Pessoa) session.getAttribute("usuarioLogado");

		String urlParaAutenticar = req.getServletPath();

		if (!urlParaAutenticar.equalsIgnoreCase("index.jsf") && usuarioLogado == null) {
			request.setAttribute("msg", "Por Favor realize o login");
			request.getRequestDispatcher("/index.jsf").forward(request, response);
			return;
		} else {
			/* executa as ações de REQUISICAO E REPOSTA */
			chain.doFilter(request, response);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {

		/* levanta a conexao com banco de dados */
		JpaUtil.getEntityManager();

	}

}
