package br.com.a2dm.brseg.bean;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import br.com.a2dm.brcmn.entity.Grupo;
import br.com.a2dm.brcmn.entity.GrupoTelaAcao;
import br.com.a2dm.brcmn.entity.Sistema;
import br.com.a2dm.brcmn.entity.TelaAcao;
import br.com.a2dm.brcmn.service.GrupoService;
import br.com.a2dm.brcmn.service.GrupoTelaAcaoService;
import br.com.a2dm.brcmn.service.SistemaService;
import br.com.a2dm.brcmn.service.TelaAcaoService;
import br.com.a2dm.brcmn.util.jsf.AbstractBean;
import br.com.a2dm.brcmn.util.jsf.JSFUtil;
import br.com.a2dm.brcmn.util.jsf.Variaveis;
import br.com.a2dm.brcmn.util.validacoes.ValidaPermissao;

@RequestScoped
@ManagedBean
public class GrupoBean extends AbstractBean<Grupo, GrupoService>
{
	private BigInteger idSistema;
	private List<Sistema> listaSistema;
	private List<TelaAcao> listaPermissoes;
	private BigInteger listaPermissoesDestino[];
	private String mensagem;
	
	private final String LISTA_PERMISSOES_SESSAO = "permissoes";
	
	private JSFUtil util = new JSFUtil();
	
