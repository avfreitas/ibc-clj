 (ns ibc-clj.core 
  (:gen-class)
  (:require  [compojure.core :refer [defroutes GET POST DELETE PUT OPTIONS ]]
             [compojure.route :as route]
             [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
             [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
             [ring.middleware.cors :refer [wrap-cors]]
             [cheshire.core :as json]
             [next.jdbc :as jdbc]
             [next.jdbc.sql :as sql]))
   
;;-----------------------------------------------------------------
;;-- Configuração do banco de dados  ------------------------------
;;-----------------------------------------------------------------
;;(def db-spec {:dbtype "mysql"
;;              :dbname "ibc_localhost"
;;              :host "localhost"
;;              :port 3307
;;              :user "root"
;;              :password "maua"})

(def db-spec {:dbtype "mysql"
              :dbname "qualitsys_ibc_desenv"
              :host "localhost"
              :port 3306
              :user "qualitsys_ibc_dev"
              :password "Ub{+hdAUeC$m"})

(def ds (jdbc/get-datasource db-spec))

;;-----------------------------------------------------------------
;;-- Handlers -----------------------------------------------------
;;----------------------------------------------------------------- 
(defn get-contact-type-handler [request]
  (let [id (Integer/parseInt (get-in request [:params :id]))
        result (sql/find-by-keys ds :contact_types {:id id})]
    (if (seq result)
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string (first result))}
      {:status 404
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string {:error "Contact type não encontrado."})})))

;----------------------------------------------------------------- 
(defn list-contact-types-handler [] 
  (let [result (sql/query ds ["SELECT * FROM contact_types"])]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string result)}))

;;----------------------------------------------------------------- 
(defn insert-contact-type-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))
        description (get params "description")
        priority (get params "priority")
        status (get params "status")
        created_at (java.time.LocalDateTime/now)
        
        ;;created_by (Integer. (get params "created_by"))
        created_by (get params "created_by") 

        updated_at (java.time.LocalDateTime/now)
        
        ;;updated_by (Integer. (get params "updated_by"))
        updated_by (get params "updated_by")]
    
   (try
        (sql/insert! ds :contact_types
                    {:id id
                     :description description
                     :priority priority
                     :status status
                     :created_at created_at
                     :created_by created_by
                     :updated_at updated_at
                     :updated_by updated_by})
      
      {:status 201
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string {:message "Contact-type criado com sucesso!"})}
      
      (catch Exception e
        {:status 500
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:error (str "Erro ao inserir um Contact-type: " (.getMessage e))})}))))

;;----------------------------------------------------------------- 
(defn delete-contact-type-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))]

   (try
     (let [existing (sql/get-by-id ds :contact_types id)]
       (if existing
         (do

           (sql/delete! ds :contact_types ["id = ?" id])
           {:status 200
            :headers {"Content-Type" "application/json"}
            :body (json/generate-string {:message "Contact-type deletado com sucesso!"})})

           {:status 404
            :headers {"Content-Type" "application/json"}
            :body (json/generate-string {:error "Contact-type não encontrado."})}))

     (catch Exception e
       {:status 500
        :headers {"Content-Type" "application/json"}
        :body (json/generate-string {:error (str "Erro ao deletar o Contact-type: " (.getMessage e))})}))))

;;----------------------------------------------------------------- 
(defn update-contact-type-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))
        description (get params "description")
        priority (get params "priority")
        status (get params "status")
        updated_at (java.time.LocalDateTime/now)
        updated_by (get params "updated_by")]
    (try
      (let [existing (sql/find-by-keys ds :contact_types {:id id})]
        (if (seq existing)
          (do
            (sql/update! ds :contact_types
                         {:description description
                          :priority priority
                          :status status
                          :updated_at updated_at
                          :updated_by updated_by}
                         ["id = ?" id])
            {:status 200
             :headers {"Content-Type" "application/json"}
             :body (json/generate-string {:message "Contact-type atualizado com sucesso!"})})
          {:status 404
           :headers {"Content-Type" "application/json"}
           :body (json/generate-string {:error "Contact-type não encontrado."})}))
      (catch Exception e
        {:status 500
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:error (str "Erro ao atualizar o Contact-type: " (.getMessage e))})}))))

;;---------------------------------------------------------------------
;; -- Endpoints   -----------------------------------------------------
;;---------------------------------------------------------------------
(defroutes app-routes
  (GET "/hello" [] "Hello, World")
  (GET "/greet" [] "HELLO IBC")
  (GET "/contact-type/:id" [id] (get-contact-type-handler {:params {:id id}}))
  (GET "/list-contact-types"  []  (list-contact-types-handler ))
  (POST "/ins-contact-type" request (insert-contact-type-handler request))
  (DELETE "/del-contact-type" request (delete-contact-type-handler request))
  (PUT "/upd-contact-type" request (update-contact-type-handler request)) 
 
  (route/not-found "Rota não definida!"))

;;---------------------------------------------------------------------
;; --- Middleware  ----------------------------------------------------
;;---------------------------------------------------------------------
(def app
  (-> (wrap-cors app-routes
       ;; :access-control-allow-origin [#"http://127.0.0.1:9999"         ;; Apache localhost
       ;;                               #"http://127.0.0.1:8080"         ;; http-server localhost
       ;;                               #"http://www.qualitsys.net.br"]  ;;Centos VPS Linux          


      :access-control-allow-origin [#"http://www.qualitsys.net.br"]
      :access-control-allow-methods [:get :post :put :delete]
      :access-control-allow-headers ["Content-Type" "X-CSRF-Token"])
      
      (wrap-json-body {:keywords? false})
      (wrap-json-response)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))
;;---------------------------------------------------------------------
(defn -main []
  (println "Iniciando o servidor..."))
;;---------------------------------------------------------------------