(ns nutri-app.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.string :as str])
  (:gen-class))

(def ativ_fis ["Corrida leve" "Corrida intensa" "Natacao" "Andar de bicicleta" "Musculacao"])

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

    (let [response (http/get "http://localhost:3000/usda"
                             {:query-params {"alimento" alimento}
                              :as :json})]

      (:body response))

    (catch Exception e

      (println "Erro ao buscar alimento:" (.getMessage e))

      nil)))

(defn kcal-ajustado [gramas-usuario kcal-usda]
  (/ (* gramas-usuario kcal-usda) 100)
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

(defn salvar_atividade [atividade tempo dataAtividade]

  (let [dados {:atividade atividade
               :tempo tempo
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



(defn menu_geral []

  (print (str
           "   ________________________________\n"
           "  |     Bem-Vindo ao NutriApp      |\n"
           "  |________________________________|\n"
           "   ----- Opcoes disponiveis ------\n\n"
           "  1 - Registrar dados do usuário\n"
           "  2 - Registrar refeicao\n"
           "  3 - Registrar atividade fisica\n"
           "  4 - Sair\n\n"
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

                  (salvar_dados_user nome idade peso altura sexo)

                  (print "\n  Opcao invalida... Tente novamente!\n\n")
                  )
                )
              )
            )
          )
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
             "   ------ Registrar alimento ------\n"))

      (print "  Digite o nome do alimento consumido (ou 0 para voltar): ")

      (flush)

      (let [nome-alimento (read-line)]

        (if (= nome-alimento "0")

          (println "\n  Voltando ao menu anterior...\n")

          (let [alimentos (buscar_alimento nome-alimento)]

            (if (or (nil? alimentos) (empty? alimentos))

              (println "Nenhum alimento encontrado para esse nome!")

              (do

                (doseq [[idx alimento] (map-indexed vector alimentos)]

                  (println (str "\n  " (inc idx) " - " (:descricao alimento))))

                (print "\n  Digite o número do alimento escolhido (ou 0 para voltar): ")

                (flush)

                (let [opcao (read-line)]

                  (if (= opcao "0")

                    (println "\n  Voltando ao menu anterior...\n")

                    (let [indice (dec (Integer/parseInt opcao))

                          alimento-escolhido (nth alimentos indice {:descricao "Desconhecido" :energia-kcal "N/A"})]

                      (print "  Informe a quantidade do alimento escolhido (em gramas): ")

                      (flush)

                      (let [quantidade (read-line)]

                        (print "  Informe a data do consumo (DD/MM/AAAA): ")

                        (flush)

                        (let [data (read-line)
                              ]

                          (salvar_alimento (:descricao alimento-escolhido) quantidade (str (kcal-ajustado (Integer/parseInt quantidade) (:energia-kcal alimento-escolhido))) data)
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

    (println "\n  Nenhum usuario cadastrado foi encontrado... Por favor, cadastre primeiro!\n")

    (do

      (print (str
               "\n   ________________________________\n"
               "  | NutriApp: Registro ativ. Fisi. |\n"
               "  |________________________________|\n"
               "   ----- Opcoes disponiveis ------\n\n"
               (str/join "\n" (map-indexed (fn [i a] (str "  " (inc i) " - " a)) ativ_fis))
               "\n\n  0 - Voltar ao menu anterior\n\n"
               "  Escolha uma opcao: "))

      (flush)

      (let

        [opcao_ativ (read-line)]

        (cond

          (= opcao_ativ "0") (println "\n  Voltando ao menu anterior...\n")

          (> (Integer/parseInt opcao_ativ) (count ativ_fis)) (println "\n  Opcao invalida... Tente novamente!\n")

          :else

          (do

            (print "  Informe a duracao da atividade fisica (Em minutos): ")

            (flush)

            (let

              [tempo_ativ (read-line)
               nome_atividade (nth ativ_fis (dec (Integer/parseInt opcao_ativ)))]

              (print  "  Informe a data da realização da atividade fisica (DD/MM/AAAA): ")

              (flush)

              (let [dataAtividade (read-line)]

                (salvar_atividade nome_atividade tempo_ativ dataAtividade)

                )
              )
            )
          )
        )
      )
    )
  )

(defn menu-acao [opcao]

  (cond

    (= opcao "1") (menu_dados_user)

    (= opcao "2") (menu_alimentos)

    (= opcao "3") (menu_ativ_fis)

    :else (println "\nOpcao invalida... Tente novamente!")

    )
  )

(defn menu-recursivo []

  (menu_geral)

  (let [opcao (read-line)]

    (if (= opcao "4")

      (println "\n  Encerrando o programa...")

      (do

        (menu-acao opcao)

        (recur)

        )
      )
    )
  )

(defn -main []

  (menu-recursivo)

  )
