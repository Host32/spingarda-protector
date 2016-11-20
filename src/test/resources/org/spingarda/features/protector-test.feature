Feature: Protector

  Scenario Outline: A pagina so tem comentarios spamados por bots

    Given a pagina "<page>" estiver sob ataque

    And a pagina tiver "<postsNumber>" postagens

    And a pagina recebeu "<commentsByPost>" comentarios

    And os comentarios foram de "<usersCount>" usuarios diferentes

    When o administrador comecar a configurar o sistema

    And a tolerancia de comentarios for "<commentsNumber>"

    And a tolerancia de posts comentados for "<postsCommentedNumber>"

    And a tolerancia de comentarios por posts for "<commentsByPostTolerance>"

    And a tolerancia de variacao do intervalo de publicacao for "<lowerCommentInterval>" milisegundos com um taxa de "<lowerCommentIntervalRate>"% de diferenca

    And a tolerancia do coeficiente de variacao de "<commentIntervalVariation>"

    And as palavras bloqueadas forem "<blockedWords>"

    And os usuarios bloqueados forem "<bannedUsers>"

    And o admnistrador gostaria de "<hideOrDel>" os comentarios

    And o administrador terminar de configurar o sistema

    And o administrador iniciar o protetor

    Then o log do sistema deve ter reportado "<hidedOrDeleted>" de "<totalCommentsBlocked>" comentarios

    Examples:

      | page                | postsNumber | commentsByPost | usersCount | commentsNumber | postsCommentedNumber | commentsByPostTolerance | lowerCommentInterval | lowerCommentIntervalRate | commentIntervalVariation | blockedWords | bannedUsers | hideOrDel | hidedOrDeleted | totalCommentsBlocked |
      | spingardacompolvora | 20          | 200            | 4          | 1000           | null                 | null                    | null                 | null                     | null                     | null         | null        | hide      | HIDE           | 4000                 |
      | paginaqualquer      | 10          | 100            | 5          | null           | 10                   | null                    | null                 | null                     | null                     | null         | null        | del       | DELETE         | 1000                 |
      | outra pagina        | 20          | 100            | 4          | null           | null                 | 20                      | null                 | null                     | null                     | null         | null        | debug     | DEBUG          | 2000                 |