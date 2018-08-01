Ksroute Csv
=============================
Este é o aplicativo integrador do ksroute por arquivos (Em desenvolvimento), ele é responsavel por exportar os pedidos dos arquivos csv para o sistema ksroute e disponibilizar os arquivos com as rotas já processadas.

#### Fluxo de Envio de pedidos

O aplicativo processa os arquivos csv dentro da pasta `/orders` cada linha do arquivo deve ser um pedido com suas dependencias (cliente, regiao, filial, etc...) depois de processados os arquivos serão enviados para a pasta `/done`. O formato do arquivo deve ser um arquivo texto sem titulos organizados pela posição das colunas e delimitado por `;` usando o formato pt-BR para datas e numeros, o formato é o padrão exportado pelo excel. A posição e o tipo dos dados podem ser encontrados [aqui](https://github.com/lazaronixon/ksroute-csv/blob/master/src/main/java/com/heuristica/ksroutewinthor/models/order). Qualquer erro será mostrador no arquivo de log e o pedido com problema não será consumido até que o erro seja corrigido.
