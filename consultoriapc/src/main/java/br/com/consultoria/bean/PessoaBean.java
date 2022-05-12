package br.com.consultoria.bean;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;

import br.com.consultoria.dao.DaoGeneric;
import br.com.consultoria.entidades.Cidades;
import br.com.consultoria.entidades.Estados;
import br.com.consultoria.entidades.Pessoa;
import br.com.consultoria.repository.IDaoPessoa;
import br.com.consultoria.repository.IDaoPessoaIpl;
import br.com.consultoria.util.JpaUtil;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	/* Instanciando uma classe */
	private Pessoa pessoa = new Pessoa();
	private DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
	private List<Pessoa> listaPessoa = new ArrayList<Pessoa>();
	private List<SelectItem> estados;
	private List<SelectItem> cidades;

	/* objeto pra trazer o upload */

	private Part arquivofoto;

	/* Injeção de depedencia */
	private IDaoPessoa iDaoPessoa = new IDaoPessoaIpl();

	/* Novo */
	public String novo() {
		pessoa = new Pessoa();
		return "";
	}

	/* Método que salva pessoa */
	public String salvarPessoa() {

		daoGeneric.save(pessoa);
		carregarListaPessoas();
		pessoa = new Pessoa();
		return "";

	}

	public String limpar() {
		pessoa = new Pessoa();
		return "";
	}

	/* Método que atualiza */
	public String atualizaPessoa() throws IOException {

		/*Salvando a imagem em original*/
		byte[] imagemByte = getBtye(arquivofoto.getInputStream());
		pessoa.setFotoBase64Original(imagemByte);
		
		/*Convertendo imagem grande em pequena*/
		BufferedImage bufferedImage = ImageIO.read( new ByteArrayInputStream(imagemByte));
		
		/*Pega o tipo da imagem*/
		int tipoImagem = bufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
		
		/*Largura e altura da imagem*/
		int altura =200;
		int largura = 200;
		
		
		/*Criar a miniatura*/
		BufferedImage redimensionarImagem = new BufferedImage(largura, altura, tipoImagem);
		Graphics2D g = redimensionarImagem.createGraphics();
		g.drawImage(redimensionarImagem, 0, 0, largura, altura, null);
		g.dispose();
	
		/*Escrever novamente a imagem no tamanho menor*/
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 
		 String extensaoArquivo = arquivofoto.getContentType().split("\\/")[1]; /*retorna image/png */
		 ImageIO.write(redimensionarImagem, extensaoArquivo, baos);
		 
		 String miniImagem = "data:"+ arquivofoto.getContentType() +";base64," + DatatypeConverter.printBase64Binary(baos.toByteArray());
		 
		 
		 pessoa.setBase64(miniImagem);
		 pessoa.setExtensao(extensaoArquivo);
		 
		 
		pessoa = daoGeneric.merge(pessoa);
		pessoa = new Pessoa();
		carregarListaPessoas();
		mostrarMsg("Cadastrado com Sucesso!");
		return "";
	}

	
	
	public void editar() {
		if (pessoa.getCidades() != null) {
			Estados estado = pessoa.getCidades().getEstado();
			pessoa.setEstados(estado);
			List<Cidades> listaCidades = JpaUtil.getEntityManager()
					.createQuery("from Cidades where estado.id = " + estado.getId() + "order by nome").getResultList();

			List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();

			for (Cidades cidade : listaCidades) {
				selectItemsCidade.add(new SelectItem(cidade, cidade.getNome()));
			}
			setCidades(selectItemsCidade);
		}

	}

	private void mostrarMsg(String msg) {

		/* Contexto do projeto: ambiente que ele está rodando */
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message); /* Associando um componente jsf com a mensagem */

	}

	/* Método que exclui pessoa */
	public String deletePessoa() {
		daoGeneric.deletepoId(pessoa);
		pessoa = new Pessoa();
		carregarListaPessoas();
		mostrarMsg("Removido com Sucesso");
		return "";

	}

	@PostConstruct /* Carrega os dados na tela */
	public void carregarListaPessoas() {
		listaPessoa = daoGeneric.getListEntity(Pessoa.class);
	}

	public String deslogar() {

		FacesContext context = FacesContext.getCurrentInstance(); // obter qualquer informação do jsf
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("usuarioLogado");

		HttpServletRequest httpServletRequest = (HttpServletRequest) context.getCurrentInstance().getExternalContext()
				.getRequest();

		httpServletRequest.getSession().invalidate();

		return "index.jsf";
	}

	public String logar() {

		Pessoa pessoaUsuario = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());
		if (pessoaUsuario != null) { /* Achou o usuario */

			// add o usuario na sessao
			FacesContext context = FacesContext.getCurrentInstance(); // obter qualquer informação do jsf
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", pessoaUsuario);
			return "pag1.jsf";
		}

		return "index.jsf";
	}

	public boolean permiteAcesso(String acesso) {
		/* Recuperando o Usuario na sessao */
		FacesContext context = FacesContext.getCurrentInstance(); // obter qualquer informação do jsf
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");

		return pessoaUser.getPerfilUser().equals(acesso);
	}

	public void pesquisaCep(AjaxBehaviorEvent event) {
		try {
			URL url = new URL("https://viacep.com.br/ws/" + pessoa.getCep() + "/json/");

			/* Faz o consumo do web service */
			URLConnection connection = url.openConnection();

			/* Executa no web services e retorna os dados */
			InputStream is = connection.getInputStream();

			/* amarzenar */ /* Faz a leitura */
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String cep = "";
			StringBuilder jsonCep = new StringBuilder();

			/* Enquanto tiver linhas */
			while ((cep = br.readLine()) != null) {
				jsonCep.append(cep);

			}

			Pessoa gPessoa = new Gson().fromJson(jsonCep.toString(), Pessoa.class);
			// pessoa.setCep(gPessoa.getCep());
			pessoa.setLogradouro(gPessoa.getLogradouro());
			pessoa.setComplemento(gPessoa.getComplemento());
			pessoa.setBairro(gPessoa.getBairro());
			pessoa.setLocalidade(gPessoa.getLocalidade());
			pessoa.setUf(gPessoa.getUf());
			pessoa.setIbge(gPessoa.getIbge());
			pessoa.setGia(gPessoa.getGia());
			pessoa.setDdd(gPessoa.getDdd());
			pessoa.setSiafi(gPessoa.getSiafi());

		} catch (Exception e) {
			e.printStackTrace();

			mostrarMsg("Erro ao consultar o cep");

		}

	}

	public void carregaCidades(AjaxBehaviorEvent event) {

		/* Pegando o objeto que foi selecionado no combox */
		Estados estado = (Estados) ((HtmlSelectOneMenu) event.getSource()).getValue();

		/* Carregando o estado selecionado na memoria */
		if (estado != null) {
			pessoa.setEstados(estado);

			List<Cidades> listaCidades = JpaUtil.getEntityManager()
					.createQuery("from Cidades where estado.id = " + estado.getId() + "order by nome").getResultList();

			List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();

			for (Cidades cidade : listaCidades) {
				selectItemsCidade.add(new SelectItem(cidade, cidade.getNome()));
			}
			setCidades(selectItemsCidade);
		}
	}

	/* método que converte um arquivo texto, imagem etc para btye */
	private byte[] getBtye(InputStream is) throws IOException {
		int len;
		int size = 1024;
		byte[] buf = null;

		if (is instanceof ByteArrayInputStream) {
			size = is.available();
			buf = new byte[size];
			len = is.read(buf, 0, size);
		} else {
			/* Saída em forma de Bytes */
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			buf = new byte[1024];

			/* Escrevendo linha por linha a saida do byte array */

			while ((len = is.read(buf, 0, size)) != -1) {
				bos.write(buf, 0, size);
			}

			buf = bos.toByteArray();

		}
		return buf;
	}

	public void download() {

		// pegando paramentros
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getInitParameterMap();
		String fileDownloadId = params.get("fileDownloadId");

		System.out.println(fileDownloadId);

	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public DaoGeneric<Pessoa> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Pessoa> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}

	public List<Pessoa> getListaPessoa() {
		return listaPessoa;
	}

	public void setListaPessoa(List<Pessoa> listaPessoa) {
		this.listaPessoa = listaPessoa;
	}

	public List<SelectItem> getEstados() {
		estados = iDaoPessoa.listaEstados();
		return estados;
	}

	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
	}

	public List<SelectItem> getCidades() {

		return cidades;
	}

	public Part getArquivofoto() {
		return arquivofoto;
	}

	public void setArquivofoto(Part arquivofoto) {
		this.arquivofoto = arquivofoto;
	}

}
