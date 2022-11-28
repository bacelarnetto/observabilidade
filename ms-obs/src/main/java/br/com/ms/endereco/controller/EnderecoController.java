package br.com.ms.endereco.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.ms.endereco.model.entity.Endereco;
import br.com.ms.endereco.service.EnderecoService;

@RestController
@RequestMapping(value="/endereco")
public class EnderecoController {
	
	@Autowired
	private EnderecoService enderecoService;
	
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<Endereco>> findCidades() {
		List<Endereco> list = enderecoService.getEnderecoByCpf() ;
		return ResponseEntity.ok().body(list);
	}

}
