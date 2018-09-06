package br.com.jusbrasil.teste.controller;

import br.com.jusbrasil.teste.exception.ParametrosInvalidos;
import br.com.jusbrasil.teste.response.ErrorResponse;
import br.com.jusbrasil.teste.response.Processo;
import br.com.jusbrasil.teste.service.ConsultaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class ConsultaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaController.class);

    @Autowired
    private ConsultaService consultaService;

    @RequestMapping("/consulta")
    public ResponseEntity consulta(@RequestParam(value="tipo") int tipo,
                                   @RequestParam(value="numeroProcesso")String numeroProcesso) {

        try {
            Processo processo = this.consultaService.consultaProcesso(numeroProcesso);
            return new ResponseEntity(processo, HttpStatus.OK);
        }catch (ParametrosInvalidos e){
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setLabel(e.getMessage());
            return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
        }catch (Exception ex) {
            LOGGER.error("consulta processo {}", numeroProcesso, ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
