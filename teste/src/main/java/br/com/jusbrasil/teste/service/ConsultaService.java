package br.com.jusbrasil.teste.service;

import br.com.jusbrasil.teste.exception.ParametrosInvalidos;
import br.com.jusbrasil.teste.response.DadosProcesso;
import br.com.jusbrasil.teste.response.Processo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ConsultaService {

    private static final String URL_TJSP = "https://esaj.tjsp.jus.br/cpopg/search.do;jsessionid=32E57541FC6" +
            "58DB290350129234B752E.cpopg4?conversationId=&dadosConsulta.localPesquisa.cdLocal=-1&cbP" +
            "esquisa=NUMPROC&dadosConsulta.tipoNuProcesso=UNIFICADO&numeroDigitoAnoUnificado={0}" +
            "&foroNumeroUnificado={1}" +
            "&dadosConsulta.valorConsultaNuUnificado={2}&dadosConsulta.valorConsulta=&uuidCaptcha=";


    private static final String URL_TJMS = "https://www.tjms.jus.br/cpopg5/search.do?conversationId=&dadosCo" +
            "nsulta.localPesquisa.cdLocal=-1&cbPesquisa=NUMPROC&dadosConsulta.tipoNuProcesso=UNIFICADO&numer" +
            "oDigitoAnoUnificado={0}&foroNumeroUnificado={1}&dadosConsulta.valorConsul" +
            "taNuUnificado={2}&dadosConsulta.valorConsulta=&uuidCaptcha=";

    private static final String MSG_SEM_INFO = "Não existem informações disponíveis para os parâmetros informados.";

    public Processo consultaProcesso(int tipo , String numeroProcesso) throws IOException {

        if(validaNumeroProcesso(numeroProcesso)){
            throw new ParametrosInvalidos("Número de processo inválido");
        }

        if(tipo < 1 && tipo > 2){
            throw new ParametrosInvalidos("Tribunal inválido");
        }

        String url;

        if(tipo == 1){
            url = MessageFormat.format(URL_TJSP ,numeroProcesso.substring(0, 15),
                    numeroProcesso.substring(numeroProcesso.length() -4,
                            numeroProcesso.length()), numeroProcesso);
        }else{
            url = MessageFormat.format(URL_TJMS ,numeroProcesso.substring(0, 15),
                    numeroProcesso.substring(numeroProcesso.length() -4,
                            numeroProcesso.length()), numeroProcesso);
        }

        Processo processo = new Processo();
        Document document = Jsoup.connect(url).get();

        if(document.getElementsContainingText(MSG_SEM_INFO).size() > 1){
            throw new ParametrosInvalidos(MSG_SEM_INFO);
        };

        processo.setDadosProcesso(this.criarDadosProcesso(document));
        processo.setPartesEnvolvidas(this.criarPartesEnvolvidas(document));
        processo.setMovimentacoesProcesso(this.criarMovimentacoes(document));

        return processo;
    }

    private boolean validaNumeroProcesso(String numeroProcesso) {
        Pattern pattern = Pattern.compile("(\\d{7}-\\d{2}.\\d{4}.\\d{1}.\\d{2}.\\d{4})");
        return !pattern.matcher(numeroProcesso).matches();
    }

    private List<DadosProcesso> criarDadosProcesso(Document document) {
        List<DadosProcesso> dadosProcessos = new ArrayList<>();
        if(!document.body().getElementsByAttributeValue("class", "secaoFormBody").isEmpty() &&
                document.body().getElementsByAttributeValue("class", "secaoFormBody").size() > 1){
            Element elementDocument = document.body().getElementsByAttributeValue("class", "secaoFormBody").get(1).child(0);
            List<Element> elements = elementDocument.getElementsByTag("tr");
            elements.stream().forEach(element -> {
                if(!element.getElementsByTag("label").isEmpty()){
                    DadosProcesso dadosProcesso = new DadosProcesso();
                    dadosProcesso.setChave(element.getElementsByTag(
                            "label").get(0).childNode(0).outerHtml().trim()
                            .replaceAll("&nbsp;", ""));
                    StringBuffer valor = new StringBuffer();
                    element.getElementsByTag("span").forEach(element1 -> {
                        valor.append(element1.childNode(0).outerHtml());
                    });
                    dadosProcesso.setValor(valor.toString().replaceAll("&nbsp;", ""));
                    dadosProcessos.add(dadosProcesso);
                }else if(!element.getElementsContainingText("Área:").isEmpty() &&
                        !element.getElementsByTag("tr").isEmpty() &&
                        element.getElementsByTag("tr").size() > 1){
                    DadosProcesso dadosProcesso = new DadosProcesso();
                    dadosProcesso.setChave(element.getElementsByTag("tr").get(1)
                            .childNode(1).childNode(1).childNode(0).outerHtml().trim()
                            .replaceAll("&nbsp;", ""));
                    dadosProcesso.setValor(element.getElementsByTag("tr").get(1)
                            .childNode(1).childNode(2).outerHtml().replaceAll("&nbsp;", ""));
                    dadosProcessos.add(dadosProcesso);
                }
            });
        }

        return dadosProcessos;
    }

    private List<DadosProcesso> criarPartesEnvolvidas(Document document) {
        List<DadosProcesso> partesEnvolvidas = new ArrayList<>();

        document.body().getElementById("tablePartesPrincipais").getElementsByTag("td").forEach(element -> {
            List<Node> childNodeList = element.childNodes();

            childNodeList.forEach(node -> {
                DadosProcesso parteProcesso = new DadosProcesso();;
                if(node instanceof Element){
                    Elements elements = ((Element) node).getElementsByTag("span");
                    elements.forEach(element1 -> {
                        element1.getElementsByTag("span").forEach(element2 -> {
                            element2.childNodes().forEach(node1 -> {
                                if(node1 instanceof TextNode){
                                    if(!node1.outerHtml().trim().isEmpty()){
                                        parteProcesso.setChave(node1.outerHtml().trim()
                                                .replaceAll("&nbsp;", ""));
                                        partesEnvolvidas.add(parteProcesso);
                                    }
                                }
                            });
                        });
                    });
                }else if(node instanceof TextNode){
                    if(!node.outerHtml().trim().isEmpty()){
                        partesEnvolvidas.get(partesEnvolvidas.size() - 1).setValor(node.outerHtml()
                                .replaceAll("&nbsp;", ""));
                    }
                }
            });
        });
        return partesEnvolvidas;
    }

    private List<DadosProcesso> criarMovimentacoes(Document document) {
        List<DadosProcesso> movimentacoesProcesso = new ArrayList<>();
//        document.body().getElementById("tabelaUltimasMovimentacoes").childNodes().forEach(node -> {
//            this.verificaValorTexto(node, movimentacoesProcesso);
//        });

        document.body().getElementById("tabelaTodasMovimentacoes").childNodes().forEach(node -> {
            this.verificaValorTexto(node, movimentacoesProcesso);
        });

        return movimentacoesProcesso;
    }

    private List<DadosProcesso> verificaValorTexto(Node node2, List<DadosProcesso> movimentacoesProcesso) {
        node2.childNodes().forEach(node -> {
            if(node instanceof Element){
                node.childNodes().forEach(node1 -> {
                    if(node1 instanceof Element){
                        this.verificaValorTexto(node1, movimentacoesProcesso);
                    }else if(node1 instanceof TextNode){
                        if(!node1.outerHtml().trim().isEmpty()){
                            SimpleDateFormat dataFormatada = new SimpleDateFormat("dd/MM/yyyy");
                            try{
                                dataFormatada.parse(node1.outerHtml().trim());
                                DadosProcesso dadosProcesso = new DadosProcesso();
                                dadosProcesso.setChave(node1.outerHtml().trim());
                                movimentacoesProcesso.add(dadosProcesso);
                            }catch(ParseException e) {
                                StringBuffer sb = new StringBuffer();
                                sb.append(movimentacoesProcesso.get(movimentacoesProcesso.size() -1).getValor());
                                sb.append(node1.outerHtml());
                                movimentacoesProcesso.get(movimentacoesProcesso.size() -1).setValor(sb.toString());
                            }
                        }
                    }
                });
            }else if(node instanceof TextNode){
                if(!node.outerHtml().trim().isEmpty()){
                    SimpleDateFormat dataFormatada = new SimpleDateFormat("dd/MM/yyyy");
                    try{
                        dataFormatada.parse(node.outerHtml().trim());
                        DadosProcesso dadosProcesso = new DadosProcesso();
                        dadosProcesso.setChave(node.outerHtml().trim());
                        movimentacoesProcesso.add(dadosProcesso);
                    }catch(ParseException e) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(movimentacoesProcesso.get(movimentacoesProcesso.size() -1).getValor());
                        sb.append(node.outerHtml());
                        movimentacoesProcesso.get(movimentacoesProcesso.size() -1).setValor(sb.toString());
                    }
                }
            }
        });
        return movimentacoesProcesso;
    }


}