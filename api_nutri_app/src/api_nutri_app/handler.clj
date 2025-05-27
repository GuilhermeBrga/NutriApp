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

(def api-key (env :usda-api-key))

;; (def libretranslate-url "https://libretranslate.de/translate")

;; (defn buscar-usda [alimento]
;;   (let [url "https://api.nal.usda.gov/fdc/v1/foods/search"
;;         params {:query alimento ; alimento pesquisado, ex.: "banana"
;;                 :pageSize 10 ; quantos alimentos diferentes eu quero que retorne
;;                 :api_key api-key} ; chave api
;;         response (http/get url {:query-params params :as :json})
;;         items (get-in response [:body :foods])]
;;     (mapv (fn [item]
;;             {:descricao (:description item)
;;              :gramas    (:servingSize item)
;;              :energia-kcal (get-in item [:foodNutrients 0 :value])})
;;           items)))


(defn kcal-ajustado [gramas-usuario gramas-usda kcal-usda]
  (/ (* gramas-usuario kcal-usda) gramas-usda) ;; regra de 3
)


;; (defn traduzir
;;   [texto origem destino]
;;   (let [response (http/post libretranslate-url
;;                             {:headers {"Content-Type" "application/json"}
;;                              :body (json/encode {:q texto
;;                                                  :source origem
;;                                                  :target destino
;;                                                  :format "text"})
;;                              :as :json})]
;;     (get-in response [:body :translatedText])))



(defn buscar-usda [alimento gramas-usuario]
  (let [url "https://api.nal.usda.gov/fdc/v1/foods/search"
        params {:query alimento ; alimento pesquisado, ex.: "banana"
                :pageSize 1 ; quantos alimentos diferentes eu quero que retorne
                :api_key api-key} ; chave api
        response (http/get url {:query-params params :as :json})
        items (get-in response [:body :foods])]
    (mapv (fn [item]
            {:descricao (:description item)
             :gramas    gramas-usuario ;(:servingSize item)
             :energia-kcal (if (not (nil? (:servingSize item))) 
             (kcal-ajustado gramas-usuario (:servingSize item) (get-in item [:foodNutrients 0 :value])) 
             (kcal-ajustado gramas-usuario 100 (get-in item [:foodNutrients 0 :value])))
             }) items)
    )
)
;; testar com seaweed (alga), pq não ta dando certo


(defn busca-resultados [alimento]
  ; definir como que eu vou pegar a variável gramas-usuario (substituirá o 200 abaixo)
  (buscar-usda alimento 200)
)


(defroutes app-routes
  
  (GET "/" [] "Nutri App by Arthur e Guilherme")
 
  (GET "/usda" [alimento]
       {:status 200
        :headers {"Content-Type" "application/json"}
        :body (json/encode (busca-resultados alimento))}
  )

  ;; (GET "/traduzir" [texto origem destino]
  ;;      (let [traducao (traduzir texto origem destino)]
  ;;        {:status 200
  ;;         :headers {"Content-Type" "application/json"}
  ;;         :body (json/encode {:traduzido traducao})})
  ;; )
  
  (route/not-found "Not Found")

)

;; (def app
;;   (wrap-defaults app-routes site-defaults)
;; )


(def app
  (wrap-defaults app-routes
                 (assoc-in site-defaults [:security :anti-forgery] false)))


;; fazer tratamento de visualização:
;; se gramas = null e energia-kcal maior que zero
;; então considere gramas = 100g (deu bom pra um exemplo, deve dar pra tudim)

;; se gramas e energia-kcal são nulos, não exiba esse resultado


; exemplo
; 100g --- 450kcal
; 210g --- x kcal

; x = (210 * 450)/100

; usar o LibreTranslate -> verificar como funciona para configurar