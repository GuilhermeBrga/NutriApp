; ========================= Opção com a lista ==========================

;(ns nutri-app.core
;  (:gen-class))
;
;(def alimentos ["Pao" "Ovo" "Cafe" "Bolo" "Arroz" "Macarrao" "Feijao" "Batata Cozida" "Frango" "Carne"])
;
;(def ativ_fis ["Corrida leve" "Corrida intensa" "Natacao" "Andar de bicicleta" "Musculacao"])
;
;(def alimentos_user (atom []))
;
;(def ativ_user (atom []))
;
;(defn menu_geral []
;
;  (let [header (str "   ________________________________\n"
;                    "  |     Bem-Vindo ao NutriApp      |\n"
;                    "  |________________________________|\n"
;                    "   ----- Opcoes disponiveis ------\n\n")
;
;        body (str "  1 - Registrar alimentacao\n"
;                  "  2 - Registrar atividade fisica\n"
;                  "  3 - Gerar relatorio\n"
;                  "  4 - Sair\n\n"
;                  "  Escolha uma opcao: ")]
;
;    (print (str header body))
;
;    )
;  )
;
;
;(defn menu_alimentos []
;
;  (let [header (str "\n   ________________________________\n"
;                    "  | NutriApp: Registro alimentacao |\n"
;                    "  |________________________________|\n"
;                    "   ----- Opcoes disponiveis ------\n\n")
;
;        body (clojure.string/join "\n" (map-indexed (fn [i alimento] (str "  " (inc i) " - " alimento)) alimentos))
;
;        footer (str "\n\n  0 - Voltar ao menu anterior\n\n  Escolha uma opcao: ")]
;
;    (print (str header body footer))
;
;    (flush)
;
;    (let [opcao_alimento (read)]
;
;      (cond (= opcao_alimento 0) (println "\n  Voltando ao menu anterior...\n")
;
;            (> opcao_alimento (count alimentos)) (println "\n  Opcao invalida... Tente novamente!\n")
;
;            :else
;
;            (do
;              (print "  Informe a quantidade do alimento escolhido em gramas ou ml: ")
;
;              (flush)
;
;              (let [quantidade_alimento (read)]
;
;                (swap! alimentos_user conj [opcao_alimento quantidade_alimento])
;
;                (println "\n  Registro salvo com sucesso!\n")
;
;                )
;              )
;            )
;      )
;    )
;  )
;
;
;(defn menu_ativ_fis []
;
;  (let [header (str "\n   ________________________________\n"
;                    "  | NutriApp: Registro ativ. Fisi. |\n"
;                    "  |________________________________|\n"
;                    "   ----- Opcoes disponiveis ------\n\n")
;
;        body (clojure.string/join "\n" (map-indexed (fn [i ativ_fis] (str "  " (inc i) " - " ativ_fis)) ativ_fis))
;
;        footer (str "\n\n  0 - Para voltar ao menu anterior\n\n  Escolha uma opcao: ")]
;
;    (print (str header body footer))
;
;    (flush)
;
;    (let [opcao_ativ (read)]
;
;      (cond (= opcao_ativ 0) (println "\n  Voltando ao menu anterior...\n")
;
;            (> opcao_ativ (count ativ_fis)) (println "\n  Opcao invalida... Tente novamente!\n")
;
;            :else
;
;            (do
;
;              (print "  Informe a duracao da atividade fisica em minutos: ")
;
;              (flush)
;
;              (let [tempo_ativ (read)]
;
;                (swap! ativ_user conj [opcao_ativ tempo_ativ])
;
;                (println "\n  Registro salvo com sucesso!\n")
;                )
;              )
;            )
;      )
;    )
;  )
;
;
;(defn gerar_relatorio []
;
;  (println @alimentos_user)
;
;  (println @ativ_user)
;
;  )
;
;
;(defn menu_acao [opcao]
;
;  (cond (= opcao 1) (menu_alimentos)
;
;        (= opcao 2) (menu_ativ_fis)
;
;        (= opcao 3) (gerar_relatorio)
;
;        :else
;        (str "\nOpcao invalida... Tente novamente!")
;
;        )
;  )
;
;
;(defn menu-recursivo []
;
;  (menu_geral)
;
;  (flush)
;
;  (let [opcao (read)]
;
;    (if (= opcao 4)
;
;      (println "\n  Encerrando o programa...")
;
;      (do
;
;        (menu_acao opcao)
;
;        (recur)
;
;        )
;      )
;    )
;  )
;
;(defn -main []
;  (menu-recursivo)
;  )


