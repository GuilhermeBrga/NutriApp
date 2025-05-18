;; HANDLER INICIAL
;; (ns api-nutri-app.handler
;;   (:require [compojure.core :refer :all]
;;             [compojure.route :as route]
;;             [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

;; (defroutes app-routes
;;   (GET "/" [] "Hello World")
;;   (route/not-found "Not Found"))

;; (def app
;;   (wrap-defaults app-routes site-defaults))



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
                :pageSize 5
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



;; HANDLER COM API OPENFOODSFACTS (TEM EM PORTUGUES)
;; (ns api-nutri-app.handler
;;   (:require [compojure.core :refer :all]
;;             [compojure.route :as route]
;;             [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
;;             [clj-http.client :as http]
;;             [cheshire.core :as json]))

;; (defn buscar-openfoodfacts [nome]
;;   (let [url "https://world.openfoodfacts.org/cgi/search.pl"
;;         params {:search_terms nome
;;                 :search_simple 1
;;                 :action "process"
;;                 :json 1
;;                 :lang "pt"}
;;         response (http/get url {:query-params params :as :json})
;;         produtos (get-in response [:body :products])]
;;     (->> produtos
;;          (filter #(or (:product_name_pt %) (:product_name %)))
;;          (map (fn [p]
;;                 {:nome         (or (:product_name_pt p) (:product_name p))
;;                  :marca        (:brands p)
;;                  :quantidade   (:quantity p)
;;                  :porcao       (:serving_size p)
;;                  :imagem       (:image_url p) ; <-- aqui está a imagem!
;;                  :energia-kcal (get-in p [:nutriments :energy-kcal_100g])
;;                  :proteinas    (get-in p [:nutriments :proteins_100g])
;;                  :carboidratos (get-in p [:nutriments :carbohydrates_100g])
;;                  :gorduras     (get-in p [:nutriments :fat_100g])}))
;;          (take 5))))

;; (defroutes app-routes
;;   (GET "/" [] "Hello World")
;;   (GET "/alimento" [nome]
;;        {:status 200
;;         :headers {"Content-Type" "application/json"}
;;         :body (json/encode (buscar-openfoodfacts nome))})
;;   (route/not-found "Not Found"))

;; (def app
;;   (wrap-defaults app-routes site-defaults))

