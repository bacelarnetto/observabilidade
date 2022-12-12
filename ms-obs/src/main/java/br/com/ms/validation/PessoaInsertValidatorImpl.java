package br.com.ms.validation;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.ms.dto.PessoaNewDTO;
import br.com.ms.model.entity.Pessoa;
import br.com.ms.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.ms.message.FieldMessage;



public class PessoaInsertValidatorImpl implements ConstraintValidator<PessoaInsertValidation, PessoaNewDTO> {

	@Autowired
	private PessoaRepository repo;
	
	@Override
	public void initialize(PessoaInsertValidation ann) {
	}

	@Override
	public boolean isValid(PessoaNewDTO objDto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();

		Pessoa aux = repo.findByEmail(objDto.getEmail());
		if (aux != null) {
			list.add(new FieldMessage("email", "Email já existente"));
		}
		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}

}