Ksroute Winthor
=============================
Este é o aplicativo integrador do ksroute com o sistema winthor (Em desenvolvimento), ele é responsavel por exportar os pedidos liberados para o sistema ksroute e importar as rotas no winthor.


Use como exemplo
-------------------
Este aplicativo pode servir de exemplo para a construção da integração para o seu sistema, por se tratar de uma abordagem muito simple, com pouca implementação e que está em conformidade com o [Enterprise Integration Patterns](http://www.eaipatterns.com/toc.html) do livro de
[Gregor Hohpe](http://www.amazon.com/exec/obidos/search-handle-url/105-9796798-8100401?%5Fencoding=UTF8&search-type=ss&index=books&field-author=Gregor%20Hohpe) e [Bobby Woolf](http://www.amazon.com/exec/obidos/search-handle-url/105-9796798-8100401?%5Fencoding=UTF8&search-type=ss&index=books&field-author=Bobby%20Woolf).

#### Frameworks

- SpringBoot: Facilita a criação de aplicativos autônomos baseados em spring que você pode só "clicar e executar".

- Liquibase: Permite que você desenvolva seu esquema de banco de dados ao longo do tempo. Em vez de escrever modificações de esquema em SQL puro, as migrações permitem que você use uma fácil DSL para descrever as alterações em suas tabelas.

- Apache Camel: Torna a integração nos projetos de software bastante produtiva. Basicamente com pouco código podemos fazer muitas coisas que precisariam de muitos códigos, bibliotecas externas, controle interno, etc.

#### Fluxo de Envio de pedidos

O aplicativo realiza uma consulta no banco de dados de tempos em tempos procurando por pedidos não consumidos, quando encontrados suas dependencias (cliente, regiao, filial, etc...) são criadas ou alteradas no ksroute e seus ids gravados no erp, entao por fim o pedido é gravado. Qualquer erro será mostrador no arquivo de log e o pedido com problema não será consumido até que o erro seja corrigido.
