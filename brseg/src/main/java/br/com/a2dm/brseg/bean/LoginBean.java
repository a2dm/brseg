package br.com.a2dm.brseg.bean;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import br.com.a2dm.brcmn.entity.Usuario;
import br.com.a2dm.brcmn.service.UsuarioService;
import br.com.a2dm.brcmn.util.criptografia.CriptoMD5;
import br.com.a2dm.brcmn.util.jsf.AbstractBean;
import br.com.a2dm.brcmn.util.jsf.JSFUtil;


@RequestScoped
@ManagedBean
public class LoginBean extends AbstractBean<Usuario, UsuarioService>
{
	private Usuario usuario;
	private String emailRecuperarSenha;
	private String mensagem;
	
	private static final String ACAO_SUCESSO = "principal";
	private static final String ACAO_LOGOUT  = "login";
	
	public LoginBean()
	{
		usuario = new Usuario(); 
	}

	public String login()
	{
		JSFUtil util = new JSFUtil();
		
		try
		{
			//VALIDANDO CAMPO LOGIN			
			if(this.usuario.getLogin() == null 
					|| this.usuario.getLogin().trim().equals(""))
			{
				//MENSAGEM DE VALIDACAO
				
				FacesMessage message = new FacesMessage("O campo Login é obrigatório!");
		        message.setSeverity(FacesMessage.SEVERITY_ERROR);
		        FacesContext.getCurrentInstance().addMessage(null, message);
				return null;
			}
			
			//VALIDANDO CAMPO SENHA
			if(this.usuario.getSenha() == null 
					|| this.usuario.getSenha().trim().equals(""))
			{
				//MENSAGEM DE VALIDACAO
				
				FacesMessage message = new FacesMessage("O campo Senha é obrigatório!");
		        message.setSeverity(FacesMessage.SEVERITY_ERROR);
		        FacesContext.getCurrentInstance().addMessage(null, message);
				return null;
			}

			Usuario usuario = new Usuario();
			usuario.setLogin(this.usuario.getLogin().toUpperCase().trim());			
			usuario.setSenha(CriptoMD5.stringHexa(this.usuario.getSenha().toUpperCase()));
			usuario.setFlgAtivo("S");
			
			usuario = UsuarioService.getInstancia().get(usuario, 0);
			
			if(usuario != null)
			{
				if(usuario.getFlgSeguranca() != null
						&& usuario.getFlgSeguranca().equals("N"))
				{
					FacesMessage message = new FacesMessage("Seu usuário não tem permissão para acessar este sistema!");
			        message.setSeverity(FacesMessage.SEVERITY_ERROR);
			        FacesContext.getCurrentInstance().addMessage(null, message);
					return null;
				}
				else
				{
					util.getSession().setAttribute("loginUsuario", usuario);
					util.getSession().setAttribute("dataLogin", new Date());
					return ACAO_SUCESSO;
				}
			}
			else
			{
				FacesMessage message = new FacesMessage("Login ou Senha incorretos!");
		        message.setSeverity(FacesMessage.SEVERITY_ERROR);
		        FacesContext.getCurrentInstance().addMessage(null, message);
				return null;
			}
		}
		catch (Exception e) 
		{
			FacesMessage message = new FacesMessage("Ocorreu um erro inesperado, favor contactar o administrador do sistema!");
	        message.setSeverity(FacesMessage.SEVERITY_ERROR);
	        FacesContext.getCurrentInstance().addMessage(null, message);
			return null;
		}		
		
	}
	
	public String logout()
	{
		
		JSFUtil util = new JSFUtil();
		util.getSession().invalidate();
		
		return ACAO_LOGOUT;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getEmailRecuperarSenha() {
		return emailRecuperarSenha;
	}

	public void setEmailRecuperarSenha(String emailRecuperarSenha) {
		this.emailRecuperarSenha = emailRecuperarSenha;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}
