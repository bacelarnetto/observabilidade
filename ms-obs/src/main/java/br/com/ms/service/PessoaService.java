package br.com.ms.service;

import java.util.List;
import java.util.Optional;

import br.com.ms.dto.PessoaDTO;
import br.com.ms.dto.PessoaNewDTO;
import br.com.ms.model.entity.Pessoa;
import br.com.ms.repository.PessoaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import br.com.ms.exception.DataIntegrityException;
import br.com.ms.exception.ObjectNotFoundException;

@Service
public class PessoaService {

	Logger logger = LoggerFactory.getLogger(PessoaService.class);

	@Autowired
	private PessoaRepository repo;

	public Pessoa find(Integer id) {
		logger.info(":::: INICIO >> buscar Pessoa  :::: COD: " + id);
		Optional<Pessoa> obj = repo.findById(id);
		logger.info(":::: FIM >> buscar Pessoa  :::: COD: " + obj.get().getId() + " - NOME: "+ obj.get().getNome() );
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Pessoa.class.getName()));
	}

	@Transactional
	public Pessoa insert(Pessoa pessoa) {
		logger.info(":::: INICIO >> Cadastro de Pessoa  ::::");
		pessoa.setId(null);
		pessoa = repo.save(pessoa);
		logger.info(":::: FIM >> Cadastro de Pessoa  ::::");
		return pessoa;
	}

	public Pessoa update(Pessoa pessoa) {
		logger.info(":::: INICIO >> Atualizacao de Pessoa  ::::");
		logger.info("->Valida se Pessoa Existe - Pessoa: " +pessoa.getId()+" - "+ pessoa.getNome());
		Pessoa newPessoa = find(pessoa.getId());
		logger.info("->Atualiza Pessoa");
		updateData(newPessoa, pessoa);
		Pessoa p = repo.save(newPessoa);
		logger.info(":::: FIM >> Atualizacao de Pessoa  ::::");
		return p;
	}

	public void delete(Integer id) {
		logger.info(":::: INICIO >> Exclusao de Pessoa  ::::");
		find(id);
		try {
			logger.info("-> COD:" + id);
			repo.deleteById(id);
			logger.info(":::: FIM >> Exclusao de Pessoa  ::::");
		} catch (DataIntegrityViolationException e) {
			logger.error("-> Não é possível excluir uma categoria que possui produtos.");
			logger.error(":::: FIM COM ERRO >> Exclusao de Pessoa  :::: COD:");
			throw new DataIntegrityException("Não é possível excluir uma categoria que possui produtos");
		}
	}

	public List<Pessoa> findAll() {
		logger.info(":::: INICIO >> Busca da lista com todas as Pessoas  ::::");
		List<Pessoa> list = repo.findAll();
		logger.info(":::: FIM >> Busca da lista com todas as Pessoas  ::::");
		return list;

	}

	private void updateData(Pessoa newPessoa, Pessoa pessoa) {
		newPessoa.setNome(pessoa.getNome());
		newPessoa.setEmail(pessoa.getEmail());
		newPessoa.setAltura(pessoa.getAltura());
		newPessoa.setPeso(pessoa.getPeso());
		newPessoa.setIdade(pessoa.getIdade());
	}

	public Pessoa fromTO(PessoaNewDTO dto) {
		logger.info(":::: INICIO >> Busca da lista paginada de Pessoas  ::::");
		return new Pessoa(null, dto.getNome(), dto.getEmail(), dto.getIdade(), dto.getPeso(), dto.getAltura());
	}

	public Pessoa fromTO(PessoaDTO dto) {
		logger.info(":::: INICIO >> Busca da lista paginada de Pessoas  ::::");
		return new Pessoa(dto.getId(), dto.getNome(), dto.getEmail(), dto.getIdade(), dto.getPeso(), dto.getAltura());
	}

}