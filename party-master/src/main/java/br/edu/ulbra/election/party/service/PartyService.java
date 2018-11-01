package br.edu.ulbra.election.party.service;

//import br.edu.ulbra.election.party.exception.GenericOutputException;
import br.edu.ulbra.election.party.input.v1.CandidateInput;
import br.edu.ulbra.election.party.model.Candidate;
import br.edu.ulbra.election.party.output.v1.CandidateOutput;
import br.edu.ulbra.election.party.output.v1.GenericOutput;
import br.edu.ulbra.election.party.repository.CandidateRepository;
import org.apache.commons.lang.StringUtils;
import org.party.ModelMapper;
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
    private static final String MESSAGE_VOTER_NOT_FOUND = "Voter not found";

    @Autowired
    public PartyService(PartyRepository partyRepository, ModelMapper modelMapper){

        this.PartyRepository = partyRepository;
        this.modelMapper = modelMapper;

    }

    public List<PartyOutput> getAll(){
        Type voterOutputListType = new TypeToken<List<PartyOutput>>(){}.getType();
        return modelMapper.map(partyRepository.findAll(), voterOutputListType);
    }

    public PartyOutput create(PartyInput partyInput) {
        validateInput(partyInput, false);
        Party party = modelMapper.map(partyInput, Party.class);
        party = partyRepository.save(party);
        return modelMapper.map(party, PartyOutput.class);
    }

    public PartyOutput getById(Long partyId){
        if (candidateId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }

        Party party = partyRepository.findById(partyId).orElse(null);
        if (party == null){
            throw new GenericOutputException(MESSAGE_VOTER_NOT_FOUND);
        }

        return modelMapper.map(party, PartyOutput.class);
    }

    public PartyOutput update(Long partyId, PartyInput partyInput) {
        if (partyId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }
        validateInput(partyInput, true);

        Party party = partyRepository.findById(partyId).orElse(null);
        if (party == null){
            throw new GenericOutputException(MESSAGE_VOTER_NOT_FOUND);
        }

        //candidate.setEmail(voterInput.getEmail());
        try {
            party.setCode(partyInput.getCode());
            party.setName(partyInput.getName());
            party.setNumber(partyInput.getNumber());
        }catch (Exception e){
            System.out.println("Erro ao setar valores");
        }

        party = partyRepository.save(party);
        return modelMapper.map(party, PartyOutput.class);
    }

    public GenericOutput delete(Long partyId) {
        if (partyId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }

        Party party = partyRepository.findById(partyId).orElse(null);
        if (party == null){
            throw new GenericOutputException(MESSAGE_VOTER_NOT_FOUND);
        }

        partyRepository.delete(party);

        return new GenericOutput("Voter deleted");
    }
}
