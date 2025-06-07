(ns api-nutri-app.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clj-http.client :as http]
            [cheshire.core :as json]
            [environ.core :refer [env]])
  )

(import '[java.time LocalDate]
        '[java.time.format DateTimeFormatter])

(def dados_user (atom []))
(def alimentos_user (atom []))
(def atividades_user (atom []))
(def datas_relatorio (atom []))

(def api_key_usda (env :usda-api-key))
(def api_key_ninja (env :ninjas-api-key))

(defn traduzir_resultados [texto de para]

  (let [url "http://localhost:5000/translate"

        body {:q texto
              :source de
              :target para
              :format "text"}

        response (http/post url {:body (json/encode body)
                                 :headers {"Content-Type" "application/json"}
                                 :as :json})]

    (:translatedText (:body response))
    )
  )


(defn buscar_alimento [alimento]
  (let [url "https://api.nal.usda.gov/fdc/v1/foods/search"
        params {:query alimento
                :dataType ["Survey (FNDDS)"]
                :pageSize 10
                :api_key api_key_usda}
        response (http/get url {:query-params params :as :json})
        items (get-in response [:body :foods])]
    (mapv (fn [item]
            (let [energia (some #(when (= "Energy" (:nutrientName %))
                                   (:value %))
                                (:foodNutrients item))
                  descricao_pt (traduzir_resultados (:description item) "en" "pt")]
              {:descricao descricao_pt
               :energia-kcal energia}))
          items)
    )
  )

(defn buscar_resultados_alimento [alimento]
  (buscar_alimento (traduzir_resultados alimento "pt" "en"))
)

(defn calorias_min [cal_hora]
  (float (/ cal_hora 60))
)


(defn buscar_atividade_fisica [atividade peso]
  (let [url "https://api.api-ninjas.com/v1/caloriesburned"
        query_params {"activity" atividade
                      "weight" (str peso)}]
    (try
      (let [response (http/get url
                               {:headers {"X-Api-Key" api_key_ninja}
                                :query-params query_params
                                :as :json})
            dados (:body response)]

        (mapv (fn [item]
                (let [descricao_pt (traduzir_resultados (:name item) "en" "pt")]
                  {:descricao descricao_pt
                   :energia-kcal (calorias_min (:calories_per_hour item))}))
              dados))

      (catch Exception e
        (println "Erro ao buscar calorias:" (.getMessage e)))))
)


;; peso em libras
(defn buscar_resultados_atividade_fisica [exercicio]
  (buscar_atividade_fisica 
  (traduzir_resultados exercicio "pt" "en") 
  (* (:peso (first @dados_user)) 2.20462)
  )
)



