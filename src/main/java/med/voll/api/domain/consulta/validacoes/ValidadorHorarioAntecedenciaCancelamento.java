package med.voll.api.domain.consulta.validacoes;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DadosCancelamentoConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;


@Component
public class ValidadorHorarioAntecedenciaCancelamento implements ValidadorCancelamentoDeConsulta{


    @Autowired
    private ConsultaRepository repository;


    public void validar(DadosCancelamentoConsulta dados){

        System.out.println("Validador de cancelamento!");
        var agora = LocalDateTime.now();

        var consulta = repository.getReferenceById(dados.idConsulta());

        var diferencaEmHoras = Duration.between(agora, consulta.getData()).toHours();

        if (diferencaEmHoras < 24){
            throw new ValidacaoException("Consulta só pode ser cancelada com no mínimo 24 horas de antecedência!");
        }


    }

}
