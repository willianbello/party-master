package br.edu.ulbra.election.party.service;

import br.edu.ulbra.election.party.exception.GenericOutputException;
import br.edu.ulbra.election.party.input.v1.PartyInput;
import br.edu.ulbra.election.party.model.Party;
import br.edu.ulbra.election.party.output.v1.PartyOutput;
import br.edu.ulbra.election.party.output.v1.GenericOutput;
import br.edu.ulbra.election.party.repository.PartyRepository;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class PartyService {



    private final PartyRepository partyRepository;

    private final ModelMapper modelMapper;

    private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_PARTY_NOT_FOUND = "Party not found";
    private static final String MESSAGE_INVALID_NUMBER = "Invalid number";
    private static final String MESSAGE_NUMBER_NOT_FOUND = "Number not found";
    private static final String MESSAGE_NUMBER_OR_CODE_ALREADY_REGISTERED = "number or code already registered";

    @Autowired
    public PartyService(PartyRepository partyRepository, ModelMapper modelMapper){

        this.partyRepository = partyRepository;
        this.modelMapper = modelMapper;

    }

    public List<PartyOutput> getAll(){
        Type partyOutputListType = new TypeToken<List<PartyOutput>>(){}.getType();
        return modelMapper.map(partyRepository.findAll(), partyOutputListType);
    }

    public PartyOutput create(PartyInput partyInput) {
        validateInput(partyInput, false);
        Party party = modelMapper.map(partyInput, Party.class);
        party = partyRepository.save(party);
        return modelMapper.map(party, PartyOutput.class);
    }

    public PartyOutput getById(Long id){
        if (id == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }

        Party party = partyRepository.getById(id).orElse(null);
        if (party == null){
            throw new GenericOutputException(MESSAGE_PARTY_NOT_FOUND);
        }
        System.out.println("ID encontrado : " + party.getId());
        new GenericOutput("ID encontrado : " + party.getId().toString());

        return modelMapper.map(party, PartyOutput.class);
    }

    public String validateByCode(String code){
        if (code == null){
            throw new GenericOutputException(MESSAGE_NUMBER_OR_CODE_ALREADY_REGISTERED);
        }

        String searchCode = partyRepository.findByCode(code).orElse(null);
        if (searchCode != null){
            throw new GenericOutputException(MESSAGE_NUMBER_OR_CODE_ALREADY_REGISTERED);
        }
        return searchCode;
    }

    public Integer validateByNumber(Integer number){
        if (number == null){
            throw new GenericOutputException(MESSAGE_INVALID_NUMBER);
        }

        Integer searchNumber = partyRepository.findByNumber(number).orElse(null);
        if (searchNumber != null){
            throw new GenericOutputException(MESSAGE_NUMBER_NOT_FOUND);
        }

        return searchNumber;
    }

    public PartyOutput update(Long id, PartyInput partyInput) {
        if (id == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }
        validateInput(partyInput, true);

        Party party = partyRepository.findById(id).orElse(null);
        if (party == null){
            throw new GenericOutputException(MESSAGE_PARTY_NOT_FOUND);
        }

        try {
            party.setName(partyInput.getName());
            party.setNumber(partyInput.getNumber());
            //party.setId(partyInput.getId());
            party.setCode(partyInput.getCode());
        }catch (Exception e){
            System.out.println("Erro ao setar valores");
        }

        party = partyRepository.save(party);
        return modelMapper.map(party, PartyOutput.class);
    }

    public GenericOutput delete(Long id) {
        if (id == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }

        Party party = partyRepository.findById(id).orElse(null);
        if (party == null){
            throw new GenericOutputException(MESSAGE_PARTY_NOT_FOUND);
        }
        System.out.println("ID encontrado : " + party.getId());
        new GenericOutput("ID encontrado : " + party.getId().toString());

        partyRepository.delete(party);

        return new GenericOutput("Party deleted");
    }

    private void validateInput(PartyInput partyInput, boolean isUpdate){
        if (StringUtils.isBlank(partyInput.getCode())){
            throw new GenericOutputException("Invalid code");
        }
        if (StringUtils.isBlank(partyInput.getName())){
            throw new GenericOutputException("Invalid name");
        }
        /*
        expressões para matches
        ^ verifica se a expressão começa com
        (?i: inicia um grupo com case-insensitive
        [a-z]+ verifica palavras com as letras de a-z até encontrar um espaço (note que após o + tem um espaço)
        [a-z ]+) verifica palavras com as letras de a-z e espaços, ou seja pode conter mais de uma palavra com espaço até encontrar o final do grupo (grupo para case-insensitive)
        $ verifica se a string termina exatamente conforme a expressão
         */
        if (!partyInput.getName().matches("^(?i:[a-z]+ [a-z ]+)$")){
            throw new GenericOutputException("Need a last name");
        }

        if (partyInput.getName().length() < 5){
            throw new GenericOutputException("Invalid name. Min. 5 letters");
        }
        if (partyInput.getNumber().toString().length() < 2 || partyInput.getNumber().toString().length() > 2){
            throw new GenericOutputException("Invalid Number");
        }
        validateByNumber(partyInput.getNumber());
        validateByCode(partyInput.getCode());

    }

}
