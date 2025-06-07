(ns nutri-app.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json])
  (:gen-class))

; -- Configuração dos dados do usuário ---------------------------------------------------------------------------------

(defn userRegistrado? []

  (let [url "http://localhost:3000/contem/user"

        response (http/get url {:headers {"Content-Type" "application/json"}})

        body (json/parse-string (:body response) true)

        vazio? (:vazio? body)]

    (not vazio?)

    )
  )

(defn salvar_dados_user [nome idade peso altura sexo]

  (let [dados {:nome nome
               :idade idade
               :peso peso
               :altura altura
               :sexo sexo}]

    (try

      (http/post "http://localhost:3000/registro/user"
                 {:body (json/encode dados)
                  :headers {"Content-Type" "application/json"}})

      (println "\n  Registro salvo com sucesso!\n")

      (catch Exception e

        (println "  Falha ao enviar registro de usuario:" (.getMessage e))

        )
      )
    )
  )

; -- Configuração dos dados dos alimentos ------------------------------------------------------------------------------

(defn buscar_alimento [alimento]

  (try

    (let [response (http/get "http://localhost:3000/buscar/alimento"
                             {:query-params {"alimento" alimento}
                              :as :json})]

      (:body response))

    (catch Exception e

      (str "\n  Erro ao buscar alimento:" (.getMessage e) "\n")

      )
    )
  )

(defn kcal_ajustado_alimento [gramas_usuario kcal_alimento]
  (float (/ (* gramas_usuario kcal_alimento) 100))
  )

(defn salvar_alimento [alimento quantidade kcal dataConsumo]

  (let [dados {:alimento alimento
               :quantidade quantidade
               :kcal kcal
               :dataConsumo dataConsumo}]

    (try

      (http/post "http://localhost:3000/registro/alimento"
                 {:body (json/encode dados)
                  :headers {"Content-Type" "application/json"}})

      (println "\n  Registro salvo com sucesso!\n")

      (catch Exception e

        (println "  Falha ao enviar registro de alimento:" (.getMessage e))

        )
      )
    )
  )

; -- Configuração dos dados das atividades físicas ---------------------------------------------------------------------

(defn buscar_atividade_fisica [atividade]

  (try

    (let [response (http/get "http://localhost:3000/buscar/atividade"
                             {:query-params {"atividade" atividade}
                              :as :json})]

      (:body response))

    (catch Exception e

      (str "\n  Erro ao buscar atividade: " (.getMessage e) "\n")

      )
    )
  )

(defn kcal_ajustado_exercicio [tempo kcal_exercicio]
  (float (* tempo kcal_exercicio))
  )


(defn salvar_atividade [atividade tempo kcal dataAtividade]

  (let [dados {:atividade atividade
               :tempo tempo
               :kcal kcal
               :dataAtividade dataAtividade}]

    (try

      (http/post "http://localhost:3000/registro/atividade"
                 {:body (json/encode dados)
                  :headers {"Content-Type" "application/json"}})

      (println "\n  Registro salvo com sucesso!\n")

      (catch Exception e

        (println "  Falha ao enviar registro de atividade:" (.getMessage e))

        )
      )
    )
  )

; -- Configuração dos dados do relatorio -------------------------------------------------------------------------------

(defn salvar_datas_relatorio [inicio fim]

  (let [dados {:inicio inicio
               :fim fim}]

    (try

      (http/post "http://localhost:3000/datas/relatorio"
                 {:body (json/encode dados)
                  :headers {"Content-Type" "application/json"}})

      (catch Exception e

        (println "  Falha ao enviar datas para o relatorio:" (.getMessage e))

        )
      )
    )
  )

(defn buscar_user []

  (try

    (let [response (http/get "http://localhost:3000/dados/user" {:as :json})]

      (:body response)

      )

    (catch Exception e

      (str "\n  Erro ao buscar usuario: " (.getMessage e) "\n")

      )
    )
  )

(defn buscar_alimentos []

  (try

    (let [response (http/get "http://localhost:3000/dados/alimento" {:as :json})]

      (:body response)

      )

    (catch Exception e

      (str "\n  Erro ao buscar usuario: " (.getMessage e) "\n")

      )
    )
  )

