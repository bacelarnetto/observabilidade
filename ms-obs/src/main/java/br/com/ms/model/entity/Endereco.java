package br.com.ms.model.entity;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Endereco implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Long id;
	private String cpfCliente;
	private String logradouro;
	private String bairro;
	private String uf;
	private String cep;
	
	public Endereco(Long id, String cpfCliente, String logradouro, String bairro, String uf,
			String cep) {
		super();
		this.id = id;
		this.cpfCliente = cpfCliente;
		this.logradouro = logradouro;
		this.bairro = bairro;
		this.uf = uf;
		this.cep = cep;
	}
	

}
