package br.com.ms.controller;

import java.util.List;

import br.com.ms.model.entity.Endereco;
import br.com.ms.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


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
