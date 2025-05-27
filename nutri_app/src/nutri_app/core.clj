(ns nutri-app.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.string :as str])
  (:gen-class))

(def ativ_fis ["Corrida leve" "Corrida intensa" "Natacao" "Andar de bicicleta" "Musculacao"])

(defn salvar_dados_user [nome idade peso]

  (let [dados {:nome nome
               :idade idade
               :peso peso}]

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

(defn salvar_alimento [alimento quantidade]

  (let [dados {:alimento alimento
            :quantidade quantidade}]

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

(defn salvar_atividade [atividade tempo]

  (let [dados {:atividade atividade :tempo tempo}]

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

  (print "  Informe seu nome: ")

  (flush)

  (let [nome (read)]

    (print "  Informe sua idade (Em anos): ")

    (flush)

    (let [idade (read)]

      (print "  Informe seu peso (Em kg): ")

      (flush)

      (let [peso (read)]

        (salvar_dados_user nome idade peso)

        )
      )
    )
  )



(defn menu_alimentos []

  (print (str
           "\n   ________________________________\n"
           "  | NutriApp: Registro alimentacao |\n"
           "  |________________________________|\n"
           "   ----- Opcoes disponiveis ------\n\n"
           "  0 - Para voltar ao menu anterior\n\n"
           "  Digite o nome do alimento consumido: "))

  (flush)

  (let [opcao_alimento (read)]

    (cond

      (= opcao_alimento 0) (println "\n  Voltando ao menu anterior...\n")

      :else

      (do

        (print "  Informe a quantidade do alimento escolhido (Em gramas): ")

        (flush)

        (let [quantidade_alimento (read)]

          (salvar_alimento opcao_alimento quantidade_alimento)

          )
        )
      )
    )
  )

(defn menu_ativ_fis []

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

    [opcao_ativ (read)]

    (cond

      (= opcao_ativ 0) (println "\n  Voltando ao menu anterior...\n")

      (> opcao_ativ (count ativ_fis)) (println "\n  Opcao invalida... Tente novamente!\n")

      :else

      (do

        (print "  Informe a duracao da atividade fisica (Em minutos): ")

        (flush)

        (let

          [tempo_ativ (read)
           nome-atividade (nth ativ_fis (dec opcao_ativ))]

          (salvar_atividade nome-atividade tempo_ativ)

          )
        )
      )
    )
  )

(defn menu-acao [opcao]

  (cond

    (= opcao 1) (menu_dados_user)

    (= opcao 2) (menu_alimentos)

    (= opcao 3) (menu_ativ_fis)

    :else (println "\nOpcao invalida... Tente novamente!")

    )
  )

(defn menu-recursivo []

  (menu_geral)

  (let [opcao (read)]

    (if (= opcao 4)

      (println "\n  Encerrando o programa...")

      (do

        (menu-acao opcao)

        (recur)

        )
      )
    )
  )

(defn -main []
  (menu-recursivo))
