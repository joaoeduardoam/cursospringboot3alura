package med.voll.api.domain.consulta;


import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.validacoes.ValidadorAgendamentoDeConsulta;
import med.voll.api.domain.consulta.validacoes.ValidadorCancelamentoDeConsulta;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaDeConsultas {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private List<ValidadorAgendamentoDeConsulta> validadores;

    @Autowired
    private List<ValidadorCancelamentoDeConsulta> validadoresCancelamento;

    public DadosDetalhamentoConsulta agendar (DadosAgendamentoConsulta dados){

        if(!pacienteRepository.existsById(dados.idPaciente())){
            throw new ValidacaoException("ID do paciente informado não existe");
        }

        if (dados.idMedico()!= null && !medicoRepository.existsById(dados.idMedico())){
            throw new ValidacaoException("ID do médico informado não existe");
        }

        validadores.forEach(v -> v.validar(dados));

        var medico = escolherMedico(dados); //medicoRepository.findById(dados.idMedico()).get();
        if (medico == null){
            throw new ValidacaoException("Não existe médico disponível na data informada.");
        }


        var paciente = pacienteRepository.findById(dados.idPaciente()).get();
        var consulta = new Consulta(null, medico, paciente, dados.data(), null);

        consultaRepository.save(consulta);

        return new DadosDetalhamentoConsulta(consulta);

    }

    private Medico escolherMedico(DadosAgendamentoConsulta dados) {

        if(dados.idMedico() != null){
            return medicoRepository.getReferenceById(dados.idMedico());
        }

        if (dados.especialidade() == null){
            throw new ValidacaoException("Especialidade é obrigatória quando o médico não for informado!");
        }

        return medicoRepository.escolherMedicoAleatorioLivreNaData(dados.especialidade(), dados.data());

    }


    public void cancelar(DadosCancelamentoConsulta dados) {


        if(!consultaRepository.existsById(dados.idConsulta())){
            throw new ValidacaoException("ID da consulta informado não existe");
        }

        if(dados.motivoCancelamento() == null){
            throw new ValidacaoException("É necessário informar o motivo do cancelamento");
        }

        validadoresCancelamento.forEach(v -> v.validar(dados));


        var consulta = consultaRepository.getReferenceById(dados.idConsulta());

        if(consulta.getMotivoCancelamento() != null){
            throw new ValidacaoException("Esta consulta já está cancelada pelo motivo "+consulta.getMotivoCancelamento());
        }

        consulta.cancelar(dados.motivoCancelamento());

    }


}
