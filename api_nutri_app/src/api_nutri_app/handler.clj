;; HANDLER COM API USDA (GOVERNO AMERICANO)
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

(def api-key-usda (env :usda-api-key))
(def api-key-ninja (env :ninjas-api-key))


(defn traduzir-alimento [texto de para]
  (let [url "http://localhost:5000/translate"
        body {:q texto
              :source de
              :target para
              :format "text"}
        response (http/post url {:body (json/encode body)
                                 :headers {"Content-Type" "application/json"}
                                 :as :json})]
    (:translatedText (:body response)))
)


(defn kcal-ajustado [gramas-usuario kcal-usda]
  (/ (* gramas-usuario kcal-usda) 100)
)


(defn buscar-usda [alimento]
  (let [url "https://api.nal.usda.gov/fdc/v1/foods/search"
        params {:query alimento
                :dataType ["Survey (FNDDS)"]
                :pageSize 10
                :api_key api-key-usda}
        response (http/get url {:query-params params :as :json})
        items (get-in response [:body :foods])]
    (mapv (fn [item]
            (let [energia (some #(when (= "Energy" (:nutrientName %))
                                   (:value %))
                                (:foodNutrients item))
                  descricao-pt (traduzir-alimento (:description item) "en" "pt")]
              {:descricao descricao-pt
               :energia-kcal energia}))
          items))
)


(defn buscar-calorias
  [atividade & {:keys [weight duration]}]
  (let [url "https://api.api-ninjas.com/v1/caloriesburned"
        query-params (cond-> {"activity" atividade}
                       weight (assoc "weight" weight)
                       duration (assoc "duration" duration))]
    (try
      (let [response (http/get url
                               {:headers {"X-Api-Key" api-key-ninja}
                                :query-params query-params
                                :as :json})]
        (:body response))
      (catch Exception e
        (println "Erro ao buscar calorias:" (.getMessage e))
        ;; Retorne nil ou um mapa de erro para ser tratado pela rota
        nil)))
)


(defn buscar-resultados [alimento]
  (buscar-usda (traduzir-alimento alimento "pt" "en"))
)


(defroutes app-routes
  
  (GET "/" [] "Nutri App by Arthur & Guilherme")
 

  (GET "/usda" [alimento]
       {:status 200
        :headers {"Content-Type" "application/json"}
        :body (json/encode (buscar-resultados alimento))}
  )


  (GET "/contem/user" []

             (if (empty? @dados_user)

               {:status 200
                :headers {"Content-Type" "application/json"}
                :body (json/encode {:vazio? true})}

               {:status 200
                :headers {"Content-Type" "application/json"}
                :body (json/encode {:vazio? false})})
  )


  (GET "/calorias" {{:keys [atividade weight duration]} :params}
    (let [;; Converte weight e duration para números, se existirem
          parsed-weight (when weight (Integer/parseInt weight))
          parsed-duration (when duration (Integer/parseInt duration))

          ;; Passa os argumentos opcionais para buscar-calorias
          calorias (buscar-calorias atividade
                                    :weight parsed-weight
                                    :duration parsed-duration)]
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/encode calorias)})
  )


  (POST "/traduzir" request
      (let [params (json/decode (slurp (:body request)) true)
            texto (:texto params)
            de    (:de params)
            para  (:para params)]
        {:status 200
         :headers {"Content-Type" "application/json"}
         :body (json/encode (traduzir-alimento texto de para))})
  )


  (POST "/registro/user" req

    (let [dados (json/decode (slurp (:body req)) true)]
      (swap! dados_user conj dados)
      (println "Dados de usuário recebidos:" dados)
      {:status 200 :body "Dados registrados!"})
  )

  (POST "/registro/alimento" req
    (let [dados (json/decode (slurp (:body req)) true)]
      (swap! alimentos_armazenados conj dados)
      (println "Alimento recebido:" dados)
      {:status 200 :body "Alimento registrado!"})
  )

  (POST "/registro/atividade" req
    (let [dados (json/decode (slurp (:body req)) true)]
      (swap! atividades_armazenadas conj dados)
      (println "Atividade recebida:" dados)
      {:status 200 :body "Atividade registrada!"})
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
  (wrap-defaults app-routes site-defaults)
)

