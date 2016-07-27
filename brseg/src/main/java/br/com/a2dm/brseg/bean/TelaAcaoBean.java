package br.com.a2dm.brseg.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import br.com.a2dm.brcmn.entity.Acao;
import br.com.a2dm.brcmn.entity.Sistema;
import br.com.a2dm.brcmn.entity.TelaAcao;
import br.com.a2dm.brcmn.service.AcaoService;
import br.com.a2dm.brcmn.service.SistemaService;
import br.com.a2dm.brcmn.service.TelaAcaoService;
import br.com.a2dm.brcmn.util.jsf.AbstractBean;
import br.com.a2dm.brcmn.util.jsf.JSFUtil;
import br.com.a2dm.brcmn.util.jsf.Variaveis;
import br.com.a2dm.brcmn.util.validacoes.ValidaPermissao;

@RequestScoped
@ManagedBean
public class TelaAcaoBean extends AbstractBean<TelaAcao, TelaAcaoService>
{
	private List<Sistema> listaSistema;
	private List<Acao> listaAcao;
	
	private JSFUtil util = new JSFUtil();
	
	public TelaAcaoBean()
	{
		super(TelaAcaoService.getInstancia());
		this.ACTION_SEARCH = "telaAcao";
		this.pageTitle = "Ação";
	}
	
