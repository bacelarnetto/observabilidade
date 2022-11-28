package br.com.ms.endereco.service;
import java.util.ArrayList;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.ms.endereco.model.entity.Endereco;

@Service
public class EnderecoService {

	Logger logger = LoggerFactory.getLogger(EnderecoService.class);

	public List<Endereco> getEnderecoByCpf() {

		logger.info(":::: INICIO >> buscar Lista de endereços  ::::");

		List<Endereco> list = new ArrayList<Endereco>();
		list.add(new Endereco(1l, "233445", "Rua 5", "Dias M",  "SP", "232323"));
		list.add(new Endereco(2l, "343545", "Rua 6", "Dias Gomes",  "SP", "234345"));

		logger.info(":::: FIM >> buscar Lista de endereços ::::");

		return list;
	}
}