package br.com.jusbrasil.teste.response;

import java.util.ArrayList;
import java.util.List;

public class Processo {

    private List<DadosProcesso> dadosProcesso = new ArrayList<>();

    private List<DadosProcesso> partesEnvolvidas = new ArrayList<>();

    private List<DadosProcesso> movimentacoesProcesso = new ArrayList<>();

    public List<DadosProcesso> getDadosProcesso() {
        return dadosProcesso;
    }

    public void setDadosProcesso(List<DadosProcesso> dadosProcesso) {
        this.dadosProcesso = dadosProcesso;
    }

    public List<DadosProcesso> getPartesEnvolvidas() {
        return partesEnvolvidas;
    }

    public void setPartesEnvolvidas(List<DadosProcesso> partesEnvolvidas) {
        this.partesEnvolvidas = partesEnvolvidas;
    }

    public List<DadosProcesso> getMovimentacoesProcesso() {
        return movimentacoesProcesso;
    }

    public void setMovimentacoesProcesso(List<DadosProcesso> movimentacoesProcesso) {
        this.movimentacoesProcesso = movimentacoesProcesso;
    }
}
