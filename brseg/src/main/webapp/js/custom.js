/**
 *  @author Carlos Diego
 *  @since 26/11/2013
 */


/**
 * Funcao utilizada para deixar caracteres maiusculos
 * 
 * @param campo
 */

function upper(campo)
{
	var str = campo.value;
	campo.value = str.toUpperCase();
}


function soNumero(evt, campo)
{
	if (navigator.appCodeName == 'Mozilla' && (navigator.appName == 'Netscape' || navigator.appName == 'Opera'))
	{
		if (evt.which)
		{
			if ((evt.which < 48 || evt.which > 57) && evt.which != 8 && evt.which != 9)
			{
				if(navigator.appName == 'Opera') // Opera
				{
					if((evt.which == 86 || evt.which == 67) && evt.ctrlKey) // Ctrl + V ou Ctrl + C
					{
						return true;
					}
				}
				else // Firefox, Chrome, Safari
				{
					if((evt.which == 118 || evt.which == 99) && evt.ctrlKey) // Ctrl + V ou Ctrl + C
					{
						return true;
					}
				}	
				return false;
			}
		}
	}
	else // IE
	{
		if (evt.keyCode < 48 || evt.keyCode > 57)
		{
			return false;
		}
	}
	
	return true;
}

function validarEmail(campo)
{
	if(campo.value == "")
	{
		return;
	}
	
	var usuario = campo.value.substring(0, campo.value.indexOf("@"));
	var dominio = campo.value.substring(campo.value.indexOf("@") + 1, campo.value.length);
	
	if ((usuario.length >= 1) && (dominio.length >= 3)
			&& (usuario.search("@") == -1) && (dominio.search("@") == -1)
			&& (usuario.search(" ") == -1) && (dominio.search(" ") == -1)
			&& (dominio.search(".") != -1) && (dominio.indexOf(".") >= 1)
			&& (dominio.lastIndexOf(".") < dominio.length - 1))
	{
		return true;
	} 
	else
	{
		alert("Favor digitar um e-mail válido!");
		campo.value = "";
	}
}

function validarCPF(campo, cpf)
{
	if(campo.value == "")
	{
		return;
	}
	
	var filtro = /^\d{3}.\d{3}.\d{3}-\d{2}$/i;
	
	if(!filtro.test(cpf))
	{
		alert("CPF inválido! Favor informar um cpf válido.");
		campo.value = "";
		return;
	}
   
	cpf = remove(cpf, ".");
	cpf = remove(cpf, "-");
	
	if(cpf.length != 11 || cpf == "00000000000" || cpf == "11111111111" ||
		cpf == "22222222222" || cpf == "33333333333" || cpf == "44444444444" ||
		cpf == "55555555555" || cpf == "66666666666" || cpf == "77777777777" ||
		cpf == "88888888888" || cpf == "99999999999")
	{
		alert("CPF inválido! Favor informar um CPF válido.");
		campo.value = "";
		return;
   }

	soma = 0;
	for(i = 0; i < 9; i++)
	{
		soma += parseInt(cpf.charAt(i)) * (10 - i);
	}
	
	resto = 11 - (soma % 11);
	if(resto == 10 || resto == 11)
	{
		resto = 0;
	}
	if(resto != parseInt(cpf.charAt(9))){
		alert("CPF inválido! Favor informar um cpf válido.");
		campo.value = "";
		return;
	}
	
	soma = 0;
	for(i = 0; i < 10; i ++)
	{
		soma += parseInt(cpf.charAt(i)) * (11 - i);
	}
	resto = 11 - (soma % 11);
	if(resto == 10 || resto == 11)
	{
		resto = 0;
	}
	
	if(resto != parseInt(cpf.charAt(10))){
		alert("CPF inválido! Favor informar um CPF válido.");
		campo.value = "";
		reurn;
	}
	
	return true;
 }
 
function remove(str, sub)
{
	i = str.indexOf(sub);
	r = "";
	if (i == -1) return str;
	{
		r += str.substring(0,i) + remove(str.substring(i + sub.length), sub);
	}
	
	return r;
}

function mascara(o,f)
{
	v_obj=o
	v_fun=f
	setTimeout("execmascara()",1)
}

function execmascara()
{
	v_obj.value=v_fun(v_obj.value)
}

function cpf_mask(v)
{
	v=v.replace(/\D/g,"")                 //Remove tudo o que não é dígito
	v=v.replace(/(\d{3})(\d)/,"$1.$2")    //Coloca ponto entre o terceiro e o quarto dígitos
	v=v.replace(/(\d{3})(\d)/,"$1.$2")    //Coloca ponto entre o setimo e o oitava dígitos
	v=v.replace(/(\d{3})(\d)/,"$1-$2")   //Coloca ponto entre o decimoprimeiro e o decimosegundo dígitos
	return v
}

function mtel(v)
{
	v=v.replace(/\D/g,"")                 //Remove tudo o que não é dígito
	v=v.replace(/^(\d{2})(\d)/g,"($1) $2");
	v=v.replace(/(\d)(\d{4})$/,"$1-$2");
	return v
}

function mcep(v){
    v=v.replace(/\D/g,"")                    //Remove tudo o que não é dígito
    v=v.replace(/^(\d{5})(\d)/,"$1-$2")         //Esse é tão fácil que não merece explicações
    return v
}

function formataData(evt, campo) 
{
	if (soNumero(evt, campo))
	{
		var unicode = (evt.which) ? evt.which : evt.keyCode;

		vr = campo.value;
		vr = vr.replace( ".", "" );
		vr = vr.replace( "-", "" );
		vr = vr.replace( "/", "" );
		vr = vr.replace( "/", "" );
		vr = vr.replace( "/", "" );
		tam = vr.length + 1;
		
		if (unicode >= 48 && unicode <= 57)
		{
			if (tam > 2 && tam < 5)
				campo.value = vr.substr(0, 2) + '/' + vr.substr(2, tam);
			if (tam >= 5 && tam <= 10)
				campo.value = vr.substr(0, 2) + '/' + vr.substr(2, 2) + '/' + vr.substr(4, 4); 
		}
		return true;
	}
	return false;
}

function verificaTamanho(campo, tamMax, resposta)
{
	var strLen;
	strLen = 0;
	if(campo.value.length > 0)
	{
		strLen = strLen + campo.value.length;
	}
	if (strLen == 1)
   	{
	   	if(campo.value.substring(0,1) == " ")
		{
	       	campo.value = "";
			strLen = strLen - 1;
		}
   	}
	if (strLen > tamMax)
	{
		campo.value = campo.value.substring(0,tamMax);
		strLen = strLen - 1;
		return;
	}
	if(resposta != null)
	{	
		if(document.forms['contexto'].elements[resposta] != null)
		{
			document.forms['contexto'].elements[resposta].value = (tamMax - strLen);
		}
		else
		{
			document.getElementById(resposta).innerHTML = '(' + (tamMax - strLen) + ' caracteres restantes)';
		}
	}
}