package br.com.ms.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import br.com.ms.dto.PessoaDTO;
import br.com.ms.dto.PessoaNewDTO;
import br.com.ms.model.entity.Pessoa;
import br.com.ms.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/pessoa")
public class PessoaController {

	@Autowired
	private PessoaService pessoaService;

	@ApiOperation(value="Cadastrar uma Pessoa")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> insert(@Valid @RequestBody PessoaNewDTO dto) {
		Pessoa pessoa = pessoaService.fromTO(dto);
		pessoa = pessoaService.insert(pessoa);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(pessoa.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}

	@ApiOperation(value="Atualizar uma Pessoa por id")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> update(@Valid @PathVariable Integer id, @RequestBody PessoaDTO dto) {
		Pessoa pessoa = pessoaService.fromTO(dto);
		if(id != null) {
			pessoa.setId(id);
		}
		pessoa = pessoaService.update(pessoa);
		return ResponseEntity.noContent().build();
	}

	@ApiOperation(value="Excluir uma Pessoa por id")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		pessoaService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@ApiOperation(value="Buscar Pessoa por id")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Pessoa> find(@PathVariable Integer id) {
		Pessoa pessoa = pessoaService.find(id);
		return ResponseEntity.ok().body(pessoa);
	}

	@ApiOperation(value="Buscar a lista com todas as Pessoas")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<PessoaDTO>> findAll() {
		List<Pessoa> list = pessoaService.findAll();
		List<PessoaDTO> listDTO = list.stream().map(pessoa -> new PessoaDTO(pessoa)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listDTO);
	}

}
