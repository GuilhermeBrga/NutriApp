(ns api-nutri-app.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clj-http.client :as http]
            [cheshire.core :as json]
            [environ.core :refer [env]]))

(def dados_user (atom []))
(def alimentos_armazenados (atom []))
(def atividades_armazenadas (atom []))

(def api-key (env :usda-api-key))

(defn buscar-usda [alimento]

  (let [url "https://api.nal.usda.gov/fdc/v1/foods/search"

        params {:query alimento
                :pageSize 1
                :api_key api-key}

        response (http/get url {:query-params params :as :json})

        items (get-in response [:body :foods])]

    (mapv (fn [item]
            {:descricao (:description item)
             :gramas    (:servingSize item)
             :energia-kcal (get-in item [:foodNutrients 0 :value])})
          items)))

(defroutes app-routes

           (GET "/" [] "NutriApp API Online!")

           (GET "/usda" [alimento]
             {:status 200
              :headers {"Content-Type" "application/json"}
              :body (json/encode (buscar-usda alimento))})

           (GET "/comtem/user" []

             (if (empty? @dados_user)

               {:status 200
                :headers {"Content-Type" "application/json"}
                :body (json/encode {:vazio? true})}

               {:status 200
                :headers {"Content-Type" "application/json"}
                :body (json/encode {:vazio? false})}))


           (POST "/registro/user" req

             (let [dados (json/decode (slurp (:body req)) true)]

               (swap! dados_user conj dados)

               (println "Dados de usuário recebidos:" dados)

               {:status 200 :body "Dados registrados!"}))

           (POST "/registro/alimento" req

             (let [dados (json/decode (slurp (:body req)) true)]

               (swap! alimentos_armazenados conj dados)

               (println "Alimento recebido:" dados)

               {:status 200 :body "Alimento registrado!"}))

           (POST "/registro/atividade" req

             (let [dados (json/decode (slurp (:body req)) true)]

               (swap! atividades_armazenadas conj dados)

               (println "Atividade recebida:" dados)

               {:status 200 :body "Atividade registrada!"}))

           (GET "/dados" []

             {:status 200

              :headers {"Content-Type" "application/json"}

              :body (json/encode {:alimentos @alimentos_armazenados
                                  :atividades @atividades_armazenadas
                                  :dados @dados_user})})

           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes (assoc site-defaults :security {:anti-forgery false})))
