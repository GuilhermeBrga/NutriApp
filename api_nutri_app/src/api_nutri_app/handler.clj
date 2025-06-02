(ns api-nutri-app.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clj-http.client :as http]
            [cheshire.core :as json]
            [environ.core :refer [env]])
  )

(def dados_user (atom []))
(def alimentos_armazenados (atom []))
(def atividades_armazenadas (atom []))

(def api-key (env :usda-api-key))

(defn traduzir-alimento [texto de para]

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


(defn buscar-usda [alimento]

  (let [url "https://api.nal.usda.gov/fdc/v1/foods/search"

        params {:query alimento
                :dataType ["Survey (FNDDS)"]
                :pageSize 10
                :api_key api-key}

        response (http/get url {:query-params params :as :json})

        items (get-in response [:body :foods])]

    (mapv (fn [item]

            (let [energia (some #(when (= "Energy" (:nutrientName %))
                                   (:value %))
                                (:foodNutrients item))

                  descricao-pt (traduzir-alimento (:description item) "en" "pt")]

              {:descricao descricao-pt
               :energia-kcal energia}))

          items)
    )
  )



(defn buscar-resultados [alimento]

  (buscar-usda (traduzir-alimento alimento "pt" "en"))

  )


(defroutes app-routes

           (GET "/" [] "Nutri App by Arthur & Guilherme")

           (POST "/traduzir" request

             (let [params (json/decode (slurp (:body request)) true)
                   texto (:texto params)
                   de    (:de params)
                   para  (:para params)]

               {:status 200
                :headers {"Content-Type" "application/json"}
                :body (json/encode (traduzir-alimento texto de para))}

               )
             )

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

           (GET "/usda" [alimento]
             {:status 200
              :headers {"Content-Type" "application/json"}
              :body (json/encode (buscar-resultados alimento))}
             )


           (POST "/registro/alimento" req

             (let [dados (json/decode (slurp (:body req)) true)]

               (swap! alimentos_armazenados conj dados)

               (println "Alimento recebido:" dados)

               {:status 200 :body "Alimento registrado!"}

               )
             )

           (POST "/registro/atividade" req

             (let [dados (json/decode (slurp (:body req)) true)]

               (swap! atividades_armazenadas conj dados)

               (println "Atividade recebida:" dados)

               {:status 200
                :body "Atividade registrada!"}

               )
             )

           (GET "/dados" []

             {:status 200

              :headers {"Content-Type" "application/json"}

              :body (json/encode {:alimentos @alimentos_armazenados
                                  :atividades @atividades_armazenadas
                                  :dados @dados_user})}

             )


           (route/not-found "Not Found")

           )

(def app

  (wrap-defaults app-routes (assoc site-defaults :security {:anti-forgery false}))

  )
