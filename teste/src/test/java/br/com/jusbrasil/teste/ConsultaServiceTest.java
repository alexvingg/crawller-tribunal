package br.com.jusbrasil.teste;

import br.com.jusbrasil.teste.exception.ParametrosInvalidos;
import br.com.jusbrasil.teste.response.Processo;
import br.com.jusbrasil.teste.service.ConsultaService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConsultaServiceTest {

    @Autowired
    private ConsultaService consultaService;

    private List<String> processos = Arrays.asList(new String[]{"1002298-86.2015.8.26.0271",
            "1002072-50.2018.8.26.0021",
            "1004121-64.2018.8.26.0021",
            "1099673-47.2017.8.26.0100",
            "1041662-88.2018.8.26.0100",
            "0033107-36.2017.8.26.0100",
            "0033500-24.2018.8.26.0100",
            "0039522-98.2018.8.26.0100",
            "0040401-76.2016.8.26.0100",
            "0002417-48.2010.8.26.0236",
            "0005097-98.2013.8.26.0236",
            "0003566-11.2012.8.26.0236",
            "0006027-24.2010.8.26.0236",
            "0005618-14.2011.8.26.0236",
            "0007846-98.2007.8.26.0236",
            "0009366-59.2008.8.26.0236",
            "0006569-08.2011.8.26.0236",
            "0008811-37.2011.8.26.0236",
            "1001044-81.2018.8.26.0236",
            "1001029-20.2015.8.26.0236"});


    @Test
    public void consultaDocumentoValido() throws IOException {
        Processo processo = this.consultaService.consultaProcesso("1002298-86.2015.8.26.0271");
        Assert.assertEquals(processo.getDadosProcesso().get(0).getValor().trim(), "1002298-86.2015.8.26.0271");
    }


    @Test
    public void consultaDocumentosValido() throws IOException {

        processos.forEach(s -> {
            Processo processo = null;
            try {
                processo = this.consultaService.consultaProcesso(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Pattern pattern = Pattern.compile("(\\d{7}-\\d{2}.\\d{4}.\\d{1}.\\d{2}.\\d{4})");
            Matcher matcher = pattern.matcher(processo.getDadosProcesso().get(0).getValor().trim());
            matcher.find();
            Assert.assertEquals(matcher.group(), s);
        });
    }

    @Test
    public void consultaDocumentoNaoExistente() throws IOException {

        try {
            this.consultaService.consultaProcesso("1002298-86.2015.8.26.0272");
        }catch (ParametrosInvalidos e){
            Assert.assertEquals("Não existem informações disponíveis para os parâmetros informados.", e.getMessage());
        }
    }

    @Test
    public void consultaDocumentoInvalido() throws IOException {

        try {
            this.consultaService.consultaProcesso("1111");
        }catch (ParametrosInvalidos e){
            Assert.assertEquals("Número de processo inválido", e.getMessage());
        }
    }
}