(defroutes app-routes

           (GET "/" [] "Nutri App by Arthur & Guilherme")

           (GET "/contem/user" []
             (if (empty? @dados_user)
               
               {:status 200
                :headers {"Content-Type" "application/json"}
                :body (json/encode {:vazio? true})}

               {:status 200
                :headers {"Content-Type" "application/json"}
                :body (json/encode {:vazio? false})}
               )
             )

           (POST "/registro/user" req
             (let [dados (json/decode (slurp (:body req)) true)]
               (swap! dados_user conj dados)
               (println "Dados de usuário recebidos:" dados)
               {:status 200 :body "Dados registrados!"}
               )
             )

           (POST "/traduzir" request
             (let [params (json/decode (slurp (:body request)) true)
                   texto (:texto params)
                   de    (:de params)
                   para  (:para params)]

               {:status 200
                :headers {"Content-Type" "application/json"}
                :body (json/encode (traduzir_resultados texto de para))}
               )
             )


           (GET "/buscar/alimento" [alimento]
             {:status 200
              :headers {"Content-Type" "application/json"}
              :body (json/encode (buscar_resultados_alimento alimento))}
             )


           (POST "/registro/alimento" req
             (let [dados (json/decode (slurp (:body req)) true)]
               (swap! alimentos_user conj dados)
               (println "Alimento recebido:" dados)
               {:status 200
                :body "Alimento registrado!"}
               )
             )

           (GET "/buscar/atividade" [atividade]
             (let []
               {:status 200
                :headers {"Content-Type" "application/json"}
                :body (json/encode (buscar_resultados_atividade_fisica atividade))}
               )
             )


           (POST "/registro/atividade" req
             (let [dados (json/decode (slurp (:body req)) true)]
               (swap! atividades_user conj dados)
               (println "Atividade recebida:" dados)
               {:status 200
                :body "Atividade registrada!"}
               )
             )

           (GET "/dados" []
             {:status 200
              :headers {"Content-Type" "application/json"}
              :body (json/encode {:alimentos @alimentos_user
                                  :atividades @atividades_user
                                  :dados @dados_user})}
             )

           (POST "/datas/relatorio" req
             (let [dados (json/decode (slurp (:body req)) true)]
               (reset! datas_relatorio [dados])
               (println "Datas do relatorio recebidas:" dados)
               {:status 200
                :body "Datas registradas!"}
               )
             )


           (GET "/dados/user" []
             {:status 200
              :headers {"Content-Type" "application/json"}
              :body (json/encode @dados_user)}
             )

           
           (GET "/dados/alimento" []
             (let [formatter (DateTimeFormatter/ofPattern "dd/MM/yyyy")
                   {:keys [inicio fim]} (first @datas_relatorio)
                   inicio_ts (.toEpochDay (LocalDate/parse inicio formatter))
                   fim_ts    (.toEpochDay (LocalDate/parse fim formatter))
                   alimentos_filtrados (filter
                                         (fn [{:keys [dataConsumo]}]
                                           (let [data_ts (.toEpochDay (LocalDate/parse dataConsumo formatter))]
                                             (<= inicio_ts data_ts fim_ts)))
                                         @alimentos_user)]

               {:status 200
                :headers {"Content-Type" "application/json"}
                :body (json/encode alimentos_filtrados)}))

           
           (GET "/dados/ativ_fis" []
             (let [formatter (DateTimeFormatter/ofPattern "dd/MM/yyyy")
                   {:keys [inicio fim]} (first @datas_relatorio)
                   inicio_ts (.toEpochDay (LocalDate/parse inicio formatter))
                   fim_ts    (.toEpochDay (LocalDate/parse fim formatter))
                   ativ_fis_filtrados (filter
                                        (fn [{:keys [dataAtividade]}]
                                           (let [data_ts (.toEpochDay (LocalDate/parse dataAtividade formatter))]
                                             (<= inicio_ts data_ts fim_ts)))
                                         @atividades_user)]

               {:status 200
                :headers {"Content-Type" "application/json"}
                :body (json/encode ativ_fis_filtrados)}))

           
            (GET "/dados/calorias" []
             (try
               (let [formatter (DateTimeFormatter/ofPattern "dd/MM/yyyy")
                     {:keys [inicio fim]} (first @datas_relatorio)
                     inicio_ts (.toEpochDay (LocalDate/parse inicio formatter))
                     fim_ts    (.toEpochDay (LocalDate/parse fim formatter))

                     alimentos_filtrados (filter
                                           (fn [{:keys [dataConsumo]}]
                                             (let [data_ts (.toEpochDay (LocalDate/parse dataConsumo formatter))]
                                               (<= inicio_ts data_ts fim_ts)))
                                           @alimentos_user)

                     ativ_fis_filtrados (filter
                                          (fn [{:keys [dataAtividade]}]
                                            (let [data_ts (.toEpochDay (LocalDate/parse dataAtividade formatter))]
                                              (<= inicio_ts data_ts fim_ts)))
                                          @atividades_user)

                     calorias_ganhas (reduce + (map #(:kcal %) alimentos_filtrados))
                     calorias_gastas (reduce + (map #(:kcal %) ativ_fis_filtrados))

                     saldo_calorico (- calorias_ganhas calorias_gastas)]


                 {:status 200
                  :headers {"Content-Type" "application/json"}
                  :body (json/encode {:calorias_consumidas calorias_ganhas
                                      :calorias_gastas     calorias_gastas
                                      :saldo_calorico      saldo_calorico})}))
            )

           (route/not-found "Not Found")

           )

(def app
  (wrap-defaults app-routes (assoc site-defaults :security {:anti-forgery false}))
  )