	@Override
	public String preparaPesquisar()
	{
		String retorno = null;
		
		try
		{
			if(validarAcesso(Variaveis.ACAO_PREPARA_PESQUISAR))
			{
				retorno = super.preparaPesquisar();
				this.getSearchObject().setFlgAtivo("T");
				this.carregarSistema();
			}
		}
		catch (Exception e)
		{
			FacesMessage message = new FacesMessage(e.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		
		return retorno;
	}
	
	@Override
	public void preparaInserir(ActionEvent event) 
	{
		try 
		{
			if(validarAcesso(Variaveis.ACAO_PREPARA_INSERIR))
			{
				super.preparaInserir(event);
				this.carregarSistema();
				this.carregarAcao();
			}
		}
		catch (Exception e) 
		{
			FacesMessage message = new FacesMessage(e.getMessage());
	        message.setSeverity(FacesMessage.SEVERITY_ERROR);
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}
	
	@Override
	public void preparaAlterar() 
	{
		try
		{
			if(validarAcesso(Variaveis.ACAO_PREPARA_ALTERAR))
			{
				super.preparaAlterar();
				TelaAcao telaAcao = new TelaAcao();
				telaAcao.setIdTelaAcao(getEntity().getIdTelaAcao());
				telaAcao = TelaAcaoService.getInstancia().get(telaAcao, TelaAcaoService.JOIN_USUARIO_CAD
																	  | TelaAcaoService.JOIN_SISTEMA
																	  | TelaAcaoService.JOIN_ACAO);
				
				setEntity(telaAcao);
				
				this.carregarAcao();
			}
		}
	    catch (Exception e)
	    {
	       FacesMessage message = new FacesMessage(e.getMessage());
	       message.setSeverity(FacesMessage.SEVERITY_ERROR);
	       FacesContext.getCurrentInstance().addMessage(null, message);
	    }
	}
	
	@Override
	protected void validarInserir() throws Exception
	{
		if(this.getEntity() == null
				|| this.getEntity().getDescricao() == null
				|| this.getEntity().getDescricao().equals(""))
		{
			throw new Exception("O campo Descrição é obrigatório!");
		}
		
		if(this.getEntity() == null
				|| this.getEntity().getPagina() == null
				|| this.getEntity().getPagina().equals(""))
		{
			throw new Exception("O campo Página é obrigatório!");
		}
		
		if(this.getEntity() == null
				|| this.getEntity().getIdSistema() == null
				|| this.getEntity().getIdSistema().intValue() == 0)
		{
			throw new Exception("O campo Sistema é obrigatório!");
		}
		
		if(this.getEntity() == null
				|| this.getEntity().getIdAcao() == null
				|| this.getEntity().getIdAcao().intValue() == 0)
		{
			throw new Exception("O campo Ação é obrigatório!");
		}
	}
	
	@Override
	protected void completarInserir() throws Exception
	{
		this.getEntity().setFlgAtivo("S");
		this.getEntity().setDataCadastro(new Date());
		this.getEntity().setIdUsuarioCad(util.getUsuarioLogado().getIdUsuario());
	}
	
	@Override
	protected void completarAlterar() throws Exception 
	{
		this.validarInserir();
		this.getEntity().setDataAlteracao(new Date());
		this.getEntity().setIdUsuarioAlt(util.getUsuarioLogado().getIdUsuario());
	}
	
	@Override
	protected void completarPesquisar() throws Exception
	{
		if(this.getSearchObject().getFlgAtivo() != null
				&& this.getSearchObject().getFlgAtivo().equals("T"))
		{
			this.getSearchObject().setFlgAtivo(null);
		}
	}
	
	public void inativar() 
	{		
		try
		{
			if(this.getEntity() != null)
			{
				if(validarAcesso(Variaveis.ACAO_INATIVAR))
				{
					TelaAcaoService.getInstancia().inativar(this.getEntity());
					
					FacesMessage message = new FacesMessage("Registro inativado com sucesso!");
					message.setSeverity(FacesMessage.SEVERITY_INFO);
					FacesContext.getCurrentInstance().addMessage(null, message);
				}
			}
		}
		catch (Exception e) 
		{
			FacesMessage message = new FacesMessage(e.getMessage());
	        message.setSeverity(FacesMessage.SEVERITY_ERROR);
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}		
	}
	
	public void ativar() 
	{		
		try
		{
			if(this.getEntity() != null)
			{
				if(validarAcesso(Variaveis.ACAO_ATIVAR))
				{
					TelaAcaoService.getInstancia().ativar(this.getEntity());
					
					FacesMessage message = new FacesMessage("Registro ativado com sucesso!");
					message.setSeverity(FacesMessage.SEVERITY_INFO);
					FacesContext.getCurrentInstance().addMessage(null, message);
				}
			}
		}
		catch (Exception e) 
		{
			FacesMessage message = new FacesMessage(e.getMessage());
	        message.setSeverity(FacesMessage.SEVERITY_ERROR);
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}		
	}
	
	@Override
	public void cancelar(ActionEvent event)
	{		
		super.cancelar(event);
		this.getSearchObject().setFlgAtivo("T");
	}

	@Override
	protected int getJoinPesquisar()
	{
		return TelaAcaoService.JOIN_SISTEMA
			 | TelaAcaoService.JOIN_ACAO
			 | TelaAcaoService.JOIN_USUARIO_CAD ;
	}
	
	private void carregarSistema() throws Exception
	{
		List<Sistema> result = SistemaService.getInstancia().pesquisar(new Sistema(), 0);
		
		Sistema s = new Sistema();
		s.setDescricao("Escolha o Sistema");
		
		List<Sistema> lista = new ArrayList<Sistema>();
		lista.add(s);
		lista.addAll(result);
		
		this.setListaSistema(lista);
	}
	
	private void carregarAcao() throws Exception
	{
		List<Acao> result = AcaoService.getInstancia().pesquisar(new Acao(), 0);
		
		Acao a = new Acao();
		a.setDescricao("Escolha a Ação");
		
		List<Acao> lista = new ArrayList<Acao>();
		lista.add(a);
		lista.addAll(result);
		
		this.setListaAcao(lista);
	}

	@Override
	protected boolean validarAcesso(String acao)
	{
		boolean temAcesso = true;

		if (!ValidaPermissao.getInstancia().verificaPermissao("telaAcao", acao))
		{
			temAcesso = false;
			HttpServletResponse rp = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			try
			{
				rp.sendRedirect("/brseg/pages/acessoNegado.jsf");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return temAcesso;
	}
	
	
	public List<Sistema> getListaSistema() {
		return listaSistema;
	}

	public void setListaSistema(List<Sistema> listaSistema) {
		this.listaSistema = listaSistema;
	}

	public List<Acao> getListaAcao() {
		return listaAcao;
	}

	public void setListaAcao(List<Acao> listaAcao) {
		this.listaAcao = listaAcao;
	}
}