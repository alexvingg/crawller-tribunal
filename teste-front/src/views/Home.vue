<template>
  <v-container fluid grid-list-md>
    <v-layout row wrap>
      <v-form ref="form"  v-model="valid" lazy-validation>
        <v-layout row wrap>
          <v-flex sm12>
            <h1>Buscar</h1>
          </v-flex>
          <v-flex sm12>
            <span>Selecione um Tribunal para listar os processos ou buscar
              pelo número unificado</span>
          </v-flex>
        </v-layout>
        <v-layout row wrap>
          <v-flex sm5 >
            <v-select
              :items="items"
              item-text="texto"
              item-value="valor"
              label="Tribunal"
              v-model="tribunal"
              :rules="tribunalRegra"
              required
            ></v-select>
          </v-flex>
          <v-flex sm5>
            <v-text-field
              :mask="mask"
              return-masked-value
              label="Número Processo"
              v-model="numeroProcesso"
              :rules="numeroProcessoRegra"
              required
            ></v-text-field>
          </v-flex>
          <v-flex sm2 >
            <v-btn @click="submit" color="primary" dark>Pesquisar</v-btn>
          </v-flex>
        </v-layout>
      </v-form>
    </v-layout>
    <v-layout row wrap v-if="data && data.dadosProcesso && data.dadosProcesso.length > 0">
      <v-flex md8 sm12>
        <v-card>
          <v-card-title primary-title>
            <h2>Movimentações</h2>
          </v-card-title>

          <v-expansion-panel>
            <v-expansion-panel-content
              v-for="(item,i) in data.movimentacoesProcesso"
              :key="i">
              <div slot="header"><b>{{item.chave}}</b></div>
              <v-card>
                <v-card-text>{{item.valor}}</v-card-text>
              </v-card>
            </v-expansion-panel-content>
          </v-expansion-panel>

        </v-card>
      </v-flex>

      <v-flex md4 sm12>
        <v-card>
          <v-card-title primary-title>
            <h2>Dados do Processo</h2>
            <div>
              <div v-bind:key="index" wrap v-for="(item, index) in data.dadosProcesso">
                <b>{{ item.chave }}</b> {{ item.valor }} <br/>
              </div>
            </div>
          </v-card-title>
        </v-card>
        <br/>
        <v-card>
          <v-card-title primary-title>
            <h2>Partes Envolvidas</h2>
            <div>
              <div v-bind:key="index" wrap v-for="(item, index) in data.partesEnvolvidas">
                <b>{{ item.chave }}</b> {{ item.valor }} <br/>
              </div>
            </div>
          </v-card-title>
        </v-card>
      </v-flex>
    </v-layout>

    <v-snackbar
      v-model="snackbar"
      :color="'error'"
      :right="true"
      :top="true"
      :multi-line="true"
      :timeout="6000"
    >
      {{ text }}
      <v-btn
        dark
        flat
        @click="snackbar = false"
      >
        Fechar
      </v-btn>
    </v-snackbar>

  </v-container>

</template>

<script>
import axios from 'axios';

export default {
  data: () => ({
    tribunal: '',
    snackbar: false,
    text: '',
    numeroProcesso: '',
    data: [],
    items: [{ texto: 'TJSP', valor: 1 }, { texto: 'TJMS', valor: 2 }],
    valid: true,
    mask: '#######-##.####.#.##.####',
    numeroProcessoRegra: [
      v => !!v || 'Número do processo é obrigatório',
      v => (v && v.length > 24) || 'Número do processo inválido',
    ],
    tribunalRegra: [
      v => !!v || 'Tribunal é obrigatório',
    ],
  }),
  methods: {
    submit() {
      this.data = [];
      if (this.$refs.form.validate()) {
        axios.get(`http://localhost:8080/api/consulta?tipo=${this.tribunal}
        &numeroProcesso=${this.numeroProcesso}`).then((res) => {
          this.data = res.data;
        }).catch((error) => {
          this.snackbar = true;
          if (error.response && error.response.data.label) {
            this.text = error.response.data.label;
          } else {
            this.text = 'Sem conexão';
          }
        });
      }
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
