;; HANDLER COM API USDA (GOVERNO AMERICANO)
(ns api-nutri-app.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clj-http.client :as http]
            [cheshire.core :as json]
            [environ.core :refer [env]]))

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
           (GET "/" [] "Hello World")
           (GET "/usda" [alimento]
             {:status 200
              :headers {"Content-Type" "application/json"}
              :body (json/encode (buscar-usda alimento))})
           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