; ========================= Opção sem a lista ==========================


(ns nutri-app.core
  (:gen-class))

(def ativ_fis ["Corrida leve" "Corrida intensa" "Natacao" "Andar de bicicleta" "Musculacao"])

(def alimentos_user (atom []))

(def ativ_user (atom []))

(defn menu_geral []

  (let [header (str "   ________________________________\n"
                    "  |     Bem-Vindo ao NutriApp      |\n"
                    "  |________________________________|\n"
                    "   ----- Opcoes disponiveis ------\n\n")

        body (str "  1 - Registrar alimentacao\n"
                  "  2 - Registrar atividade fisica\n"
                  "  3 - Gerar relatorio\n"
                  "  4 - Sair\n\n"
                  "  Escolha uma opcao: ")]

    (print (str header body))

    )
  )


(defn menu_alimentos []

  (let [header (str "\n   ________________________________\n"
                    "  | NutriApp: Registro alimentacao |\n"
                    "  |________________________________|\n"
                    "   ----- Opcoes disponiveis ------\n\n")

        footer (str "  0 - Para voltar ao menu anterior\n\n  Digite o nome do seu alimento em inglês: ")]

    (print (str header footer))

    (flush)

    (let [opcao_alimento (read)]

      (cond (= opcao_alimento 0) (println "\n  Voltando ao menu anterior...\n")

            ; Verificar se o alimento foi encontrado na API

            :else

            (do
              (print "  Informe a quantidade do alimento escolhido em gramas ou ml: ")

              (flush)

              (let [quantidade_alimento (read)]

                (swap! alimentos_user conj [opcao_alimento quantidade_alimento])

                (println "\n  Registro salvo com sucesso!\n")

                )
              )
            )
      )
    )
  )


(defn menu_ativ_fis []

  (let [header (str "\n   ________________________________\n"
                    "  | NutriApp: Registro ativ. Fisi. |\n"
                    "  |________________________________|\n"
                    "   ----- Opcoes disponiveis ------\n\n")

        body (clojure.string/join "\n" (map-indexed (fn [i ativ_fis] (str "  " (inc i) " - " ativ_fis)) ativ_fis))

        footer (str "\n\n  0 - Voltar ao menu anterior\n\n  Escolha uma opcao: ")]

    (print (str header body footer))

    (flush)

    (let [opcao_ativ (read)]

      (cond (= opcao_ativ 0) (println "\n  Voltando ao menu anterior...\n")

            (> opcao_ativ (count ativ_fis)) (println "\n  Opcao invalida... Tente novamente!\n")

            :else

            (do

              (print "  Informe a duracao da atividade fisica em minutos: ")

              (flush)

              (let [tempo_ativ (read)]

                (swap! ativ_user conj [opcao_ativ tempo_ativ])

                (println "\n  Registro salvo com sucesso!\n")
                )
              )
            )
      )
    )
  )


(defn gerar_relatorio []

  (println @alimentos_user)

  (println @ativ_user)

  )


(defn menu_acao [opcao]

  (cond (= opcao 1) (menu_alimentos)

        (= opcao 2) (menu_ativ_fis)

        (= opcao 3) (gerar_relatorio)

        :else
        (str "\nOpcao invalida... Tente novamente!")

        )
  )


(defn menu-recursivo []

  (menu_geral)

  (flush)

  (let [opcao (read)]

    (if (= opcao 4)

      (println "\n  Encerrando o programa...")

      (do

        (menu_acao opcao)

        (recur)

        )
      )
    )
  )

(defn -main []
  (menu-recursivo)
  )