	public GrupoBean()
	{
		super(GrupoService.getInstancia());
		this.ACTION_SEARCH = "grupo";
		this.pageTitle = "Grupo";
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
	public void preparaAlterar() 
	{
		try
		{
			if(validarAcesso(Variaveis.ACAO_PREPARA_ALTERAR))
			{
				super.preparaAlterar();
				Grupo grupo = new Grupo();
				grupo.setIdGrupo(getEntity().getIdGrupo());
				grupo = GrupoService.getInstancia().get(grupo, GrupoService.JOIN_USUARIO_CAD);
				
				setEntity(grupo);
			}
		}
	    catch (Exception e)
	    {
	       FacesMessage message = new FacesMessage(e.getMessage());
	       message.setSeverity(FacesMessage.SEVERITY_ERROR);
	       FacesContext.getCurrentInstance().addMessage(null, message);
	    }
	}
	
	public void preparaConfigurar()
	{
		try
		{
			this.carregarSistema();
			this.setIdSistema(new BigInteger("-1"));
			this.setListaPermissoes(null);
			this.setListaPermissoesDestino(null);
		}
		catch (Exception e) 
		{
			FacesMessage message = new FacesMessage(e.getMessage());
	        message.setSeverity(FacesMessage.SEVERITY_ERROR);
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void configurar(ActionEvent event)
	{
		try
		{
			this.setMensagem(null);
			
			if(validarAcesso(Variaveis.ACAO_CONFIGURAR))
			{
				if(this.getIdSistema() == null
						|| this.getIdSistema().intValue() <= 0)
				{
					this.setMensagem("O campo Sistema é obrigatório!");
				}
				
				List<TelaAcao> listaSessao = (List<TelaAcao>) util.getSession().getAttribute(LISTA_PERMISSOES_SESSAO);
				List<GrupoTelaAcao> listaRemovidos = new ArrayList<GrupoTelaAcao>();
				List<GrupoTelaAcao> listaAdicionados = new ArrayList<GrupoTelaAcao>();
				List<GrupoTelaAcao> listaFinal = new ArrayList<GrupoTelaAcao>();
				
				for(TelaAcao ta : listaSessao)
				{			
					boolean flag = true;
					for(int i = 0; i < this.listaPermissoesDestino.length; i++)
					{
						if(ta.getIdTelaAcao().intValue() == listaPermissoesDestino[i].intValue())
						{
							flag = false;
						}
					} 
					
					if(flag)
					{
						//SE FOI REMOVIDO, SETAR N
						GrupoTelaAcao gta = new GrupoTelaAcao();
						gta.setIdTelaAcao(ta.getIdTelaAcao());
						gta.setIdGrupo(this.getEntity().getIdGrupo());
						listaRemovidos.add(gta);
					}
				}
				
				for(int i = 0; i < this.listaPermissoesDestino.length; i++)
				{
					boolean flag = true;
					for(TelaAcao ta : listaSessao)
					{
						if(ta.getIdTelaAcao().intValue() == listaPermissoesDestino[i].intValue())
						{
							flag = false;
						}
					}
					if(flag)
					{
						GrupoTelaAcao gta = new GrupoTelaAcao();
						gta.setFiltroMap(new HashMap<String, Object>());
						gta.getFiltroMap().put("idSistema", this.getIdSistema());
						gta.setIdGrupo(this.getEntity().getIdGrupo());
						gta.setIdTelaAcao(new BigInteger(listaPermissoesDestino[i].toString()));
						gta.setIdUsuarioCad(util.getUsuarioLogado().getIdUsuario());
						
						listaAdicionados.add(gta);
					}
				}
				
				listaFinal.addAll(listaRemovidos);
				listaFinal.addAll(listaAdicionados);
				
				GrupoTelaAcaoService.getInstancia().inserirPermissoes(listaFinal);
				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Configurações realizadas com sucesso!", null));
			}
		}
		catch (Exception e) 
		{
			this.setMensagem(e.getMessage());
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
	}
	
	public void inativar() 
	{		
		try
		{
			if(this.getEntity() != null)
			{
				if(validarAcesso(Variaveis.ACAO_INATIVAR))
				{
					GrupoService.getInstancia().inativar(this.getEntity());
					
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
					GrupoService.getInstancia().ativar(this.getEntity());
					
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
	
	public void carregarPermissoes()
	{
		try
		{
			util.getSession().removeAttribute(LISTA_PERMISSOES_SESSAO);
			
			
			this.carregarPermissoesDestino();
			
			TelaAcao telaAcao = new TelaAcao();
			telaAcao.setIdSistema(this.getIdSistema());
			
			List<TelaAcao> lista = TelaAcaoService.getInstancia().pesquisar(telaAcao, TelaAcaoService.JOIN_ACAO);
			
			for (TelaAcao obj : lista)
			{
				obj.setDescricao(obj.getDescricao() + " - " + obj.getAcao().getDescricao());
			}
			
			this.setListaPermissoes(lista);
		}
		catch (Exception e)
		{
			FacesMessage message = new FacesMessage(e.getMessage());
	        message.setSeverity(FacesMessage.SEVERITY_ERROR);
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}
	
	private void carregarPermissoesDestino() throws Exception
	{
		TelaAcao telaAcao = new TelaAcao();
		telaAcao.setIdSistema(this.getIdSistema());
		telaAcao.setFlgAtivo("S");
		telaAcao.setFiltroMap(new HashMap<String, Object>());
		telaAcao.getFiltroMap().put("idGrupo", this.getEntity().getIdGrupo());
		telaAcao.getFiltroMap().put("flgAtivoGrupoTelaAcao", "S");
		
		List<TelaAcao> listaPresente = TelaAcaoService.getInstancia().pesquisar(telaAcao, TelaAcaoService.JOIN_GRUPO_TELA_ACAO);
		
		util.getSession().setAttribute(LISTA_PERMISSOES_SESSAO, listaPresente);
		
		BigInteger[] listaPermissoesDestino = null;
		
		if(listaPresente != null && !listaPresente.isEmpty())
		{
			listaPermissoesDestino = new BigInteger[listaPresente.size()];
			
			for (int i = 0; i < listaPresente.size(); i++)
			{
				listaPermissoesDestino[i] = listaPresente.get(i).getIdTelaAcao();
			}
		}
		
		this.setListaPermissoesDestino(listaPermissoesDestino);
	}
	
	private void carregarSistema() throws Exception
	{
		List<Sistema> result = SistemaService.getInstancia().pesquisar(new Sistema(), 0);
		
		Sistema s = new Sistema();
		s.setDescricao("Escolha o Sistema");
		s.setIdSistema(new BigInteger("-1"));
		
		List<Sistema> lista = new ArrayList<Sistema>();
		lista.add(s);
		lista.addAll(result);
		
		this.setListaSistema(lista);
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
	
	@Override
	protected int getJoinPesquisar()
	{
		return GrupoService.JOIN_USUARIO_CAD;
	}
	
	@Override
	protected boolean validarAcesso(String acao)
	{
		boolean temAcesso = true;
		
		if (!ValidaPermissao.getInstancia().verificaPermissao("grupo", acao))
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
	
	@Override
	public void cancelar(ActionEvent event)
	{		
		super.cancelar(event);
		this.getSearchObject().setFlgAtivo("T");
	}

	public List<TelaAcao> getListaPermissoes() {
		return listaPermissoes;
	}

	public void setListaPermissoes(List<TelaAcao> listaPermissoes) {
		this.listaPermissoes = listaPermissoes;
	}

	public BigInteger[] getListaPermissoesDestino() {
		return listaPermissoesDestino;
	}

	public void setListaPermissoesDestino(BigInteger[] listaPermissoesDestino) {
		this.listaPermissoesDestino = listaPermissoesDestino;
	}

	public BigInteger getIdSistema() {
		return idSistema;
	}

	public void setIdSistema(BigInteger idSistema) {
		this.idSistema = idSistema;
	}

	public List<Sistema> getListaSistema() {
		return listaSistema;
	}

	public void setListaSistema(List<Sistema> listaSistema) {
		this.listaSistema = listaSistema;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}