(defn buscar_ativ_fis []

  (try

    (let [response (http/get "http://localhost:3000/dados/ativ_fis" {:as :json})]

      (:body response)

      )

    (catch Exception e

      (str "\n  Erro ao buscar usuario: " (.getMessage e) "\n")

      )
    )
  )


(defn buscar_saldo []

  (try

    (let [response (http/get "http://localhost:3000/dados/calorias" {:as :string}) ; pegue como string
          dados     (json/decode (:body response) true)] ; parse JSON

      dados)

    (catch Exception e

      (println "\nErro ao buscar o saldo: " (.getMessage e))

      )
    )
  )


; -- Funções de apoio --------------------------------------------------------------------------------------------------

(defn formato_data [texto]

  (if (re-matches #"\d{2}/\d{2}/\d{4}" texto)

    true

    false

    )
  )


; -- Configurações dos menus -------------------------------------------------------------------------------------------


(defn menu_geral []

  (print (str
           "   ________________________________\n"
           "  |     Bem-Vindo ao NutriApp      |\n"
           "  |________________________________|\n"
           "   ----- Opcoes disponiveis ------\n\n"
           "  1 - Registrar dados do usuário\n"
           "  2 - Consultar dados do usuário\n"
           "  3 - Registrar refeicao\n"
           "  4 - Registrar atividade fisica\n"
           "  5 - Verificar extrato\n"
           "  6 - Verificar saldo\n"
           "  7 - Sair\n\n"
           "  Escolha uma opcao: "))

  (flush)

  )

(defn menu_dados_user []

  (if (= (userRegistrado?) true)

    (println "\n  Usuario já cadastrado!\n")

    (do

      (print (str "\n   ________________________________\n"
                  "  | NutriApp: Cadastro de usuario  |\n"
                  "  |________________________________|\n"
                  "  ------ Registro de usuario ------\n"))

      (print "\n  Informe seu nome: ")

      (flush)

      (let [nome (read-line)]

        (print "  Informe sua idade (Em anos): ")

        (flush)

        (let [idade (read-line)]

          (print "  Informe seu peso (Em kg): ")

          (flush)

          (let [peso (read-line)]

            (print "  Informe sua altura (Em cm): ")

            (flush)

            (let [altura (read-line)]

              (print "  Informe seu sexo (M - Masc / F - Fem): ")

              (flush)

              (let [sexo (read-line)]

                (if (or (= sexo "M") (= sexo "F"))

                  (salvar_dados_user nome (Integer/parseInt idade) (Integer/parseInt peso) (Integer/parseInt altura) sexo)

                  (print "\n  Opcao invalida... Tente novamente!\n")
                  )
                )
              )
            )
          )
        )
      )
    )
  )

(defn menu_consultar_user []

  (let [usuarios (buscar_user)
        usuario (first usuarios)]

    (if (not (userRegistrado?))

      (println "\n  Nenhum usuario cadastrado foi encontrado... Por favor, cadastre primeiro!\n")

      (do

        (println "\n  ==================== DADOS DO USUÁRIO REGISTRADO ====================")
        (println (str "  Nome: "   (:nome usuario)))
        (println (str "  Idade: "  (:idade usuario) " anos"))
        (println (str "  Peso: "   (:peso usuario) " kg"))
        (println (str "  Altura: " (:altura usuario) " cm"))
        (println (str "  Sexo: "   (if (= (:sexo usuario) "M") "Masculino" "Feminino")))
        (println "  =================================\n")

        )
      )
    )
  )

(defn menu_alimentos []

  (if (not (userRegistrado?))

    (println "\n  Nenhum usuario cadastrado foi encontrado... Por favor, cadastre primeiro!\n")

    (do

      (println

        (str "\n   ________________________________\n"
             "  | NutriApp: Registro alimentacao |\n"
             "  |________________________________|\n"
             "   ------ Registrar Alimento ------\n")
        )

      (print "  Digite o nome do alimento consumido (ou 0 para voltar): ")

      (flush)

      (let [nome_alimento (read-line)]

        (if (= nome_alimento "0")

          (println "\n  Voltando ao menu anterior...\n")

          (let [alimentos (buscar_alimento nome_alimento)]

            (if (or (nil? alimentos) (empty? alimentos))

              (println "\n  Nenhum alimento encontrado para esse nome!\n")

              (do

                (print "\n")

                (run! (fn [[idx alimento]]

                        (println (str "  " (inc idx) " - " (:descricao alimento))))

                      (map-indexed vector alimentos))

                (print "\n  Digite o número do alimento escolhido (ou 0 para voltar): ")

                (flush)

                (let [opcao (read-line)]

                  (if (= opcao "0")

                    (println "\n  Voltando ao menu anterior...\n")

                    (let [indice (dec (Integer/parseInt opcao))
                          alimento_escolhido (nth alimentos indice {:descricao "Desconhecido" :energia-kcal "N/A"})]

                      (if (or (< indice 0) (> indice 9))

                        (print "\n  Opção fora do escopo!\n\n")

                        (do

                          (print "  Informe a quantidade do alimento escolhido (em gramas): ")

                          (flush)

                          (let [quantidade (read-line)]

                            (print "  Informe a data do consumo (DD/MM/AAAA): ")

                            (flush)

                            (let [data (read-line)]

                              (if (formato_data data)

                                (salvar_alimento

                                  (:descricao alimento_escolhido)
                                  (Integer/parseInt quantidade)
                                  (kcal_ajustado_alimento (Integer/parseInt quantidade) (:energia-kcal alimento_escolhido))
                                  data

                                  )

                                (println "\n  Formato da data está invalido... Tente novamente!\n")

                                )
                              )
                            )

                          )

                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    )
  )

(defn menu_ativ_fis []

  (if (not (userRegistrado?))

    (println "\n  Nenhum usuário cadastrado foi encontrado... Por favor, cadastre primeiro!\n")

    (do

      (println

        (str "\n   ________________________________\n"
             "  |  NutriApp: Registro Ativ. Fis  |\n"
             "  |________________________________|\n"
             "   ----- Registrar Ativ. Fis -----\n"))

      (print "  Digite o nome da atividade física realizada (ou 0 para voltar): ")

      (flush)

      (let [nome_ativ (read-line)]

        (if (= nome_ativ "0")

          (println "\n  Voltando ao menu anterior...\n")

          (let [ativs (buscar_atividade_fisica nome_ativ)]

            (if (or (nil? ativs) (empty? ativs))

              (println "\n  Nenhuma atividade encontrada com esse nome!\n")

              (do

                (print "\n")

                (run! (fn [[idx ativ]]

                        (println (str "  " (inc idx) " - " (:descricao ativ))))

                      (map-indexed vector ativs))

                (print "\n  Digite o número da atividade realizada (ou 0 para voltar): ")

                (flush)

                (let [opcao (read-line)]

                  (if (= opcao "0")

                    (println "\n  Voltando ao menu anterior...\n")

                    (let [indice (dec (Integer/parseInt opcao))]

                      (if (or (< indice 0) (> indice 9))

                        (print "\n  Opção fora do escopo!\n\n")

                        (do

                          (let [atividade_escolhida (nth ativs indice)]

                            (print "  Informe a duração da atividade (em minutos): ")

                            (flush)

                            (let [duracao (read-line)]

                              (print "  Informe a data da atividade (DD/MM/AAAA): ")

                              (flush)

                              (let [data (read-line)]

                                (if (formato_data data)

                                  (salvar_atividade (:descricao atividade_escolhida)
                                                    (Integer/parseInt duracao)
                                                    (kcal_ajustado_exercicio (Integer/parseInt duracao) (:energia-kcal atividade_escolhida))
                                                    data)

                                  (println "\n  Formato da data está invalido... Tente novamente!\n")

                                  )
                                )
                              )
                            )
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    )
  )

(defn menu_extrato []

  (if (not (userRegistrado?))

    (println "\n  Nenhum usuário cadastrado foi encontrado... Por favor, cadastre primeiro!\n")

    (do

      (print "\n  Informe a data inicial do periodo a ser avaliado: ")

      (flush)

      (let [inicio (read-line)]

        (if (formato_data inicio)

          (do

            (print "  Informe a data final do periodo a ser avaliado: ")

            (flush)

            (let [fim (read-line)]

              (if (formato_data fim)

                (do

                  (salvar_datas_relatorio inicio fim)

                  (let [                                    ;usuarios (buscar_user)
                        ;usuario (first usuarios)

                        alimentos (buscar_alimentos)

                        ativ_fis (buscar_ativ_fis)]

                    (println "\n  ============================================================")
                    (println "  ==================== EXTRATO DO USUÁRIO ====================")
                    (println "  ============================================================\n")

                    (if (empty? alimentos)

                      (do

                        (println "  ===== ALIMENTOS REGISTRADOS =====\n")

                        (println "  Nenhum dado registrado para o período especificado!\n")

                        (println "  =================================\n")

                        )

                      (do

                        (println "  ===== ALIMENTOS REGISTRADOS =====\n")

                        (mapv

                          (fn [alimento]

                            (println

                              (str "  Alimento: " (:alimento alimento)
                                   " | Quantidade: " (:quantidade alimento) " g"
                                   " (" (format "%.2f" (:kcal alimento)) " kcal)"
                                   " | Data de registro: " (:dataConsumo alimento)
                                   "\n"
                                   )
                              )
                            )

                          alimentos

                          )

                        (println "  =================================\n")

                        )
                      )


                    (if (empty? ativ_fis)

                      (do

                        (println "\n  ===== ATIVIDADES REGISTRADAS =====\n")
                        (println "  Nenhum dado registrado para o período especificado!\n")
                        (println "  =================================")
                        (println "  =====================================================================\n")
                        )



                      (do

                        (println "\n  ===== ATIVIDADES REGISTRADAS =====\n")

                        (mapv

                          (fn [ativ]

                            (println

                              (str "  Atividade: " (:atividade ativ)
                                   " | Tempo: " (:tempo ativ) " min"
                                   " (" (format "%.2f" (:kcal ativ)) " kcal)"
                                   " | Data de registro: " (:dataAtividade ativ)
                                   "\n"
                                   )
                              )
                            )

                          ativ_fis

                          )

                        (println "  =================================")
                        (println "  =====================================================================\n")

                        )
                      )
                    )
                  )

                (println "\n  Formato da data está invalido... Tente novamente!\n")

                )
              )
            )

          (println "\n  Formato da data está invalido... Tente novamente!\n")

          )
        )
      )
    )
  )


(defn menu_saldo []

  (if (not (userRegistrado?))

    (println "\n  Nenhum usuário cadastrado foi encontrado... Por favor, cadastre primeiro!\n")

    (do

      (print "\n  Informe a data inicial do periodo a ser avaliado: ")

      (flush)

      (let [inicio (read-line)]

        (if (formato_data inicio)

          (do

            (print "  Informe a data final do periodo a ser avaliado: ")

            (flush)

            (let [fim (read-line)]

              (if (formato_data fim)

                (do

                  (salvar_datas_relatorio inicio fim)

                  (let [saldo (buscar_saldo)]

                    (println "\n  ====================================================================")
                    (println "  ==================== RESUMO CALÓRICO DO PERÍODO ====================")
                    (println "  ====================================================================\n")

                    (println (str "  Total de calorias acumuladas: " (:calorias_consumidas saldo) " kcal"))
                    (println (str "  Total de calorias gastas:     " (:calorias_gastas saldo) " kcal"))
                    (println (str "  Saldo calórico final:         " (:saldo_calorico saldo) " kcal\n"))
                    (println "  =====================================================================\n")

                    )
                  )

                (println "\n  Formato da data está invalido... Tente novamente!\n")

                )



              )
            )

          (println "\n  Formato da data está invalido... Tente novamente!\n")

          )
        )
      )
    )
  )


(defn menu_acao [opcao]

  (cond

    (= opcao "1") (menu_dados_user)

    (= opcao "2") (menu_consultar_user)

    (= opcao "3") (menu_alimentos)

    (= opcao "4") (menu_ativ_fis)

    (= opcao "5") (menu_extrato)

    (= opcao "6") (menu_saldo)

    :else (println "\n  Opcao invalida... Tente novamente!\n")

    )
  )

(defn menu_recursivo []

  (menu_geral)

  (let [opcao (read-line)]

    (if (= opcao "7")

      (println "\n  Encerrando o programa...")

      (do

        (menu_acao opcao)

        (recur)

        )
      )
    )
  )

(defn -main []

  (menu_recursivo)

  )
